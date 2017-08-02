package Jetty_jersey_Netty_Socketio;

import java.io.FileInputStream;
import java.io.InputStream;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;


/**
 * open index.html send message to test.
 * <p>
 * https.
 * <p>
 * Created by Key.Xiao on 2017/8/2.
 */
public class App {
static Configuration socketio_config;
static SocketIOServer socketio_server;

public static void main(String[] args) throws Exception {
    // Config section
    int WEBSOCKET_HTTPS_PORT = 9092;
    int RESTFUL_HTTPS_PORT = 2222;

    String KEYSTORE_FILE = "./resource/keystore.jks";
    String KEYSTORE_PASSWORD = "password";
    String KEYMANAGER_PASSWORD = "password";

    System.out.println("Starting Netty-Socket.IO .....");

    socketio_config = new Configuration();
    socketio_config.setHostname("localhost");
    socketio_config.setPort(WEBSOCKET_HTTPS_PORT);

    // Setup SSL
    InputStream stream = new FileInputStream(KEYSTORE_FILE);
    socketio_config.setKeyStorePassword(KEYSTORE_PASSWORD);
    socketio_config.setKeyStore(stream);

    // System.out.println("Websocket Keystore format: " + socketio_config.getSSLProtocol());

    socketio_server = new SocketIOServer(socketio_config);

    socketio_server.addConnectListener(new ConnectListener() {
        public void onConnect(SocketIOClient client) {
            String token = client.getHandshakeData().getSingleUrlParam("authorization");

            String uuid = "";
            String uuid_temp = client.getHandshakeData().getSingleUrlParam("uuid");
            String client_ip = client.getHandshakeData().getAddress().getHostString();

            if (uuid_temp != null) {
                uuid = uuid_temp;
            } else if (client_ip != null) {
                uuid = client_ip;
            } else {
                uuid = "undefined";
            }

            System.out.println("Web Socket Client is connected, token = " + token + ", uuid = " + uuid + ".");
        }
    });

    socketio_server.addEventListener("chatevent", ChatObject.class, new DataListener<ChatObject>() {
        public void onData(SocketIOClient client, ChatObject data, AckRequest ackRequest) {
            socketio_server.getBroadcastOperations().sendEvent("chatevent", data);
        }
    });

    socketio_server.start();

    // Thread.sleep(Integer.MAX_VALUE);
    // socketio_server.stop();

    System.out.println("Starting Jetty .....");

    Server server = new Server(RESTFUL_HTTPS_PORT);

    /*// Setup SSL
    SslContextFactory sslContextFactory = new SslContextFactory();
    sslContextFactory.setKeyStorePath(System.getProperty("jetty.keystore.path", KEYSTORE_FILE));
    sslContextFactory.setKeyStorePassword(System.getProperty("jetty.keystore.password", KEYSTORE_PASSWORD));
    sslContextFactory.setKeyManagerPassword(System.getProperty("jetty.keymanager.password", KEYMANAGER_PASSWORD));
*/
    // Setup HTTP Configuration
  /*  HttpConfiguration httpConf = new HttpConfiguration();
    httpConf.setSecurePort(RESTFUL_HTTPS_PORT);
    httpConf.setSecureScheme("https");
    httpConf.addCustomizer(new SecureRequestCustomizer());*/

   /* ServerConnector serverConnector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory,
            "http/1.1"), new HttpConnectionFactory(httpConf)); // <-- use it!
    ServerConnector serverConnector = new ServerConnector(server, new SslConnectionFactory("http/1.1"));
    serverConnector.setPort(RESTFUL_HTTPS_PORT);
*/
    //server.setConnectors(new Connector[] { serverConnector });

    // Setup Servlet Container
    ResourceConfig config = new ResourceConfig();
    config.packages("Jetty_jersey_Netty_Socketio");
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

public static void sendSocketIoBroadcastMessage(String userName, String message) {
    socketio_server.getBroadcastOperations().sendEvent("chatevent", new ChatObject(userName, message));

}
}
