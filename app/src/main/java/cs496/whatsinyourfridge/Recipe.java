package cs496.whatsinyourfridge;

import org.json.JSONException;
import org.json.JSONObject;


public class Recipe {
    public Recipe() {
        setWhen(System.currentTimeMillis());
    }

    private String title;

    private Long when;

    private String blather;

    private String tags;

    public String getBlather() {
        return blather != null ? blather : "";
    }

    public String getTags() {
        return tags != null ? tags : "";
    }

    public String getTitle() {
        return title != null ? title : "";
    }

    public long getWhen() {
        return when != null ? when.longValue() : 0L;
    }

    public void setBlather(String blather) {
        this.blather = (blather != null ? blather : "");
    }

    public void setTags(String tags) {
        this.tags = tags;
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
        tmp.put("blather", getBlather());
        tmp.put("tags", getTags());
        return tmp.toString();

    }

    public static Recipe fromJson(String json) throws JSONException {
        JSONObject tmp = new JSONObject(json);
        Recipe entry = new Recipe();
        entry.setWhen(tmp.getLong("when"));
        entry.setTitle(tmp.getString("title"));
        entry.setBlather(tmp.getString("blather"));
        entry.setTags(tmp.getString("tags"));
        return entry;

    }
}
