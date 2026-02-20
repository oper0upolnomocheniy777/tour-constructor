package com.sfedu.touragency.util;

import org.apache.logging.log4j.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * Allows sharing objects between different application modules
 * Also contains methods to make objects available directly in
 * the JSP page
 *
 * <p>Two main methods user get() and publish() that have various overloaded versions
 *
 * <p>Shortname of an object - its name in source file with a lower case first letter
 */
public enum ServiceLocator {
    INSTANCE;

    public static final Logger LOGGER = LogManager.getLogger(ServiceLocator.class);

    private Map<String, Object> objects = new ConcurrentHashMap<>();

    /**
     * Make object globally available by the given name
     * @param name    name by which the object can be retrieved
     * @param o       the shared object
     */
    public void publish(String name, Object o) {
        objects.put(name, o);
    }

    /**
     * Create an instance of the given class and publishes it by
     * its fully qualified name and short name
     */
    public <T> void publish(Class<T> clazz) {
        try {
            T o = clazz.newInstance();
            publish(o, clazz);
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error("Cannot publish object", e);
        }
    }

    public <T> void publish(T o, Class<T> clazz) {
        publish(clazz.getName(), o);
    }

    public Object remove(String name) {
        Object o = objects.remove(name);

        return o;
    }

    public <T> T remove(Class<T> clazz) {
        return (T) remove(clazz.getName());
    }

    /**
     * Get published object by name
     */
    public Object get(String name) {
        return objects.get(name);
    }


    public <T> T get(Class<T> clazz) {
        return (T) get(clazz.getName());
    }

    public void clear() {
        this.objects.clear();
    }
}
