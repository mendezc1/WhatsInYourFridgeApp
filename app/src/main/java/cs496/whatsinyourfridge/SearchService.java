package cs496.whatsinyourfridge;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SearchService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // returns an object that can return a reference to the service
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, "hello", duration);
        toast.show();
        return new SearchServiceBinder();
    }

    private WhatsInYourFridge listener;
    public void setListener(WhatsInYourFridge listener) {
        this.listener = listener;
    }

    public class SearchServiceBinder extends Binder {
        public SearchService getService() {
            return SearchService.this;
        }
    }

    private static final String BASE_URL = "http://food2fork.com/api/search";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    /*** Specify a means for the activity to receive measurements back ***/
    public interface Callback {
        public void onMeasurement(String recipe);
    }

    private Callback callback;

    public void setListener(Callback callback) {
        this.callback = callback;
    }

    private void sendRecipe(String recipe) {
        Callback cb = this.callback;
        if (cb != null) cb.onMeasurement(recipe);
    }

    private class RecipeSearch extends Thread {

        @Override
        public void run() {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, "I'm running", duration);
            toast.show();
            synchronized (this) {
                try {
                    context = getApplicationContext();
                    duration = Toast.LENGTH_SHORT;
                    toast = Toast.makeText(context, "hello from recipe search", duration);
                    toast.show();
                } catch (SecurityException e) {
                    e.printStackTrace();
                    throw new IllegalStateException("Cannnot start the sampler due to " + e);
                }
            }
            while (!isInterrupted()) {
                try {
                    Thread.sleep(1000);
                    synchronized (this) {
                        if (true) {
                            sendRecipe("RECIPE");
                        }
                    }
                } catch (InterruptedException e) {
                    cleanup();
                    break; // exiting the loop
                }
            }
        }

        public synchronized void cleanup() {

        }
    }
}
