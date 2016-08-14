package architect.jazzy.medicinereminder.MedicalAssistant.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Jibin_ism on 02-Jul-15.
 */
public class FeedItem implements Parcelable{
    String title, url, description;
    Date modified=new Date();

    public FeedItem() {
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public FeedItem(String title, String url, String description, Date modified) {
        this.title = title;
        this.url = url;
        this.description = description;
        this.modified = modified;
    }

    public FeedItem(String title, String url, String description) {
        this.title = title;
        this.url = url;
        this.description = description;
    }

    private FeedItem(Parcel in) {
        title = in.readString();
        url = in.readString();
        description = in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(description);
    }

    public static final Parcelable.Creator<FeedItem> CREATOR = new Parcelable.Creator<FeedItem>() {
        @Override
        public FeedItem createFromParcel(Parcel source) {
            return new FeedItem(source);
        }

        @Override
        public FeedItem[] newArray(int size) {
            return new FeedItem[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
