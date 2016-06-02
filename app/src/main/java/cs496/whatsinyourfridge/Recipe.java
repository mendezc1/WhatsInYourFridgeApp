package cs496.whatsinyourfridge;

import org.json.JSONException;
import org.json.JSONObject;


public class Recipe {
    public Recipe() {
        setWhen(System.currentTimeMillis());
    }

    private String title;

    private Long when;

    private String source_url;

    private String img_url;

    public String getSource_url() {
        return source_url != null ? source_url : "";
    }

    public String getImg_url() {
        return img_url != null ? img_url : "";
    }

    public String getTitle() {
        return title != null ? title : "";
    }

    public long getWhen() {
        return when != null ? when.longValue() : 0L;
    }

    public void setSource_url(String source_url) {this.source_url = (source_url != null ? source_url : "");}

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public void setTitle(String title) {
        this.title = title != null ? title : "";
    }

    public void setWhen(long when) {
        this.when = when;
    }

    public String toJson() throws JSONException {
        JSONObject tmp = new JSONObject();
        tmp.put("when", getWhen());
        tmp.put("title", getTitle());
        tmp.put("source_url", getSource_url());
        tmp.put("img_url", getImg_url());
        return tmp.toString();

    }

    public static Recipe fromJson(String json) throws JSONException {
        JSONObject tmp = new JSONObject(json);
        Recipe entry = new Recipe();
        entry.setWhen(tmp.getLong("when"));
        entry.setTitle(tmp.getString("title"));
        entry.setSource_url(tmp.getString("source_url"));
        entry.setImg_url(tmp.getString("img_url"));
        return entry;
    }
}
