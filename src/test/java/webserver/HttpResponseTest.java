package webserver;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;


public class HttpResponseTest {

    private String testDirectory = "./src/test/resources/";

    @Test
    public void responseForward() throws FileNotFoundException {
        HttpResponse response =
                new HttpResponse(createoutputStream("Http_Forward.txt"));
        response.forward("/user/form.html");
    }

    @Test
    public void responseRedirect() throws FileNotFoundException {
        HttpResponse response =
                new HttpResponse(createoutputStream("Http_Redirect.txt"));
        response.sendRedirect("/index.html");
    }

    @Test
    public void responseCookies() throws FileNotFoundException {
        HttpResponse response =
                new HttpResponse(createoutputStream("Http_Cookie.txt"));
        response.addHeader("Set-Cookie", "logined=true");
        response.sendRedirect("/index.html");
    }

    private OutputStream createoutputStream(String fileName) throws FileNotFoundException {
        return new FileOutputStream(new File(testDirectory + fileName));
    }
}
