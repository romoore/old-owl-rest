
package org.grailrtls;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

// The Java class will be hosted at the URI path "/my/html"
@Path("/my/html")
public class MyResource {

    // TODO: update the class to suit your needs
    
    // The Java method will process HTTP GET requests
    @GET 
    @Produces("text/html")
    public RESTGreeting getIt(@QueryParam("name")String name) {
    	return new RESTGreeting(name);
        
    }
}
