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

    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> params = new HashMap<>();
    private RequestLine requestLine;

    public HttpRequest(InputStream in) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = br.readLine();
            if(line == null) return;

            requestLine = new RequestLine(line);

            String header = br.readLine();
            while(!"".equals(header)) {
                log.debug("Header: {}", header);
                String[] tokens = header.split(":");
                headers.put(tokens[0].trim(), tokens[1].trim());
                header = br.readLine();
                if(header == null) break;
            }

            if (getMethod().isPost()) {
                String requestBody = IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")));
                log.debug("Request Body: {}", requestBody);
                params = HttpRequestUtils.parseQueryString(requestBody);
            } else {
                params = requestLine.getParams();
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getURL() {
        return requestLine.getURL();
    }

    public String getHeader(String key){
        return headers.get(key);
    }

    public String getParameter(String key) {
        return params.get(key);
    }
}
