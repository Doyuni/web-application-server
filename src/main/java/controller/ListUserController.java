package controller;

import db.DataBase;
import http.HttpSession;
import model.User;
import http.HttpRequest;
import http.HttpResponse;

import java.util.Collection;

public class ListUserController extends AbstractController{
    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        if (isLogined(request.getSession())) {
            response.sendRedirect("/user/login.html");
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

    private static boolean isLogined(HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null) {
            return false;
        }
        return true;
    }
}
