package cs496.whatsinyourfridge;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class HttpGet {

    private Map<String, String> params = new HashMap<String, String>();
    private String charset;
    private String surl;

    public HttpGet(String url) {
        this.surl = url;
        this.charset = "UTF-8";
    }

    public void addFormField(String name, String value) {
        params.put(name, value);
    }

    public String finish() throws Exception {
        // adapted from HttpPost

        StringBuilder queryString = new StringBuilder();
        for (String key : params.keySet()) {
            if (queryString.length() != 0)
                queryString.append('&');
            else
                queryString.append('?');
            queryString.append(URLEncoder.encode(key, charset));
            queryString.append('=');
            queryString.append(URLEncoder.encode(params.get(key), charset));
        }

        URL url = new URL(surl + queryString);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        Reader in = new BufferedReader(new InputStreamReader(
                conn.getInputStream(), charset));
        StringBuffer rv = new StringBuffer();
        for (int c; (c = in.read()) >= 0; rv.append((char) c))
            ;
        return rv.toString();
    }

}
