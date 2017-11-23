package nl.amis.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("rest-api")
@Consumes("application/json")
@Produces("application/json")
public class PersonResource {
    public PersonResource() {
    }

    @POST
    public Response postData(String content) {

        // Provide method implementation.
        // TODO

        throw new UnsupportedOperationException();
    }

    @GET
    @Path("person")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public People getPeople() {
        People p = new People();
        p.setCollectionLabel("restful collection");
        p.getPersons().add(new Person("John", "Doe", "Morocco", "Cab Driver"));
        p.getPersons().add(new Person("Sarah", "Brower", "UK", "Cab Driver"));
        p.getPersons().add(new Person("Luke", "Chiefs", "Germany", "Cook"));
        // Provide method implementation.
        // TODO
        return p;
    }
}
