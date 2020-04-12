package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;
import java.util.stream.Stream;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String firstLine = br.readLine();
            String url = HttpRequestUtils.getURL(firstLine);


            if (url.startsWith("/user/create")) {
                int index = url.indexOf("?");
                String requestPath = url.substring(0, index);
                String params = url.substring(index + 1);
                log.debug("Request Path: {}", requestPath);
                log.debug("Params: {}", params);
                Map<String, String> userData = HttpRequestUtils.parseQueryString(params);
                String userId = userData.get("userId");
                String password = userData.get("password");
                String name = userData.get("name");
                String email = userData.get("email");

                User user = new User(userId, password, name, email);
                log.debug("User object 확인: {}", user);

                url = "/index.html";
//                // 데이터 조회
//                Stream<Map<String, String>> stringStream = Stream.of(userData);
//                stringStream.forEach(System.out::println);
            }

            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
