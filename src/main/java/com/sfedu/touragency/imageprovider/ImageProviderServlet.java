package com.sfedu.touragency.imageprovider;

import com.sfedu.touragency.dispatcher.Controller;
import com.sfedu.touragency.domain.Role;
import com.sfedu.touragency.util.ServiceLocator;
import org.apache.logging.log4j.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import java.io.*;

/**
 * Servlet for handling multipart upload.
 * Multipart handling cannot be handled in {@link Controller}
 * because of Tomcat's multipart support. Tomcat cannot handle multipart data after a
 * forward was made
 */
@WebServlet(urlPatterns = "/image-provider/*")
@MultipartConfig
public class ImageProviderServlet extends HttpServlet{
    private static final Logger LOGGER = LogManager.getLogger(ImageProviderServlet.class);

    private static final String FORM_IMAGES = "images[]";

    private ImageService imageService = ServiceLocator.INSTANCE.get(ImageService.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int index = req.getRequestURI().indexOf(req.getServletPath()) +
                req.getServletPath().length() + 1;
        String filename = req.getRequestURI().substring(index);

        if(!imageService.load(filename, resp.getOutputStream())) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            writeGetHeaders(resp, filename);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!req.isUserInRole(Role.TOUR_AGENT.toString())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        int filesCount = (int) req.getParts().stream()
                .filter(p -> p.getName().equals(FORM_IMAGES))
                .count();

        String[] names = new String[filesCount];

        int i = 0;
        for (Part part: req.getParts()) {
            if (!part.getName().equals(FORM_IMAGES))
                continue;

            String ext = getExtension(part.getContentType());

            String filename = imageService.save(part.getInputStream(), ext);

            if (filename != null) {
                names[i] = getUrl(req, filename);
                i++;
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }

        resp.setStatus(HttpServletResponse.SC_CREATED);

        String[] jsonArr = new String[names.length];
        for (int j = 0; j < jsonArr.length; j++) {
            jsonArr[j] = jsonArray(quote(names[j]), quote(imageService.thumbnailName(names[j])));
        }

        resp.getOutputStream().print(jsonArray(jsonArr));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!req.isUserInRole(Role.TOUR_AGENT.toString())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String filename = req.getParameter("file");
        imageService.delete(filename);
    }

    private String jsonArray(String... values) {
        return "[" +  String.join(",", values) + "]" ;
    }

    private String getUrl(HttpServletRequest request, String name) {
        return request.getRequestURL() + "/" + name;
    }

    private String quote(String name) {
        return "\"" + name + "\"";
    }

    private String getExtension(String contentType) {
        switch (contentType) {
            case "image/png":
                return ".png";
            case "image/jpeg":
                return ".jpg";
            default:
                return null;
        }
    }


    private void writeGetHeaders(HttpServletResponse resp, String filename) {
        String filetype = filename.substring(filename.indexOf(".") + 1);
        resp.setHeader("Content-Type", "image/" + filetype);
        resp.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");
        resp.setHeader("Cache-Control", "max-age=11536000");
    }
}
