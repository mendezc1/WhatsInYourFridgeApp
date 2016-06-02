package cs496.whatsinyourfridge;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import eecs.oregonstate.edu.tutorialauth.R;


public class WhatsInYourFridge extends AppCompatActivity implements BufferThread.StatusListener  {
    String ingredients = " ";
    String mostRecent = " ";
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
        setContentView(R.layout.content_whats_in_your_fridge);
        Context app = getApplicationContext();
        SharedPreferences sharedPref = app.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        mostRecent = sharedPref.getString(getString(R.string.ingredientList), " ");

        Log.d("most recent", mostRecent);
        if(mostRecent.length() > 2) {
            View v = findViewById(R.id.ingredient_list_ll);
            doGet(v);
        }
        buffer = new BufferThread(this, new FileManager(this), new Server(this, username, password, session));
        buffer.start();
    }
    private BufferThread buffer;

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            Log.d("error", String.valueOf(e));
            return null;
        }
    }

    public void doGet(View v) {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                //addRecipe("Please wait...");
            }

            protected String doInBackground(Void... params) {
                HttpGet http = new HttpGet("http://food2fork.com/api/search");
                http.addFormField("key", "8fb888939f3d819b54a8c4f41cf9822f");
                if(ingredients == " "){
                    http.addFormField("q", mostRecent);
                }
                else {
                    http.addFormField("q", ingredients);
                }

                try {
                    String rvString = http.finish();
                    int titleIndex = rvString.indexOf("title");
                    int endOfTitle = rvString.indexOf("source_url");
                    int sourceIndex = endOfTitle;
                    int endOfSource = rvString.indexOf("recipe_id");
                    String titleSlice = rvString.substring(titleIndex+9, endOfTitle-4);
                    String sourceSlice = rvString.substring(sourceIndex+14, endOfSource-4);
                    int imageStart = rvString.indexOf("image_url");
                    int imageEnd = rvString.indexOf("social_rank");
                    String imageSlice = rvString.substring(imageStart+13, imageEnd-4);
                    Log.d("source_url ", sourceSlice);
                    Log.d("Image_url", imageSlice);

                   // addImage(imageSlice);
                    addRecipe(imageSlice, "image");
                    addRecipe(titleSlice, "title");
                    addRecipe(sourceSlice, "url");

                    return "thisshoulddonothing";
                } catch (Exception e) {
                    return formatError(e);
                }
            }

            protected void onPostExecute(String txt) {
                //addRecipe(txt, "title");
            }
        }.execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (buffer != null) {
            if (!buffer.isInterrupted())
                buffer.interrupt();
            buffer.cleanup();
        }
        String filename = "mostRecentSearch";
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(ingredients.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void report(String msg) {
        UiUtil.toastOnUiThread(this, msg);
    }

    public void doSave(View v) {
        try {
            Recipe entry = new Recipe();
            entry.setTitle(UiUtil.readText(this, R.id.txtTitle));
            if (entry.getTitle().length() == 0)
                throw new IllegalArgumentException("Please enter an ingredient.");
            entry.setSource_url(UiUtil.readText(this, R.id.txtBlather));


            String tags = "";
            if (UiUtil.readChk(this, R.id.chkIronic)) tags += "ironic ";
            if (UiUtil.readChk(this, R.id.chkSerious)) tags += "serious ";
            if (UiUtil.readChk(this, R.id.chkSilly)) tags += "silly ";
            entry.setImg_url(tags);
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
    /** Called when the user clicks the Add button */
    public void add_item(View view) {
        EditText text = (EditText) findViewById(R.id.edit_message);
        String str = text.getText().toString();
        ingredients += ", " + str;
        View v = findViewById(R.id.recipe_list_ll);
        doGet(v);
        TextView textView = new TextView(this);
        textView.setText(str);

        LinearLayout ll= (LinearLayout) findViewById(R.id.mainLayoutID);
        ll.addView(textView);

       Context app = getApplicationContext();
        SharedPreferences sharedPref = app.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.ingredientList), ingredients);
        editor.commit();
    }

    private void addImage(final String img_url){
   /*
        if (Looper.getMainLooper() == Looper.myLooper()) {
            Log.d("HELLO", "in add image");
            LinearLayout linear = (LinearLayout) findViewById(R.id.recipe_list_ll);
            ImageView image = new ImageView(WhatsInYourFridge.this);
            image.setImageResource(R.mipmap.ic_launcher);
            linear.addView(image);

            Bitmap bitmap = getBitmapFromURL(img_url);
            image.setImageBitmap(bitmap);
            //  image.getLayoutParams().height = 600;
            //  image.getLayoutParams().width = 1000;
            Log.d("HELLO1234 ", String.valueOf(image));
        }*/
     /*   else{
            runOnUiThread(new Runnable() {
                @Override public void run() {
                   addImage(img_url);
                }
            });
        }
*/
    }
    private void addRecipe(final String str, final String type) {
        if (Looper.getMainLooper() == Looper.myLooper()) {


            if(type == "url"){
                TextView txtResult = new TextView(this);
                txtResult.setText(str);
             txtResult.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(str));
                     startActivity(browserIntent);
                 }
             });
                LinearLayout ll= (LinearLayout) findViewById(R.id.recipe_list_ll);
                ll.addView(txtResult);
                if (txtResult != null) {
                    txtResult.setText(str);
                    txtResult.setMovementMethod(new ScrollingMovementMethod());
                    txtResult.scrollTo(0, 0);
                }
            }
            else if(type == "img"){
                Log.d("HELLO", "in add image");
                LinearLayout linear = (LinearLayout) findViewById(R.id.recipe_list_ll);
                ImageView image = new ImageView(WhatsInYourFridge.this);
                image.setImageResource(R.mipmap.ic_launcher);
                linear.addView(image);

                Bitmap bitmap = getBitmapFromURL(str);
                image.setImageBitmap(bitmap);
                //  image.getLayoutParams().height = 600;
                //  image.getLayoutParams().width = 1000;
                Log.d("HELLO1234 ", String.valueOf(image));
            }
            else if(type == "title"){
                TextView txtResult = new TextView(this);
                txtResult.setText(str);
                LinearLayout ll= (LinearLayout) findViewById(R.id.recipe_list_ll);
                ll.addView(txtResult);
                if (txtResult != null) {
                    txtResult.setText(str);
                    txtResult.setMovementMethod(new ScrollingMovementMethod());
                    txtResult.scrollTo(0, 0);
                }
            }

        } else {
            runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            addRecipe(str, type);
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

