package com.sfedu.touragency.app;

import org.apache.logging.log4j.*;

import javax.servlet.*;
import javax.servlet.annotation.*;

/**
 * Entry point of the web application
 */
@WebListener
public class AppContextListener implements ServletContextListener {
    private static final Logger LOGGER
            = LogManager.getLogger(AppContextListener.class);

    private WebApplication webApplication = new WebApplication();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().setAttribute("webApplication", webApplication);
        webApplication.setServletContext(sce.getServletContext());
        webApplication.init();

        LOGGER.info("WebApplication initialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
