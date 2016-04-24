package cs496.whatsinyourfridge;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.view.ViewGroup;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextInfo;
import android.view.textservice.TextServicesManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SpellCheckerActivity extends AppCompatActivity implements SpellCheckerSession.SpellCheckerSessionListener {

    EditText ed1;
    private SpellCheckerSession mScs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        ed1=(EditText)findViewById(R.id.edit_message);
        final TextServicesManager tsm = (TextServicesManager) getSystemService(
                Context.TEXT_SERVICES_MANAGER_SERVICE);
        mScs = tsm.newSpellCheckerSession(null, null, this, true);
    }

    @Override
    public void onGetSuggestions(SuggestionsInfo[] results) {
        Toast.makeText(getApplicationContext(), ed1.getText().toString(), Toast.LENGTH_SHORT).show();
        mScs.getSuggestions(new TextInfo(ed1.getText().toString()), 3);
    }

    @Override
    public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] results) {
        //Nothing here but android studio complains if this stub is deleted
    }
}
