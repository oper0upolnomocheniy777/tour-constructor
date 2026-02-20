package com.sfedu.touragency.dispatcher;

import com.sfedu.touragency.domain.User;
import com.sfedu.touragency.security.SecurityContext;
import com.sfedu.touragency.util.TryOptionalUtil;
import org.apache.logging.log4j.*;

import javax.servlet.http.*;
import java.util.*;

/**
 * A wrapper class that contains all the necessary information for
 * processing a request by {@link Controller} along with some
 * helper methods
 *
 * Note that {@link #redirect(String)} and {@link #renderPage(String)} has special treatment
 * by the ControllerDispatcherServlet
 */
public class RequestService {
    private static Logger LOGGER = LogManager.getLogger(RequestService.class);

    private HttpServletRequest request;

    private HttpServletResponse response;

    private List<String> groups;

    /**
     * Page that will be rendered as the result of the request
     */
    private String renderPage = null;

    /**
     * If not null than the dispatcher server will redirect the request to this path
     */
    private String redirectPath = null;

    public RequestService(HttpServletRequest request, HttpServletResponse response,
                          List<String> groups) {
        this.request = request;
        this.response = response;
        this.groups = groups;
    }

    /**
     * After the method invocation `val` will be available on the .jsp page by the name
     * `key`
     */
    public void setPageAttribute(String key, Object val) {
        request.setAttribute(key, val);
    }

    /**
     * If invoked than in the end of the request processing the user will be redirected
     * to the given page. Subsequent invocations override previous one
     *
     * Note that {@link #redirect(String)} and {@link #renderPage(String)} has special treatment
     * by the {@link ControllerDispatcherServlet}
     * @param where
     */
    public void redirect(String where) {
        redirectPath = where;
    }

    /**
     * If invoked than in the end of the request processing the specified path will be
     * rendered to the response. Subsequent invocations override previous one
     *
     * Note that {@link #redirect(String)} and {@link #renderPage(String)} has special treatment
     * by the {@link ControllerDispatcherServlet}
     **/
    public void renderPage(String path) {
        this.renderPage = path;
    }

    /**
     * Delegates to {@link HttpServletResponse#setStatus(int)}
     * @param status
     */
    public void setStatus(int status) {
        response.setStatus(status);
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    /**
     * Get request parameter(uri parameter or form data)
     */
    public Optional<String> getParameter(String parameter) {
        String s = request.getParameter(parameter);
        return Optional.ofNullable(s).filter(v -> !v.isEmpty());
    }

    /**
     * Get request parameter and tries to parse it to Long. If not parsable than
     * returns {@link Optional#EMPTY}
     */
    public Optional<Long> getLong(String param) {
        return getParameter(param)
                .flatMap((s) -> TryOptionalUtil.of(() -> Long.valueOf(s)));
    }

    /**
     * Get request parameter and tries to parse it to Integer. If not parsable than
     * returns {@link Optional#EMPTY}
     */
    public Optional<Integer> getInt(String param) {
        return getParameter(param)
                .flatMap((s) -> TryOptionalUtil.of(() -> Integer.valueOf(s)));
    }

    /**
     * Retrieves <b>param</b> request parameter as a non null String
     * @param param
     * @return a String representation of the request parameter or an empty string
     */
    public String getString(String param) {
        return getParameter(param).orElse("");
    }

    public Optional<Boolean> getBool(String param) {
        return getParameter(param)
                .flatMap((s) -> TryOptionalUtil.of(() -> s.equals("1") || s.equals(true)));
    }

    public String getRenderPage() {
        return renderPage;
    }

    public String getRedirectPath() {
        return redirectPath;
    }

    /**
     * Helper method for retrieving current user
     * @return a User or an Empty optional if there is no authorized one
     */
    public Optional<User> getUser() {
        return SecurityContext.INSTANCE.getCurrentUser(request);
    }

    /**
     * Helper method for retrieving current user directly from the database.
     * @return a User or an Empty optional if there is no authorized one
     */
    public Optional<User> loadUser() {
        SecurityContext.INSTANCE.updateUserCache(request);
        return SecurityContext.INSTANCE.getCurrentUser(request);
    }

    public boolean isRedirect() {
        Boolean b = (Boolean) request.getSession().getAttribute(ControllerDispatcherServlet.REDIRECT_KEY);

        return b != null && b;
    }

    public void clearRedirectFlag() {
        request.getSession().setAttribute(ControllerDispatcherServlet.REDIRECT_KEY, null);
    }

    public HttpMethod getMethod() {
        String formMethod = getString("__method");

        try {
            if (!formMethod.isEmpty() && formMethod != null)
                return HttpMethod.valueOf(formMethod.toUpperCase());
        } catch (IllegalArgumentException e) {
            LOGGER.info("Malformed HTTP request method");
        }

        return HttpMethod.valueOf(request.getMethod());
    }

    public void putFlashParameter(String param, Object o) {
        Map<String, Object> flash = (Map<String, Object>) request.getSession()
                .getAttribute(ControllerDispatcherServlet.FLASH_SESSION_KEY);
        if(flash == null) {
            flash = new HashMap<>();
        }

        flash.put(param, o);

        request.getSession().setAttribute(ControllerDispatcherServlet.FLASH_SESSION_KEY, flash);
    }

    public Object getFlashParameter(String param) {
        Map<String, Object> flash = (Map<String, Object>) request.getSession()
                    .getAttribute(ControllerDispatcherServlet.FLASH_SESSION_KEY);
        if(flash == null) {
            return null;
        }

        return flash.get(param);
    }

    public void clearFlash() {
        Map<String, Object> flash =
                (Map<String, Object>) request.getSession().getAttribute(ControllerDispatcherServlet.FLASH_SESSION_KEY);

        if(flash == null) {
            flash = new HashMap<>();
        }

        flash.clear();

        request.getSession().setAttribute(ControllerDispatcherServlet.FLASH_SESSION_KEY, flash);
    }
}
