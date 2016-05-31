/*
package cs496.whatsinyourfridge;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import eecs.oregonstate.edu.tutorialauth.R;

public class LimerickActivity extends AppCompatActivity implements BufferThread.StatusListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");
        long session = intent.getLongExtra("session", 0L);

        if (username == null || password == null || session <= 0L) {
            UiUtil.toastOnUiThread(this, "Error: username, password, session");
            return;
        }

        setContentView(R.layout.activity_limerick);
        buffer = new BufferThread(this, new FileManager(this), new Server(this, username, password, session));
        buffer.start();
    }

    private BufferThread buffer;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (buffer != null) {
            if (!buffer.isInterrupted())
                buffer.interrupt();
            buffer.cleanup();
        }
    }


    @Override
    public void report(String msg) {
        UiUtil.toastOnUiThread(this, msg);
    }

    public void doSave(View v) {
        try {
            Limerick entry = new Limerick();
            entry.setTitle(UiUtil.readText(this, R.id.txtTitle));
            if (entry.getTitle().length() == 0)
                throw new IllegalArgumentException("Please enter a title.");
            entry.setBlather(UiUtil.readText(this, R.id.txtBlather));
            if (entry.getBlather().length() == 0)
                throw new IllegalArgumentException("Please enter a limerick.");

            String tags = "";
            if (UiUtil.readChk(this, R.id.chkIronic)) tags += "ironic ";
            if (UiUtil.readChk(this, R.id.chkSerious)) tags += "serious ";
            if (UiUtil.readChk(this, R.id.chkSilly)) tags += "silly ";
            entry.setTags(tags);
            BufferThread tmp = buffer;
            if (tmp != null) {
                tmp.write(entry);

                // reset the screen
                UiUtil.writeText(this, R.id.txtTitle, "");
                UiUtil.writeText(this, R.id.txtBlather, "");
                UiUtil.writeChk(this, R.id.chkSilly, false);
                UiUtil.writeChk(this, R.id.chkIronic, false);
                UiUtil.writeChk(this, R.id.chkSerious, false);
            } else
                report("Unable to save your work, right now. Sorry!");
        } catch (IllegalArgumentException ex) {
            report(ex.getMessage());
        }
    }


}
*/