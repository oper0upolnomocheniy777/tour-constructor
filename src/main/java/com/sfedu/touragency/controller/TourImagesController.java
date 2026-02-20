package com.sfedu.touragency.controller;

import com.sfedu.touragency.dispatcher.Controller;
import com.sfedu.touragency.dispatcher.RequestService;
import com.sfedu.touragency.domain.Role;
import com.sfedu.touragency.domain.Tour;
import com.sfedu.touragency.domain.TourImage;
import com.sfedu.touragency.imageprovider.ImageService;
import com.sfedu.touragency.security.AccessDeniedException;
import com.sfedu.touragency.service.TourImageService;
import com.sfedu.touragency.util.ServiceLocator;
import org.apache.logging.log4j.*;

import javax.servlet.http.*;
import java.net.*;

public class TourImagesController extends Controller {
    private static final Logger LOGGER = LogManager.getLogger(TourImagesController.class);

    private TourImageService tourImageService =
            ServiceLocator.INSTANCE.get(TourImageService.class);

    private ImageService imageService = ServiceLocator.INSTANCE.get(ImageService.class);

    @Override
    public void post(RequestService reqService) {
        long tourId = reqService.getLong("tourId").get();
        URL imageUrl = null;
        URL thumbnailUrl = null;

        try {
            imageUrl = new URL(reqService.getString("imageUrl"));
            thumbnailUrl = new URL(reqService.getString("thumbnailUrl"));
        } catch (MalformedURLException e) {
            reqService.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        TourImage tourImage = new TourImage();
        tourImage.setTour(new Tour(tourId));
        tourImage.setImageUrl(imageUrl);
        tourImage.setThumbnailUrl(thumbnailUrl);

        tourImageService.create(tourImage);
    }

    @Override
    public void delete(RequestService reqService) {
        long id = reqService.getLong("id").get();
        TourImage tourImage = tourImageService.read(id);
        tourImageService.delete(id);

        if (!reqService.getRequest().isUserInRole(Role.TOUR_AGENT.toString())) {
            throw new AccessDeniedException();
        }

        String filename = reqService.getString("file");
        imageService.delete(getFilename(tourImage.getImageUrl()));
    }

    private String getFilename(URL url) {
        String s = url.getFile();
        return s.substring(s.lastIndexOf("/") + 1);
    }
}
