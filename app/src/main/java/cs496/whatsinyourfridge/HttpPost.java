package cs496.whatsinyourfridge;

// code adapted from http://stackoverflow.com/questions/4205980/java-sending-http-parameters-via-post-method-easily

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class HttpPost {

	private Map<String, String> params = new HashMap<String, String>();
	private String charset;
	private String surl;

	public HttpPost(String url, String charset) {
		this.surl = url;
		this.charset = charset;
	}

	public void addFormField(String name, String value) {
		params.put(name, value);
	}

	public String finish() throws Exception {
		URL url = new URL(surl);

		StringBuilder postData = new StringBuilder();
		for (String key : params.keySet()) {
			if (postData.length() != 0)
				postData.append('&');
			postData.append(URLEncoder.encode(key, charset));
			postData.append('=');
			postData.append(URLEncoder.encode(params.get(key), charset));
		}
		byte[] postDataBytes = postData.toString().getBytes(charset);

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length",
				String.valueOf(postDataBytes.length));
		conn.setDoOutput(true);
		conn.getOutputStream().write(postDataBytes);

		Reader in = new BufferedReader(new InputStreamReader(
				conn.getInputStream(), charset));
		StringBuffer rv = new StringBuffer();
		for (int c; (c = in.read()) >= 0; rv.append((char) c))
			;
		return rv.toString();
	}
}
