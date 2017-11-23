package nl.amis.portal.view;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import nl.amis.portal.view.cache.Cache;

@ApplicationPath("app-rest")
public class GenericApplication extends Application {
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();

        // Register root resources.
        classes.add(Cache.class);

        // Register provider classes.

        return classes;
    }
}
