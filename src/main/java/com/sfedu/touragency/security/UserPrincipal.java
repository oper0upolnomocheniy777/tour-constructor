package com.sfedu.touragency.security;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * This class only for support of the 'native' security capabilities of
 * the {@link javax.servlet.http.HttpServletRequest}
 *
 * @see HttpServletRequest#getUserPrincipal()
 */
public class UserPrincipal implements Principal{
    private final String name;

    public UserPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
