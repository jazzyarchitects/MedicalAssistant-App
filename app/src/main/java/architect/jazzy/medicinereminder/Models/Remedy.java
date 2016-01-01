package architect.jazzy.medicinereminder.Models;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import architect.jazzy.medicinereminder.HelperClasses.BackendUrls;

/**
 * Created by Jibin_ism on 25-Dec-15.
 */
public class Remedy implements Parcelable {
    private User author;
    
    private Bitmap remedyImage=null;
    
    private String id, title, description, publishedOn;
    private ArrayList<String> diseases, tags, references;
    private Stats stats;
    private Image image;

    boolean checked=false;

    public Remedy() {
        stats=new Stats();
        image=new Image();
        diseases=new ArrayList<>();
        tags=new ArrayList<>();
        author=new User();
        references=new ArrayList<>();
    }



    protected Remedy(Parcel in) {
        stats=new Stats();
        image=new Image();
        diseases=new ArrayList<>();
        tags=new ArrayList<>();
        author=new User();
        references=new ArrayList<>();

        this.setId(in.readString());
        author.setId(in.readString());
        author.setName(in.readString());

        this.setTitle(in.readString());
        this.setDescription(in.readString());
        this.setPublishedOn(in.readString());

        in.readStringList(references);
        in.readStringList(tags);
        in.readStringList(diseases);

        image.setFileName(null, in.readString());
        image.setPath(in.readString());

        stats.setComments(in.readInt());
        stats.setDownvote(in.readInt());
        stats.setUpvote(in.readInt());
        stats.setViews(in.readInt());

    }

    public static final Creator<Remedy> CREATOR = new Creator<Remedy>() {
        @Override
        public Remedy createFromParcel(Parcel in) {
            return new Remedy(in);
        }

        @Override
        public Remedy[] newArray(int size) {
            return new Remedy[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        User author = this.getAuthor();

        parcel.writeString(this.getId());

        parcel.writeString(author.getId());
        parcel.writeString(author.getName());

        parcel.writeString(this.getTitle());
        parcel.writeString(this.getDescription());
        parcel.writeString(this.getPublishedOn());

        parcel.writeStringList(this.getReferences());
        parcel.writeStringList(this.getTags());
        parcel.writeStringList(this.getDiseases());

        parcel.writeString(this.getImage().getFileName());
        parcel.writeString(this.getImage().getPath());

        parcel.writeInt(this.getStats().getComments());
        parcel.writeInt(this.getStats().getDownvote());
        parcel.writeInt(this.getStats().getUpvote());
        parcel.writeInt(this.getStats().getViews());


    }

    public static Remedy parseFeedData(Context context,JSONObject jsonObject){
        Remedy remedy=new Remedy();
        try{
            User author = remedy.getAuthor();

            JSONObject authorObject = jsonObject.getJSONObject("author");
            author.setId(authorObject.getString("_id"));
            author.setName(authorObject.getString("name"));
            remedy.setAuthor(author);

            remedy.setId(jsonObject.getString("_id"));
            remedy.setDescription(jsonObject.getString("description"));
            remedy.setTitle(jsonObject.getString("title"));
            remedy.setPublishedOn(jsonObject.getString("publishedOn"));
            
            ArrayList<String> diseases = new ArrayList<>();
            JSONArray diseasesArray= jsonObject.optJSONArray("diseases");
            if(diseasesArray!=null && diseasesArray.length()!=0){
                for (int i = 0; i < diseasesArray.length(); i++) {
                    diseases.add(diseasesArray.getString(i));
                }
            }
            remedy.setDiseases(diseases);
            
            ArrayList<String> tags = new ArrayList<>();
            JSONArray tagsArray= jsonObject.optJSONArray("tags");
            if(tagsArray!=null && tagsArray.length()!=0){
                for (int i = 0; i < tagsArray.length(); i++) {
                    diseases.add(tagsArray.getString(i));
                }
            }
            remedy.setTags(tags);

            ArrayList<String> references = new ArrayList<>();
            JSONArray referencesArray= jsonObject.optJSONArray("references");
            if(referencesArray!=null && referencesArray.length()!=0){
                for (int i = 0; i < referencesArray.length(); i++) {
                    diseases.add(referencesArray.getString(i));
                }
            }
            remedy.setReferences(references);

            Image image=remedy.getImage();
            JSONObject imageObject = jsonObject.optJSONObject("image");
            if(imageObject!=null){
                image.setFileName(context, imageObject.optString("filename"));
                image.setPath(imageObject.optString("path"));
            }
            remedy.setImage(image);

            Stats stats=remedy.getStats();
            JSONObject statsObject = jsonObject.getJSONObject("stats");

            stats.setComments(statsObject.optInt("comments"));
            stats.setDownvote(statsObject.optInt("downvote"));
            stats.setUpvote(statsObject.optInt("upvote"));
            stats.setViews(statsObject.optInt("views"));

            remedy.setStats(stats);

        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
        return remedy;
    }

    public void loadRemedyImage(Context context){
        this.getImage().setFileName(context, this.getImage().getFileName());
    }



    /**Getter Setter methods**/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublishedOn() {
        return publishedOn;
    }

    public void setPublishedOn(String publishedOn) {
        this.publishedOn = publishedOn;
    }

    public ArrayList<String> getDiseases() {
        return diseases;
    }

    public void setDiseases(ArrayList<String> diseases) {
        this.diseases = diseases;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public ArrayList<String> getReferences() {
        return references;
    }

    public void setReferences(ArrayList<String> references) {
        this.references = references;
    }


    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getDiseasesString(){
        String s="";
        if(diseases.size()==0){
            return "";
        }
        s=diseases.get(0);
        for(int i=1;i<diseases.size();i++){
            s+=", "+diseases.get(i);
        }
        return s;
    }

    public class Image{
        private String fileName="", path="";

        public Image() {
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(final Context context, final String fileName) {

            if(!fileName.equalsIgnoreCase(this.fileName) && context!=null){
                //TODO: Download image
                Thread thread=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            remedyImage=Picasso.with(context)
                                    .load(BackendUrls.REMEDY_IMAGE+fileName)
                                    .get();
                            if(imageLoadListener!=null){
                                imageLoadListener.onImageDownloaded(remedyImage);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
            this.fileName = fileName;

        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    public class Stats{
        private int upvote=0;
        private int downvote=0;
        private int comments=0;

        public int getViews() {
            return views;
        }

        public void setViews(int views) {
            this.views = views;
        }

        private int views=0;

        public Stats() {
        }

        public int getUpvote() {
            return upvote;
        }

        public void setUpvote(int upvote) {
            this.upvote = upvote;
        }

        public int getDownvote() {
            return downvote;
        }

        public void setDownvote(int downvote) {
            this.downvote = downvote;
        }

        public int getComments() {
            return comments;
        }

        public void setComments(int comments) {
            this.comments = comments;
        }
    }

    public Bitmap getRemedyImage() {
        return remedyImage;
    }

    public void setRemedyImage(Bitmap remedyImage) {
        this.remedyImage = remedyImage;

    }

    private ImageLoadListener imageLoadListener;
    public interface ImageLoadListener{
        void onImageDownloaded(Bitmap bitmap);
    }

    public void setImageLoadListener(ImageLoadListener imageLoadListener){
        this.imageLoadListener=imageLoadListener;
    }

}
