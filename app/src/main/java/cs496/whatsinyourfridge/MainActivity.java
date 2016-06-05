package cs496.whatsinyourfridge;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import eecs.oregonstate.edu.tutorialauth.R;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void hitServer(View v) {
        try {
            final String username = UiUtil.readText(this, R.id.txtUsername);
            if (username.length() == 0)
                throw new IllegalArgumentException("Please enter a username.");
            final String password = UiUtil.readText(this, R.id.txtPassword);
            if (password.length() == 0)
                throw new IllegalArgumentException("Please enter a password.");

            final boolean reg = (v.getId() == R.id.btnRegister);
            new AsyncTask<Void, Void, Long>() {
                protected void onPreExecute() {
                    UiUtil.writeText(MainActivity.this, R.id.txtStatus, "Hold on a sec...");
                    UiUtil.enableView(MainActivity.this, R.id.btnRegister, false);
                    UiUtil.enableView(MainActivity.this, R.id.btnLogin, false);
                }

                @Override
                protected Long doInBackground(Void... params) {
                    try {
                        return Server.init(username, password, reg);
                    } catch (Exception e) {
                        UiUtil.toastOnUiThread(MainActivity.this, "Error: " + e.getMessage());
                        return 0L;
                    }
                }

                protected void onPostExecute(Long sessionId) {
                    if (sessionId > 0) {
                        UiUtil.writeText(MainActivity.this, R.id.txtStatus, "");
                        startWhatsInYourFridge(username, password, sessionId);
                    } else {
                        UiUtil.writeText(MainActivity.this, R.id.txtStatus, "Please try again...");
                    }
                    UiUtil.enableView(MainActivity.this, R.id.btnRegister, true);
                    UiUtil.enableView(MainActivity.this, R.id.btnLogin, true);
                }
            }.execute();
        } catch (IllegalArgumentException e) {
            UiUtil.toastOnUiThread(this, e.getMessage());
        }
    }

    private void startWhatsInYourFridge(String username, String password, long sessionId) {
        Intent intent = new Intent(this, WhatsInYourFridge.class);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        intent.putExtra("session", sessionId);
        startActivity(intent);
    }

}
