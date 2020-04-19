package controller;

import db.DataBase;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.util.Collection;

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

    public boolean isLogin(String fieldValue) {
        if (fieldValue == null) {
            return false;
        }
        return Boolean.parseBoolean(fieldValue);
    }
}
