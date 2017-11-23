package nl.amis.portal.view.cache;

import java.util.Map;

import javax.json.Json;
import javax.json.JsonBuilderFactory;

import javax.json.JsonObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nl.amis.portal.view.TokenCache;

@Path("cache")
@Produces("application/json")
public class Cache {
    public Cache() {
    }

    @GET
    @Path("{token}")
    @Produces({ "application/json" })
    public JsonObject getData(@PathParam("token") String token) {

        // parse token, get details for user; get stuff from cache
        Map userMap = (Map) new TokenCache().retrieveFromCache(token);
        //https://docs.oracle.com/javaee/7/api/javax/json/JsonObjectBuilder.html
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        JsonObject value = factory.createObjectBuilder()
                                  .add("principal", (String) userMap.get("principal"))
                                  .add("roles", (String) userMap.get("roles"))
                                  .add("subject", (String) userMap.get("subject"))
                                  .add("details", factory.createObjectBuilder().add("detail1", "some value"))
                                  .build();
        return value;
    }

}
