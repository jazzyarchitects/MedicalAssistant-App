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
import architect.jazzy.medicinereminder.Services.BackendInterfacer;

/**
 * Created by Jibin_ism on 25-Dec-15.
 */
public class Remedy implements Parcelable {
    private User author;
    
    private Bitmap remedyImage=null;
    
    private String id, title, description, publishedOn;
    private ArrayList<String> diseases, tags, references;
    private ArrayList<Comment> comments;
    private Stats stats;
    private Image image;
    private int imageIndex =-1;

    boolean upvoted=false;
    boolean downvoted=false;


    boolean bookmarked=false;

    boolean checked=false;

    public Remedy() {
        stats=new Stats();
        image=new Image();
        diseases=new ArrayList<>();
        tags=new ArrayList<>();
        author=new User();
        references=new ArrayList<>();
        comments=new ArrayList<>();
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

        boolean[] arr=new boolean[3];
        in.readBooleanArray(arr);
        this.setUpvoted(arr[0]);
        this.setDownvoted(arr[1]);
        this.setBookmarked(arr[2]);

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


        boolean[] booleen=new boolean[3];
        booleen[0]=this.isUpvoted();
        booleen[1]=this.isDownvoted();
        booleen[2]=this.isBookmarked();
        parcel.writeBooleanArray(booleen);


    }

    public static Remedy parseRemedy(Context context,JSONObject jsonObject){
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
                    tags.add(tagsArray.getString(i));
                }
            }
            remedy.setTags(tags);

            ArrayList<String> references = new ArrayList<>();
            JSONArray referencesArray= jsonObject.optJSONArray("references");
            if(referencesArray!=null && referencesArray.length()!=0){
                for (int i = 0; i < referencesArray.length(); i++) {
                    references.add(referencesArray.getString(i));
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

            remedy.setUpvoted(jsonObject.optBoolean("upvoted"));
            remedy.setDownvoted(jsonObject.optBoolean("downvoted"));
            remedy.setBookmarked(jsonObject.optBoolean("bookmarked"));

            remedy.setStats(stats);

        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
        return remedy;
    }

    public int getImageIndex() {
        return imageIndex;
    }

    public void setImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
    }

    public boolean isBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    public void loadRemedyImage(Context context){
        this.getImage().setFileName(context, this.getImage().getFileName());
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
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


    public boolean isDownvoted() {
        return downvoted;
    }

    public void setDownvoted(boolean downvoted) {
        this.downvoted = downvoted;
    }

    public boolean isUpvoted() {
        return upvoted;
    }

    public void setUpvoted(boolean upvoted) {
        this.upvoted = upvoted;
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


    private String arrayToString(ArrayList<String> strings){
        String s="";
        if(strings==null || strings.size()==0){
            return "";
        }
        s=strings.get(0);
        for(int i=1;i<strings.size();i++){
            s+=", "+strings.get(i);
        }
        return s;
    }

    public String getReferencesString(){
        return arrayToString(references);
    }
    public String getTagsString(){
        return arrayToString(tags);
    }
    public String getDiseasesString(){
        return arrayToString(diseases);
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
                                    .load(BackendUrls.getRemedyImage(fileName))
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



    public void upvote(Context context){
        BackendInterfacer interfacer = new BackendInterfacer(context, BackendUrls.getUpvoteUrl(this.getId()), "PUT", null);
        interfacer.execute();
    }

    public void downvote(Context context){
        BackendInterfacer interfacer = new BackendInterfacer(context, BackendUrls.getDownvoteUrl(this.getId()), "PUT", null);
        interfacer.execute();
    }

    public void bookmark(Context context){
        BackendInterfacer interfacer = new BackendInterfacer(context, BackendUrls.getBookmarkUrl(this.getId()), "PUT", null);
        interfacer.execute();
    }


}
