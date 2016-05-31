package cs496.whatsinyourfridge;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONObject;

import java.io.IOException;


public class Server {
    private final String username;
    private final String password;
    private long session;

    // Registers or authenticates the user, and returns a session id
    public static long init(String username, String password, boolean newAccount) throws Exception {
        String op = newAccount ? "register" : "login";
        HttpPost initPost = new HttpPost(getUrl() + "?op=" + op, "UTF-8");
        initPost.addFormField("username", username);
        initPost.addFormField("password", password);

        String json = initPost.finish();
        JSONObject obj = new JSONObject(json);
        String err = obj.has("errormsg") ? obj.getString("errormsg") : null;
        if (err != null && err.length() > 0) throw new IOException(err);
        long session = obj.getLong("session");
        if (session <= 0)
            throw new IllegalArgumentException("Unable to access server; session==" + session);
        return session;
    }

    Context context;

    public Server(Context context, String username, String password, long session) {
        this.context = context;
        this.username = username;
        this.password = password;
        this.session = session;
    }

    public boolean isNetworkOn() {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public long send(Recipe entry) throws Exception {
        // First, validate the session. It might have timed out. If so, then log in again.
        if (!validateSession()) return 0L;

        // Second, transmit the data.
        HttpPost post = new HttpPost(getUrl() + "?op=create", "UTF-8");
        post.addFormField("title", entry.getTitle());
        post.addFormField("blather", entry.getBlather());
        post.addFormField("tags", entry.getTags());
        post.addFormField("when", Long.toString(entry.getWhen()));
        post.addFormField("sessionId", Long.toString(session));
        String json = post.finish();
        JSONObject obj = new JSONObject(json);
        String err = obj.has("errormsg") ? obj.getString("errormsg") : null;
        if (err != null && err.length() > 0) throw new IOException(err);
        long id = obj.getLong("id");
        if (id <= 0)
            throw new IllegalArgumentException("Unable to save data; id==" + id);
        return id;
    }

    private boolean validateSession() throws Exception {
        // we'll try a certain number of times to validate the session; if it doesn't work, login again
        if (validateSession(10, 2000)) return true; // great

        // ok, the session seems dead. Time to login again
        this.session = init(username, password, false);

        // now that we have logged in again, let's try to validate this new session.
        if (validateSession(10, 2000)) return true; // great

        return false; // this isn't going anywhere
    }

    private boolean validateSession(int NUM_RETRIES, int SLEEP_TIME) throws Exception {
        if (!isNetworkOn()) return false;

        for (int retry = 0; retry < NUM_RETRIES; retry++) {
            HttpPost post = new HttpPost(getUrl() + "?op=touch", "UTF-8");
            post.addFormField("sessionId", Long.toString(session));
            String json = post.finish();
            JSONObject obj = new JSONObject(json);
            String err = obj.has("errormsg") ? obj.getString("errormsg") : null;
            if (err == null || err.length() == 0) return true;

            Thread.sleep(SLEEP_TIME); // ok, wait a little while, then try again
        }
        return false;
    }

    private static String getUrl() {
        return "http://10.0.3.2:8888/tutorialauth";
    }

}
