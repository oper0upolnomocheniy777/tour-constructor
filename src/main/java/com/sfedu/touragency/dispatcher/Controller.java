package com.sfedu.touragency.dispatcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class used for convenient request handling
 *
 * <p> It resembles a HttpServlet but has the advantage of centralized
 * dispatching and path parameter handling
 *
 * <p> Every method receives a RequestService instance that contains
 * some convenient wrapper methods
 *
 * @see ControllerDispatcherServlet
 * @see RequestService
 */
public abstract class Controller {
    private static final Logger LOGGER = LogManager.getLogger(Controller.class);

    public void execute(RequestService reqService) {
        HttpMethod method = reqService.getMethod();

        switch (method) {
            case GET:
                get(reqService);
                break;
            case POST:
                post(reqService);
                break;
            case PUT:
                put(reqService);
                break;
            case DELETE:
                delete(reqService);
                break;
            default:
                LOGGER.error("Switch doesn't cover all the enum variants");
        }

        any(reqService);
    }

    public void get(RequestService reqService) {}
    public void post(RequestService reqService) {}
    public void delete(RequestService reqService) {}
    public void put(RequestService reqService) {}
    public void any(RequestService reqService) {}

    /**
     * Service controller always try to match the URL pattern, while
     * only one regular controller would be called per request
     *
     * @return true if this controller serves as a service controller
     */
    public boolean isService() {
        return false;
    }
}
