package com.sfedu.touragency.dispatcher;

import com.sfedu.touragency.domain.Role;
import com.sfedu.touragency.filter.StaticResourceFilter;
import com.sfedu.touragency.security.SecurityContext;

import javax.servlet.*;
import java.util.*;

/**
 * A more user friendly way of creating and registering a {@link ControllerDispatcherServlet}
 */
public class ControllerDispatcherServletBuilder {
    private final ServletContext servletContext;

    private List<ControllerDispatcherServlet.MatcherEntry> matchers
            = new ArrayList<>();

    public ControllerDispatcherServletBuilder(ServletContext sc) {
        this.servletContext = sc;
    }

    public SecurityRoleConfigurer withSecurity(String url, Controller controller) {
        ControllerDispatcherServlet.MatcherEntry matcherEntry =
                new ControllerDispatcherServlet.MatcherEntry(url, controller);

        matchers.add(matcherEntry);

        return new SecurityRoleConfigurer(url);
    }

    public ControllerDispatcherServletBuilder addMapping(String url, Controller controller) {
        ControllerDispatcherServlet.MatcherEntry matcherEntry =
                new ControllerDispatcherServlet.MatcherEntry(url, controller);

        matchers.add(matcherEntry);

        return this;
    }

    public ControllerDispatcherServletBuilder reset() {
        matchers.clear();
        return this;
    }

    public ControllerDispatcherServlet build() {
        ControllerDispatcherServlet dispatcherServlet = new ControllerDispatcherServlet();

        for (ControllerDispatcherServlet.MatcherEntry entry : matchers) {
            dispatcherServlet.addMapping(entry);
        }

        return dispatcherServlet;
    }

    public ControllerDispatcherServlet buildAndRegister(String name, String mapping) {
        ControllerDispatcherServlet servlet = build();

        ServletRegistration.Dynamic dynamic = servletContext.addServlet(name, servlet);
        dynamic.setMultipartConfig(new MultipartConfigElement(""));
        dynamic.addMapping(mapping);

        StaticResourceFilter filter =
                new StaticResourceFilter(mapping.replace("/*", ""));
        filter.ignore("/image-provider");

        FilterRegistration.Dynamic filterDynamic =
                servletContext.addFilter("Static Resource Filter", filter);
        filterDynamic.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD), true, "/*");

        return servlet;
    }

    public class SecurityRoleConfigurer {
        private String path;

        private SecurityRoleConfigurer(String path) {
            this.path = path;
        }

        public SecurityMethodConfigurer roles(Role... role) {
            return new SecurityMethodConfigurer(role, path);
        }

        public SecurityMethodConfigurer authorized() {
            return new SecurityMethodConfigurer(new Role[]{}, path);
        }
    }

    public class SecurityMethodConfigurer {
        private Role[] roles;
        private String path;

        private SecurityMethodConfigurer(Role[] roles, String path) {
            this.roles = roles;
            this.path = path;
        }

        public ControllerDispatcherServletBuilder allow(HttpMethod... methods) {
            SecurityContext.INSTANCE.addSecurityConstraint(path,
                    HttpMethod.combine(methods), roles);
            return ControllerDispatcherServletBuilder.this;
        }

        public ControllerDispatcherServletBuilder modifying() {
            return allow(HttpMethod.DELETE, HttpMethod.POST, HttpMethod.PUT);
        }

        public ControllerDispatcherServletBuilder permitAll() {
            SecurityContext.INSTANCE.addSecurityConstraint(path,
                    HttpMethod.any(), roles);
            return ControllerDispatcherServletBuilder.this;
        }
    }

}
