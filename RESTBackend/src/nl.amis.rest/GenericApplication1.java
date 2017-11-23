package nl.amis.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import oracle.wsm.metadata.annotation.PolicyReference;
import oracle.wsm.metadata.annotation.PolicySet;

@ApplicationPath("direct-resources")
@PolicySet(references = { @PolicyReference(value = "oracle/http_jwt_token_service_policy") })
public class GenericApplication1 extends Application {
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();

        // Register root resources.
        classes.add(PersonResource.class);

        // Register provider classes.

        return classes;
    }
}
