package com.sfedu.touragency.util;

import org.apache.logging.log4j.*;

import java.io.*;
import java.net.*;

/**
 * Loads a resource file from the classpath. ResourceBundle is a better alternative
 */
public class ResourcesUtil {
    private static Logger LOGGER = LogManager.getLogger(ResourcesUtil.class);

    public static InputStream getResourceInputStream(String path) {
        return ResourcesUtil.class.getClassLoader().getResourceAsStream(path);
    }

    public static File getResourceFile(String path) {
        URL url = ResourcesUtil.class.getClassLoader().getResource(path);

        File file = null;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            LOGGER.warn("Bad path", e);
            return null;
        }


        if (file.exists()) {
            return file;
        }

        LOGGER.info("Cannot load resource");
        return null;
    }
}
