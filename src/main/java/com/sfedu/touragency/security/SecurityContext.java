package com.sfedu.touragency.security;

import com.sfedu.touragency.dispatcher.HttpMethod;
import com.sfedu.touragency.domain.Role;
import com.sfedu.touragency.domain.User;
import com.sfedu.touragency.persistence.dao.UserDao;
import org.apache.logging.log4j.*;

import javax.servlet.http.*;
import java.util.*;
import java.util.concurrent.locks.*;
import java.util.regex.*;

/**
 * A singleton object for encapsulating user authorization and authentication
 * Allows adding security constrains to the web application
 */
public enum SecurityContext {
    INSTANCE;

    private static Logger LOGGER = LogManager.getLogger(SecurityContext.class);

    private static final String SESSION_USER = "user";

    private UserDao userDao;

    private String loginPage = "/login.html";

    private List<SecurityConstraint> securityConstraints = new ArrayList<>();

    private ReadWriteLock rwLock = new ReentrantReadWriteLock();

    /**
     * Allows access to URLs denoted by the s regular expression
     * for the given roles and denies for other users
     * @param s     regular expression which tested against the URL
     * @param roles roles that allowed to access the given set of pages;
     *              if null - only authenticated user can access the pages
     * @return  the same SecurityContext
     */
    public SecurityContext addSecurityConstraint(String s, HttpMethod.HttpMethodMask mask,
                                                 Role... roles) {
        rwLock.writeLock().lock();
        try {
            securityConstraints.add(new SecurityConstraint(s, mask.getMask(), roles));
            return this;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public SecurityContext addSecurityConstraint(String s, Role... roles) {
        return addSecurityConstraint(s, HttpMethod.any(), roles);
    }

    /**
     * Test if a user with the given roles can access the page
     * @param path  URI of the resource starting with /
     * @param roles roles of the user
     * @return true if user allowed to access the resource, false - otherwise
     */
    public boolean allowed(String path, HttpMethod method, List<Role> roles) {
        rwLock.readLock().lock();
        try {
            if (roles == null) {
                roles = new ArrayList<>();
            }

            for (SecurityConstraint sc : securityConstraints) {

                if (sc.matches(path, method)) {
                    return sc.allowed(roles);
                }
            }

            return true;

        } finally {
            rwLock.readLock().unlock();
        }
    }

    public String getLoginPage() {
        return loginPage;
    }

    public void setLoginPage(String loginPage) {
        this.loginPage = loginPage;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void updateUserCache(HttpServletRequest req) {
        Optional<User> maybeUser = getCurrentUser(req);
        if(maybeUser.isPresent()) {
            Long id = maybeUser.get().getId();
            User user = userDao.read(id);
            req.getSession().setAttribute(SESSION_USER, user);
        }
    }

    public Optional<User> getCurrentUser(HttpServletRequest req) {
        return Optional.ofNullable((User) req.getSession(true).getAttribute(SESSION_USER));
    }

    public void reset() {
        securityConstraints.clear();
        setUserDao(null);
    }


    /**
     * Represents a pair of regular expression and roles allowed to access the
     * set of pages(denoted by the expression)
     */
    private static class SecurityConstraint {
        private final Pattern pattern;
        private final Matcher matcher;
        private final List<Role> rolesAllowed = new ArrayList<>();
        private final int mask;

        public SecurityConstraint(String pathPattern, int mask, Role... rolesAllowed) {
            pattern = Pattern.compile("^" + pathPattern + "$");
            matcher = pattern.matcher("");
            this.rolesAllowed.addAll(Arrays.asList(rolesAllowed));
            this.mask = mask;
        }

        public boolean matches(String path, HttpMethod method) {
            matcher.reset(path);
            return method.matches(mask) && matcher.matches();
        }

        public boolean allowed(List<Role> roles) {
            if (rolesAllowed.isEmpty()) {
                return true;
            }

            for (Role role : roles) {
                if (rolesAllowed.contains(role)) {
                    return true;
                }
            }

            return false;
        }
    }
}
