package cs496.whatsinyourfridge;

import android.content.Context;

import org.json.JSONException;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileManager {
    private Context context;

    FileManager(Context context) {
        this.context = context;
    }

    private static final String EXT_PREPARING = ".writing";
    private static final String EXT_SENT = ".sent";

    private File computeFilename(long when, String title, String status) {
        if (title == null) title = "";
        StringBuffer tmp = new StringBuffer();
        tmp.append(when);
        tmp.append('-');
        for (int i = 0; i < title.length(); i++) {
            char ch = tmp.charAt(i);
            if (Character.isLetterOrDigit(ch))
                tmp.append(ch);
            else
                tmp.append('_');
        }
        tmp.append(status);
        return new File(context.getFilesDir(), tmp.toString());
    }


    void write(Recipe entry) throws IOException, JSONException {
        File file = computeFilename(entry.getWhen(), entry.getTitle(), EXT_PREPARING);
        FileWriter writer = new FileWriter(file);
        writer.write(entry.toJson());
        writer.flush();
        writer.close();
    }

    private Recipe read(File file) throws FileNotFoundException, JSONException {
        String json = new Scanner(file).useDelimiter("\\Z").next();
        return Recipe.fromJson(json);
    }

    List<Recipe> loadUnsent() throws FileNotFoundException, JSONException {
        List<Recipe> rv = new ArrayList<Recipe>();
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(EXT_PREPARING);
            }
        };
        File[] files = context.getFilesDir().listFiles(filter);
        if (files != null) {
            for (File file : files) {
                Recipe entry = read(file);
                rv.add(entry);
            }
        }
        return rv;
    }

    void markSent(Recipe entry) {
        File unsentFilename = this.computeFilename(entry.getWhen(), entry.getTitle(), EXT_PREPARING);
        File sentFilename = this.computeFilename(entry.getWhen(), entry.getTitle(), EXT_SENT);
        unsentFilename.renameTo(sentFilename);
    }

}
