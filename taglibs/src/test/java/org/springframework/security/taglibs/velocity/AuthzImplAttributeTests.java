/* Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.security.taglibs.velocity;

import junit.framework.TestCase;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.jsp.JspException;


public class AuthzImplAttributeTests extends TestCase {
    //~ Instance fields ================================================================================================

    private final Authz authz = new AuthzImpl();
    private TestingAuthenticationToken currentUser;

    //~ Methods ========================================================================================================

    protected void setUp() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("abc", "123", "ROLE_SUPERVISOR","ROLE_RESTRICTED"));
    }

    protected void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
    }

    public void testAssertsIfAllGrantedSecond() {
        boolean r1 = authz.allGranted("ROLE_SUPERVISOR,ROLE_SUPERTELLER");
        boolean r2 = authz.anyGranted("ROLE_RESTRICTED");

        //prevents request - principal is missing ROLE_SUPERTELLE
        assertFalse(r1 && r2);
    }

    public void testAssertsIfAnyGrantedLast() {
        boolean r2 = authz.anyGranted("ROLE_BANKER");

        // prevents request - principal is missing ROLE_BANKER
        assertFalse(r2);
    }

    public void testAssertsIfNotGrantedFirst() {
        boolean r1 = authz.allGranted("ROLE_SUPERVISOR,ROLE_RESTRICTED");
        boolean r2 = authz.noneGranted("ROLE_RESTRICTED");
        boolean r3 = authz.anyGranted("ROLE_SUPERVISOR");

        //prevents request - principal has ROLE_RESTRICTED
        assertFalse(r1 && r2 && r3);
    }

    public void testAssertsIfNotGrantedIgnoresWhitespaceInAttribute() {
        //allows request - principal has ROLE_SUPERVISOR
        assertTrue(authz.anyGranted("\tROLE_SUPERVISOR  \t, \r\n\t ROLE_TELLER "));
    }

    public void testIfAllGrantedIgnoresWhitespaceInAttribute() {
        //allows request - principal has ROLE_RESTRICTED and ROLE_SUPERVISOR
        assertTrue(authz.allGranted("\nROLE_SUPERVISOR\t,ROLE_RESTRICTED\t\n\r "));
    }

    public void testIfNotGrantedIgnoresWhitespaceInAttribute()
        throws JspException {
        //prevents request - principal does not have ROLE_TELLER
        assertFalse(authz.allGranted(" \t  ROLE_TELLER \r"));
    }
}
