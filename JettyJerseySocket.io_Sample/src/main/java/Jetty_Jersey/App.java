package Jetty_Jersey;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;


/**
 * Created by Key.Xiao on 2017/8/2.
 */
public class App {

public static void main(String[] args) throws Exception {

    int RESTFUL_HTTPS_PORT = 2222;

    System.out.println("Starting Jetty .....");

    Server server = new Server(RESTFUL_HTTPS_PORT);

    // Setup Servlet Container
    ResourceConfig config = new ResourceConfig();
    config.packages("Jetty_Jersey.resource");
    ServletHolder servlet = new ServletHolder(new ServletContainer(config));

    ServletContextHandler context = new ServletContextHandler(server, "/*");
    context.addServlet(servlet, "/*");

    try {
        server.start();
        server.join();
    } finally {
        server.destroy();
    }
}

}
