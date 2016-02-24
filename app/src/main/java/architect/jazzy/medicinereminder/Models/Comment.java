package architect.jazzy.medicinereminder.Models;

import org.json.JSONObject;

/**
 * Created by Jibin_ism on 24-Jan-16.
 */
public class Comment {
    private String id, comment;
    private User user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static Comment parseJSON(JSONObject jsonObject){
        Comment comment=new Comment();
        try {
            comment.setId(jsonObject.getString("_id"));
            comment.setComment(jsonObject.getString("comment"));
            JSONObject authorObject = jsonObject.getJSONObject("author");
            User user = new User(authorObject.getString("_id"), authorObject.getString("name"));
            comment.setUser(user);
        }catch (Exception e){
            e.printStackTrace();
        }
        return comment;
    }
}
