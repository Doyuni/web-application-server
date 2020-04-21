package controller;

import db.DataBase;
import model.User;
import http.HttpRequest;
import http.HttpResponse;
import util.HttpRequestUtils;

import java.util.Collection;
import java.util.Map;

public class ListUserController extends AbstractController{
    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        if (!isLogin(request.getHeader("Cookie"))) {
            response.forward("/user/login.html");
            return;
        }
        Collection<User> users = DataBase.findAll();
        StringBuffer sb = new StringBuffer();
        sb.append("<table border='1'>");
        for (User user: users) {
            sb.append("<tr>")
                    .append("<td>" + user.getUserId() + "</td>")
                    .append("<td>" + user.getName() + "</td>")
                    .append("<td>" + user.getEmail() + "</td>")
                    .append("</tr>");
        }
        sb.append("</table>");
        byte[] body = sb.toString().getBytes();
        response.response200Header();
        response.responseBody(body);
    }

    public boolean isLogin(String cookieValue) {
        Map<String, String> cookies = HttpRequestUtils.parseCookies(cookieValue);
        String value = cookies.get("logined");
        if (value == null) {
            return false;
        }
        return Boolean.parseBoolean(value);
    }
}
