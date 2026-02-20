package com.sfedu.touragency.app;

import com.sfedu.touragency.controller.*;
import com.sfedu.touragency.dispatcher.ControllerDispatcherServletBuilder;
import com.sfedu.touragency.domain.Role;
import com.sfedu.touragency.imageprovider.ImageService;
import com.sfedu.touragency.imageprovider.ImageServiceImpl;
import com.sfedu.touragency.persistence.ConnectionManager;
import com.sfedu.touragency.persistence.JdbcTemplate;
import com.sfedu.touragency.persistence.dao.TourDao;
import com.sfedu.touragency.persistence.dao.factory.DaoFactory;
import com.sfedu.touragency.persistence.dao.factory.JdbcDaoFactory;
import com.sfedu.touragency.security.SecurityContext;
import com.sfedu.touragency.service.*;
import com.sfedu.touragency.service.impl.*;
import com.sfedu.touragency.util.ResourcesUtil;
import com.sfedu.touragency.util.ServiceLocator;
import org.apache.logging.log4j.*;

import javax.servlet.*;
import java.io.*;
import java.util.*;

import static com.sfedu.touragency.util.ResourcesUtil.*;

/**
 * The web application itself. It has two goals:
 * 1. configure the application(connection manager, service locator, security, controllers)
 * 2. bootstrap the application
 */
public class WebApplication {
    private static final Logger LOGGER = LogManager.getLogger(WebApplication.class);

    /**
     * This field necessary for registering front controller servlet
     */
    private ServletContext servletContext;

    private ConnectionManager connectionManager;

    private ServiceLocator serviceLocator;

    private DaoFactory daoFactory;

    private Properties appProperties;

    protected void init() {
        serviceLocator = ServiceLocator.INSTANCE;

        readProperties();
        setUpPersistence();
        createDb();
        prepareServices();

        SecurityContext.INSTANCE.setUserDao(daoFactory.getUserDao());

        ControllerDispatcherServletBuilder servletBuilder =
                new ControllerDispatcherServletBuilder(servletContext);
        buildDispatcherServlet(servletBuilder)
                .buildAndRegister("Command Dispatcher Servlet", "/app/*");
        configureSecurity(SecurityContext.INSTANCE);
    }

    private void prepareServices() {
        UserService userService = new UserServiceImpl(daoFactory.getUserDao(),
                daoFactory.getTourDao());

        TourService tourService = new TourServiceImpl(daoFactory.getTourDao());

        PurchaseService purchaseService = new PurchaseServiceImpl(daoFactory.getPurchaseDao(),
                        daoFactory.getTourDao(), daoFactory.getUserDao(), connectionManager);

        AuthService authService = new AuthServiceImpl(userService);

        ReviewService reviewService = new ReviewServiceImpl(daoFactory.getReviewDao(),
                daoFactory.getUserDao(), daoFactory.getTourDao(), connectionManager);

        TourImageService tourImageService = new TourImageServiceImpl(daoFactory.getTourImageDao());
        ImageService imageService = getImageService();

        serviceLocator.publish(userService, UserService.class);
        serviceLocator.publish(tourService, TourService.class);
        serviceLocator.publish(purchaseService, PurchaseService.class);
        serviceLocator.publish(authService, AuthService.class);
        serviceLocator.publish(reviewService, ReviewService.class);
        serviceLocator.publish(tourImageService, TourImageService.class);
        serviceLocator.publish(imageService, ImageService.class);
        serviceLocator.publish(daoFactory.getTourDao(), TourDao.class);
    }

    private void readProperties() {
        appProperties = new Properties();
        try(InputStream is = ResourcesUtil.getResourceInputStream("app.properties")) {
            appProperties.load(is);
        } catch (IOException e) {
            LOGGER.error("Cannot read app.properties file from classpath");
        }
    }

    private ImageService getImageService() {
        return new ImageServiceImpl(appProperties.getProperty(AppProperties.IMAGE_PROVIDER_DIR),
                Long.valueOf(appProperties.getProperty(AppProperties.IMAGE_PROVIDER_MAX_SIZE)));
    }

    private void configureSecurity(SecurityContext sc) {
        sc.addSecurityConstraint("/agent/.*", Role.TOUR_AGENT)
                .addSecurityConstraint("/user/.*", Role.CUSTOMER, Role.TOUR_AGENT);
    }

    private ControllerDispatcherServletBuilder buildDispatcherServlet(ControllerDispatcherServletBuilder servletBuilder) {
        return servletBuilder
                .addMapping("/", new RedirectController("/index"))
                .addMapping("/index", new RandomHotTourController())
                .addMapping("/login", new LoginController())
                .addMapping("/logout", new LogoutController())
                .addMapping("/register", new RegisterController())
                .addMapping("/lang", new LocaleController())
                .addMapping("/tour", new TourController())
                .withSecurity("/tours", new ToursController())
                    .authorized().modifying()
                .addMapping("/user/buy", new BuyController())
                .addMapping("/user/purchase", new PurchaseController())
                .addMapping("/user/purchases", new PurchasesController())
                .addMapping("/review", new ReviewController())
                .addMapping("/agent/tours", new AgentToursPageController())
                .addMapping("/agent/users", new AgentUsersPageController())
                .withSecurity("/user/discount",new UpdateDiscountController())
                    .roles(Role.TOUR_AGENT).permitAll()
                .addMapping("/agent/setadmin", new AgentSetAdminController())
                .withSecurity("/tour-images", new TourImagesController())
                    .roles(Role.TOUR_AGENT).modifying()
                .addMapping("/agent/new-tour", new AgentNewTourPageController())
                .addMapping("/agent/edit-tour", new AgentEditTourController())
                .addMapping("/agent/order", new AgentOrderController());
    }

    private void createDb() {
        File file = getResourceFile("database.sql");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(connectionManager);
        jdbcTemplate.executeSqlFile(file);
        serviceLocator.publish(connectionManager, ConnectionManager.class);
    }

    private void setUpPersistence() {
        connectionManager = ConnectionManager.fromJndi(
                appProperties.getProperty(AppProperties.CP_JNDI));
        daoFactory = new JdbcDaoFactory(connectionManager);
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

}
