package cs496.whatsinyourfridge;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.PrintWriter;
import java.io.StringWriter;

import static cs496.whatsinyourfridge.R.id.edit_message;

public class WhatsInYourFridge extends AppCompatActivity implements ServiceConnection, SearchService.Callback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whats_in_your_fridge);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // make sure the service is running
        //Context app = getApplicationContext();
       // Intent intent = new Intent(app, SearchService.class);
        //app.startService(intent);
        View v = findViewById(R.id.mainLayoutID);
        doGet(v);
    }
    public void doGet(View v) {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                setText("Please wait...");
            }

            protected String doInBackground(Void... params) {
                HttpGet http = new HttpGet("http://food2fork.com/api/search?key=8fb888939f3d819b54a8c4f41cf9822f&q=shredded%20chicken");
                //http.addFormField("university", "Oregon State University");
                //http.addFormField("course", "mobile and cloud development");
                try {
                    return http.finish();
                } catch (Exception e) {
                    return formatError(e);
                }
            }

            protected void onPostExecute(String txt) {
                setText(txt);
            }
        }.execute();
    }
    private SearchService service;
    @Override
    protected void onStart() {
        super.onStart();
        // get a reference to the service, for receiving messages
        Context app = getApplicationContext();
        Intent intent = new Intent(app, SearchService.class);
       bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    public void onServiceConnected(ComponentName name, IBinder binder) {
        // called when bindService succeeds
        service = ((SearchService.SearchServiceBinder) binder).getService();
        service.setListener(this);
        updateLabels();
    }

    private void updateLabels() {

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // let's disconnect from the service; it keeps running, though
       if (service != null)
           unbindService(this);
    }

    public void onServiceDisconnected(ComponentName name) {
        // called when unbindService succeeds
        if (service != null)
            service.setListener(null);
        service = null;
        updateLabels();
    }

    /** Called when the user clicks the Send button */
    public void add_item(View view) {
        EditText text = (EditText)findViewById(edit_message);
        String str = text.getText().toString();
        //Context context = getApplicationContext();
        //int duration = Toast.LENGTH_SHORT;
       // Toast toast = Toast.makeText(context, str, duration);
        //toast.show();

        TextView textView = new TextView(this);
        textView.setText(str);

        LinearLayout ll= (LinearLayout) findViewById(R.id.mainLayoutID);
        ll.addView(textView);
    }

    @Override
    public void onMeasurement(String recipe) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, "Pizza!", duration);
        toast.show();
    }

    private void setText(final String str) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            TextView txtResult = (TextView) (findViewById(R.id.edit_message));
            if (txtResult != null) {
               // Context context = getApplicationContext();
               // int duration = Toast.LENGTH_SHORT;
               // Toast toast = Toast.makeText(context, "setting text", duration);
               // toast.show();
                txtResult.setText(str);
                txtResult.setMovementMethod(new ScrollingMovementMethod());
                txtResult.scrollTo(0, 0);
            }
        } else {
            runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            setText(str);
                        }
                    }
            );
        }
    }

    private String formatError(Exception ex) {
        StringWriter tmp = new StringWriter();
        tmp.append("An exception has occurred...\n");
        ex.printStackTrace(new PrintWriter(tmp));
        return tmp.toString();
    }
}

