package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger("HttpRequest.class");

    private String url;
    private String method;

    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> params = new HashMap<>();

    public HttpRequest(InputStream in) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String requestLine = br.readLine();
            if(requestLine == null) return;
            processRequestLine(requestLine);

            String header = br.readLine();
            while(!"".equals(header)) {
                log.debug("Header: {}", header);
                String[] tokens = header.split(":");
                headers.put(tokens[0].trim(), tokens[1].trim());
                header = br.readLine();
                if(header == null) return;
            }

            if ("POST".equals(method)) {
                String requestBody = IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")));
                log.debug("Request Body: {}", requestBody);
                params = HttpRequestUtils.parseQueryString(requestBody);
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    public void processRequestLine(String requestLine) {
        log.debug("Request Line:{}", requestLine);
        String[] tokens = requestLine.split(" ");
        method = tokens[0];

        if (method.equals("POST")) {
            url = tokens[1];
            return;
        }
        int index = tokens[1].indexOf("?");
        if (index == -1) {
            url = tokens[1];
        } else {
            url = tokens[1].substring(0, index);
            params = HttpRequestUtils.parseQueryString(tokens[1].substring(index+1));
        }
    }

    public String getMethod() {
        return method;
    }

    public String getURL() {
        return url;
    }

    public String getHeader(String key){
        return headers.get(key);
    }

    public String getParameter(String key) {
        return params.get(key);
    }
}
