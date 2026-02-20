package com.sfedu.touragency.security;


import com.sfedu.touragency.domain.User;
import com.sfedu.touragency.util.PasswordEncoder;
import org.apache.logging.log4j.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.security.*;

/**
 * A wrapper around HttpServletRequest that override methods related to
 * security, authorization and authentication
 *
 * @see SecurityContext
 * @see SecurityFilter
 */
public class SecuredHttpServletRequest extends HttpServletRequestWrapper {
    private static final Logger LOGGER = LogManager.getLogger(SecuredHttpServletRequest.class);

    private SecurityContext securityContext = SecurityContext.INSTANCE;

    SecuredHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    protected HttpServletRequest getHttpRequest() {
        return (HttpServletRequest) getRequest();
    }

    @Override
    public Principal getUserPrincipal() {
        User user = (User) getHttpRequest().getSession(false).getAttribute("user");
        if(user == null) {
            return null;
        }

        return new UserPrincipal(user.getId().toString());
    }

    @Override
    public boolean isUserInRole(String role) {
        User user = getCurrentUser();
        if(user == null) {
            return false;
        }

        // TODO: simplify this
        if (user.getRoles().stream().map(Enum::name).anyMatch(s -> s.equals(role))) {
            return true;
        }

        return false;
    }

    @Override
    public void login(String username, String password) throws ServletException {
        User user;

        user = securityContext.getUserDao().read(username);

        if (user != null && user.getPassword().equals(PasswordEncoder.encodePassword(password))) {
            getHttpRequest().getSession(true).setAttribute("user", user);
            getHttpRequest().getSession(false).setAttribute("loggedIn", true);
        } else {
            getHttpRequest().getSession().invalidate();
            throw new ServletException("Bad login credentials");
        }
    }

    @Override
    public void logout() throws ServletException {
        getSession().invalidate();
    }

    public User getCurrentUser() {
        return (User) getSession(true).getAttribute("user");
    }
}
