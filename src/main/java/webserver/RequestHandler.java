package webserver;

import controller.Controller;
import http.HttpRequest;
import http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    private Map<String, Controller> controllers = new HashMap<String, Controller>();

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);

            if (request.getCookies().getCookie("JSESSIONID") == null) {
                response.addHeader("Set-Cookie", "JSESSIONID="+ UUID.randomUUID());
            }

            Controller controller = RequestMapping.getController(request.getURL());
            if (controller == null) {
                String url = getDefaultURL(request.getURL());
                response.forward(url);
            } else {
                controller.service(request, response);
            }

        } catch(IOException e) {
            log.error(e.getMessage());
        }

    }

    private String getDefaultURL(String url) {
        if ("/".equals(url)) return "/index.html";
        return url;
    }

}
