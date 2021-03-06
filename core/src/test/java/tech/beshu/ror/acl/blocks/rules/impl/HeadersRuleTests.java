/*
 *    This file is part of ReadonlyREST.
 *
 *    ReadonlyREST is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    ReadonlyREST is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with ReadonlyREST.  If not, see http://www.gnu.org/licenses/
 */

package tech.beshu.ror.acl.blocks.rules.impl;

import com.google.common.collect.Sets;
import org.junit.Test;
import org.mockito.Mockito;
import tech.beshu.ror.acl.blocks.rules.RuleExitResult;
import tech.beshu.ror.acl.blocks.rules.SyncRule;
import tech.beshu.ror.requestcontext.RequestContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Created by sscarduzio on 23/05/2018.
 */

public class HeadersRuleTests {

  @Test
  public void testSimpleHeaderMatch() {
    RuleExitResult res = match(Collections.singletonList("hkey:hvalue"), new HashMap() {{
      put("hkey", "hvalue");
    }});
    assertTrue(res.isMatch());
  }

  @Test
  public void testHeaderMultiSeparator() {
    RuleExitResult res = match(Collections.singletonList("hkey:hvalue:123"), new HashMap() {{
      put("hkey", "hvalue:123");
    }});
    assertTrue(res.isMatch());
  }

  @Test
  public void testHeaderCapitalSettingsKey() {
    RuleExitResult res = match(Collections.singletonList("Hkey:hvalue:123"), new HashMap() {{
      put("hkey", "hvalue:123");
    }});
    assertTrue(res.isMatch());
  }

  @Test
  public void testHeaderCapitalSettingsKeyValue() {
    RuleExitResult res = match(Collections.singletonList("Hkey:Hvalue:123"), new HashMap() {{
      put("hkey", "hvalue:123");
    }});
    assertTrue(res.isMatch());
  }

  @Test
  public void testHeaderCapitalHeaderKey() {
    RuleExitResult res = match(Collections.singletonList("hkey:hvalue:123"), new HashMap() {{
      put("Hkey", "hvalue:123");
    }});
    assertTrue(res.isMatch());
  }

  @Test
  public void testHeaderCapitalHeaderValue() {
    RuleExitResult res = match(Collections.singletonList("hkey:hvalue:123"), new HashMap() {{
      put("hkey", "Hvalue:123");
    }});
    assertFalse(res.isMatch());
  }

  private RuleExitResult match(List<String> configured, Map<String, String> found) {
    Map<String, String> foundCaseInsensitive = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    foundCaseInsensitive.putAll(found);
    return match(configured, foundCaseInsensitive, Mockito.mock(RequestContext.class));
  }

  private RuleExitResult match(List<String> configured, Map<String, String> found, RequestContext rc) {
    when(rc.getHeaders()).thenReturn(found);

    SyncRule r = new HeadersSyncRule(new HeadersSyncRule.Settings(Sets.newHashSet(configured)));
    return r.match(rc);
  }

}
