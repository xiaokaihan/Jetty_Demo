package jetty;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;


/**
 * Jetty Demo.  access handler handle http request.
 *
 * Created by Key.Xiao on 2017/8/2.
 */
public class App {
private final static int RESTFUL_HTTP_PORT = 2222;

    public static void main(String[] args) throws Exception {
        App app = new App();
        app.startJetty();
    }

    private void startJetty() throws Exception {
        System.out.println("Starting Jetty .....");

        Server server = new Server(RESTFUL_HTTP_PORT);
        server.setHandler(new MyJettyHandler());

        try {
            server.start();
            server.join();
        } finally {
            server.destroy();
        }
    }
}

class MyJettyHandler extends AbstractHandler {

/**
 * 请求url为： <a>http://localhost:2222/system/jwt/decoded</a> 时，返回clientInfo json字符串;
 * <p>
 * 请求url为： <a>http://localhost:2222/clients/:clientID/feedPlan</a> 时，返回feedPlan json字符串;
 */
public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
    try {
        // 处理程序设置响应状态，内容类型，并在使用writer创建响应的正文之前将请求标记为已处理的请求。
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        String writeRes = null;
        if (target.contains("decoded")) {
            writeRes = "{\"clientInfo\":{\"brokerID\":\"MR\",\"clientID\":\"2trade124\",\"clientUniqueKey\":\"MR.2trade124\",\"updateDateTime\":\"20170728150000\"}}";
        } else if (target.contains("feedPlan")) {
            writeRes = "{\"feedPlan\":\"4\",\"showFeedAgreement\":\"Y\"}";
        }
        response.getWriter().println(writeRes);
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}