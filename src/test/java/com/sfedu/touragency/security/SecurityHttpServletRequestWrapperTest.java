package com.sfedu.touragency.security;

import com.sfedu.touragency.domain.Role;
import com.sfedu.touragency.domain.User;
import com.sfedu.touragency.persistence.dao.UserDao;
import com.sfedu.touragency.security.support.MockHttpSession;
import com.sfedu.touragency.util.PasswordEncoder;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SecurityHttpServletRequestWrapperTest {
    @Mock
    private UserDao userDao;

    @Mock
    private HttpServletRequest mockRequest;

    private User user;

    private User agent;

    private SecurityContext securityContext;

    private SecuredHttpServletRequest request;

    @Before
    public void setUp() throws Exception {
        SecurityContext.INSTANCE.reset();
        securityContext = SecurityContext.INSTANCE;
        securityContext.setUserDao(userDao);

        user = getUser();
        agent = getAgent();

        given(userDao.read("john_doe")).willReturn(user);
        given(userDao.read("jane_doe")).willReturn(agent);

        HttpSession session = new MockHttpSession();
        given(mockRequest.getSession()).willReturn(session);
        given(mockRequest.getSession(anyBoolean())).willReturn(session);

        request = new SecuredHttpServletRequest(mockRequest);
    }

    @Test
    public void testNoCurrentUser() {
        assertNull(request.getCurrentUser());
    }

    @Test
    public void testLogin() throws ServletException {
        request.login("john_doe", "passw");
        assertNotNull(request.getCurrentUser());
    }

    @Test(expected = ServletException.class)
    public void testLoginBadCredentials() throws ServletException {
        request.login("john_doe", "passr");
    }

    @Test
    public void testLogoutAfterLogin() throws ServletException {
        request.login("john_doe", "passw");
        request.logout();
        assertNull(request.getCurrentUser());
    }

    @Test
    public void testLogoutWithoutLogin() throws ServletException {
        request.logout();
        assertNull(request.getCurrentUser());
    }

    @Test
    public void testGetUserPrincipalAfterLogin() throws ServletException {
        request.login("john_doe", "passw");
        assertNotNull(request.getUserPrincipal());
        assertEquals("1", request.getUserPrincipal().getName());
    }

    @Test
    public void testGetUserPrincipalWithoutLogin() throws ServletException {
        assertNull(request.getUserPrincipal());
    }

    @Test
    public void testIsUserInRoleHit() throws ServletException {
        request.login("john_doe", "passw");
        assertTrue(request.isUserInRole(Role.CUSTOMER.name()));
    }

    @Test
    public void testIsUserInRoleMiss() throws ServletException {
        request.login("john_doe", "passw");
        assertFalse(request.isUserInRole(Role.TOUR_AGENT.name()));
    }

    @Test
    public void testIsUserInRoleHitWhenSeveralRoles() throws ServletException {
        request.login("jane_doe", "passw");
        assertTrue(request.isUserInRole(Role.TOUR_AGENT.name()));
        assertTrue(request.isUserInRole(Role.CUSTOMER.name()));
    }

    @Test
    public void testIsUserInRoleHitWhenSeveralRolesMiss() throws ServletException {
        request.login("jane_doe", "passw");
        assertFalse(request.isUserInRole("b"));
    }

    private static User getUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john_doe");
        user.setRoles(Arrays.asList(Role.CUSTOMER));
        user.setUsername("user");
        user.setDiscount(10);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword(PasswordEncoder.encodePassword("passw"));
        return user;
    }

    private static User getAgent() {
        User user = new User();
        user.setId(2L);
        user.setUsername("jane_doe");
        user.setRoles(Arrays.asList(Role.TOUR_AGENT, Role.CUSTOMER));
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setPassword(PasswordEncoder.encodePassword("passw"));
        return user;
    }
}
