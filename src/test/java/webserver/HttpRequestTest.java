package webserver;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import static org.junit.Assert.*;

public class HttpRequestTest {

    private String testDirectory = "./src/test/resources/";

    @Test
    public void requestGET() throws FileNotFoundException {
        InputStream in = new FileInputStream(new File(testDirectory+"Http_GET.txt"));
        HttpRequest request = new HttpRequest(in);

        assertEquals(HttpMethod.GET, request.getMethod());
        assertEquals("/user/create", request.getURL());
        assertEquals("keep-alive", request.getHeader("Connection"));
        assertEquals("doyuni", request.getParameter("userId"));
    }

    @Test
    public void requestPOST() throws FileNotFoundException {
        InputStream in = new FileInputStream(new File(testDirectory+"Http_POST.txt"));
        HttpRequest request = new HttpRequest(in);

        assertEquals(HttpMethod.POST, request.getMethod());
        assertEquals("/user/create", request.getURL());
        assertEquals("keep-alive", request.getHeader("Connection"));
        assertEquals("doyuni", request.getParameter("userId"));
    }
}
