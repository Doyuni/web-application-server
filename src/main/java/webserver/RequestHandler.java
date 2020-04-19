package webserver;

import controller.Controller;
import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

            String url = getDefaultURL(request.getURL());
            // Extract Method 리팩토링
            if ("/user/create".equals(url)) {
               createUser(request, response);
            } else if ("/user/login".equals(url)) {
                login(request, response);
            } else if ("/user/list".equals(url)) {
               listUser(request, response);
            } else {
                response.forward(url);
            }
        } catch(IOException e) {
            log.error(e.getMessage());
        }

    }

    private void createUser(HttpRequest request, HttpResponse response) {
        User user = new User(request.getParameter("userId")
                , request.getParameter("password")
                , request.getParameter("name")
                , request.getParameter("email"));
        DataBase.addUser(user);
        response.sendRedirect("/index.html");
    }

    private void login(HttpRequest request, HttpResponse response) {
        User user = DataBase.findUserById(request.getParameter("userId"));
        if (user == null) {
            response.sendRedirect("/user/login_failed.html");
        } else {
            if (user.getPassword().equals(request.getParameter("password"))) {
                response.addHeader("Set-Cookie", "logined=true");
                response.sendRedirect("/index.html");
            } else {
                response.sendRedirect("/user/login_failed.html");
            }
        }
    }

    private void listUser(HttpRequest request, HttpResponse response) {
        if (isLogin(request.getHeader("Cookie"))) {
            Collection<User> users = DataBase.findAll();
            StringBuffer sb = new StringBuffer();
            sb.append("<table border='1'>");
            for (User user : users) {
                sb.append("<tr>")
                        .append("<td>" + user.getUserId() + "</td>")
                        .append("<td>" + user.getName() + "</td>")
                        .append("<td>" + user.getEmail() + "</td>")
                        .append("</tr>");
            }
            sb.append("</table>");
            log.debug("사용자 목록 추가");
            response.forwardBody(sb.toString());
        } else {
            response.sendRedirect("/user/login.html");
        }
    }
    private String getDefaultURL(String url) {
        if ("/".equals(url)) return "/index.html";
        return url;
    }

    private boolean isLogin(String line) {
        log.debug("isLogin: {}", line);
        Map<String, String> cookies = HttpRequestUtils.parseCookies(line.trim());
        String value = cookies.get("logined");
        if (value == null) {
            return false;
        }
        return Boolean.parseBoolean(value);
    }
}
