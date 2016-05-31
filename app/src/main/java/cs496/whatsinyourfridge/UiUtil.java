package cs496.whatsinyourfridge;

import android.app.Activity;
import android.os.Looper;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;


public class UiUtil {

    public static String readText(Activity activity, int id) {
        TextView view = (TextView) activity.findViewById(id);
        if (view != null) {
            CharSequence rv = view.getText();
            if (rv != null)
                return rv.toString();
        }
        return "";
    }

    public static void writeText(Activity activity, int id, String vl) {
        TextView view = (TextView) activity.findViewById(id);
        if (view != null)
            view.setText(vl);
    }

    public static boolean readChk(Activity activity, int id) {
        CheckBox view = (CheckBox) activity.findViewById(id);
        if (view != null) {
            return view.isChecked();
        }
        return false;
    }

    public static void enableView(Activity activity, int id, boolean vl) {
        View view = activity.findViewById(id);
        if (view != null)
            view.setEnabled(vl);
    }


    public static void writeChk(Activity activity, int id, boolean vl) {
        CheckBox view = (CheckBox) activity.findViewById(id);
        if (view != null)
            view.setChecked(vl);
    }

    public static void toastOnUiThread(final Activity activity, final String msg) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
        } else {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    toastOnUiThread(activity, msg);
                }
            });
        }
    }

}
