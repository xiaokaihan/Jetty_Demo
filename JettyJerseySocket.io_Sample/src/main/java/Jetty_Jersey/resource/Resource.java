package Jetty_Jersey.resource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by Key.Xiao on 2017/8/2.
 */
@Path("test")
public class Resource {

@GET
@Path("hello")
@Produces(MediaType.TEXT_PLAIN)
public String helloWorld() {
    return "Hello World!";
}

@GET
@Path("key")
@Produces(MediaType.TEXT_PLAIN)
public String postMessage(@FormParam("name") String name){
    return "<h2>Hello, " + name + "</h2>";
}
}
