package webserver;


import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class RequestLineTest {

    @Test
    public void create_method() {
        RequestLine line = new RequestLine("GET /index.html HTTP/1.1");
        assertEquals("GET", line.getMethod());
        assertEquals("/index.html", line.getURL());
    }

    @Test
    public void create_path_and_params() {
        RequestLine line = new RequestLine(
                "GET /user/create?userId=doyuni&password=pass HTTP/1.1");
        assertEquals("GET", line.getMethod());
        assertEquals("/user/create", line.getURL());
        Map<String, String> params = line.getParams();
        assertEquals(2, params.size());
        assertEquals("doyuni", line.getParams().get("userId"));
    }
}
