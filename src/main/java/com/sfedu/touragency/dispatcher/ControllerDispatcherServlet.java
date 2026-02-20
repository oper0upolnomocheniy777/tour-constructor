package com.sfedu.touragency.dispatcher;

import com.sfedu.touragency.persistence.ConnectionManager;
import com.sfedu.touragency.util.ServiceLocator;
import org.apache.logging.log4j.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

/**
 * Dispatches incoming request to mapped Controllers
 */
public class ControllerDispatcherServlet extends HttpServlet {
    public static final Logger LOGGER = LogManager.getLogger(ControllerDispatcherServlet.class);

    public static final String FLASH_SESSION_KEY = "__flash";
    public static final String REDIRECT_KEY = "__redirect";
    public static final String PAGE_SUFFIX = ".html";
    public static final String HEADER_REFERRER = "Referer";

    private final List<MatcherEntry> httpMatchers;

    private final List<MatcherEntry> httpServiceMatchers;



    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        dispatch(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        dispatch(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        dispatch(req, resp);
    }

    /**
     * Determines which Controller should process the request and how to handle the
     * result of the controller's processing
     * @param req
     * @param resp
     */
    private void dispatch(HttpServletRequest req, HttpServletResponse resp) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null)
            pathInfo = "/";

        RequestService requestService = new RequestService(req, resp, null);

        boolean any = false;
        any |= dispatchLoop(req, resp, pathInfo, requestService, httpMatchers, true);
        any |= dispatchLoop(req, resp, pathInfo, requestService, httpServiceMatchers, false);

        if(!tryRedirect(resp, requestService)) {
            tryRender(req, resp, requestService, any);
        }

        if(!requestService.isRedirect()) {
            requestService.clearFlash();
        } else {
            requestService.clearRedirectFlag();
        }

        ConnectionManager cm = ServiceLocator.INSTANCE.get(ConnectionManager.class);
        if (cm != null) {
            cm.clean();
        }
    }

    public ControllerDispatcherServlet() {
        httpMatchers = new ArrayList<>();
        httpServiceMatchers = new ArrayList<>();
    }

    /**
     * Define a URL-Controller mapping
     *
     * @param url      - regular expression that is used for matching
     * @param controller
     */
    public void addMapping(String url, Controller controller) {
        MatcherEntry matcherEntry = new MatcherEntry(url, controller);
        addMapping(matcherEntry);
    }

    void addMapping(MatcherEntry entry) {
        if (entry.controller.isService()) {
            httpServiceMatchers.add(entry);
        } else {
            httpMatchers.add(entry);
        }
    }

    private void tryRender(HttpServletRequest req, HttpServletResponse resp, RequestService requestService, boolean any) {
        if (requestService.getRenderPage() != null) {
            try {
                req.getRequestDispatcher(requestService.getRenderPage()).forward(req, resp);
            } catch (ServletException | IOException e) {
                LOGGER.warn("An exception happened at page rendering phase");
            }
        } else {
            try {
                if (req.getMethod().equals("GET")) {
                    req.getRequestDispatcher("/pages/" + withSuffix(req.getPathInfo()))
                            .forward(req, resp);
                } else if (req.getHeader(HEADER_REFERRER) != null) {
                    resp.sendRedirect(req.getHeader(HEADER_REFERRER));
                }
            } catch (ServletException | IOException e) {
                LOGGER.warn("An exception happened at page rendering phase");
            }
        }
    }

    private boolean tryRedirect(HttpServletResponse resp, RequestService requestService) {
        if (requestService.getRedirectPath() != null) {
            try {
                requestService.getRequest().getSession().setAttribute(REDIRECT_KEY, true);
                resp.sendRedirect(requestService.getRedirectPath());
                return true;
            } catch (IOException e) {
                LOGGER.warn("An exception happened at redirecting");
            }
        }
        return false;
    }

    private boolean dispatchLoop(HttpServletRequest req, HttpServletResponse resp, String pathInfo,
                                 RequestService requestService,
                                 List<MatcherEntry> httpMatchers, boolean matchFirst) {
        boolean any = false;

        for (MatcherEntry matcherEntry : httpMatchers) {

            if (matcherEntry.matches(pathInfo)) {
                resp.setStatus(HttpServletResponse.SC_OK);

                any = true;

                matcherEntry.call(requestService);

                if (matchFirst) {
                    break;
                }
            }
        }

        return any;
    }

    private boolean hasPageSuffix(String pathInfo) {
        int index = pathInfo.lastIndexOf(PAGE_SUFFIX);
        return index + PAGE_SUFFIX.length() == pathInfo.length();
    }

    private String withSuffix(String pathInfo) {
        if (!hasPageSuffix(pathInfo))
            return pathInfo + PAGE_SUFFIX;
        return pathInfo;
    }

    /**
     * This class represent a URL pattern - Controller pair
     */
    static class MatcherEntry {
        private String url;

        private final Controller controller;

        public MatcherEntry(String url, Controller controller) {
            this.controller = controller;
            this.url = url;
        }

        public boolean matches(String pathInfo) {
            int index = pathInfo.lastIndexOf(PAGE_SUFFIX);
            if (index + PAGE_SUFFIX.length() == pathInfo.length()) {
                pathInfo = pathInfo.substring(0, index);
            }
            return url.equalsIgnoreCase(pathInfo);
        }

        public void call(RequestService requestService) {
            controller.execute(requestService);
        }
    }
}
