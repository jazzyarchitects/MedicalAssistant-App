package architect.jazzy.medicinereminder.MedicalAssistant.Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.text.Spanned;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jibin_ism on 19-Aug-15.
 */
public class WebDocument implements Parcelable {

    String title,organizationName, snippet,url;
    String  fullSummary;
    ArrayList<String> mesh,groupName,altTitle;

    public WebDocument() {
        mesh=new ArrayList<>();
        groupName=new ArrayList<>();
        altTitle=new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getAltTitle() {
        return altTitle;
    }

    public void addAltTitle(String altTitle) {
        this.altTitle.add(altTitle);
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Spanned getFullSummary() {
        return Html.fromHtml(fullSummary);
    }

    public void setFullSummary(String fullSummary) {
        this.fullSummary = fullSummary;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public void addMesh(String mesh) {
        this.mesh.add(mesh);
    }

    public JSONObject toJSON(){
        JSONObject jsonObject=new JSONObject();
        try{
            jsonObject.put("title",getTitle());
            jsonObject.put("url",getUrl());
            jsonObject.put("organization",getOrganizationName());
            jsonObject.put("alt",getAltTitle().get(0));
            jsonObject.put("full",getFullSummary());
        }catch (JSONException e){
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static WebDocument parse(JSONObject jsonObject){
        WebDocument webDocument=new WebDocument();
        try{
            webDocument.setTitle(jsonObject.optString("title"));
            webDocument.setUrl(jsonObject.optString("url"));
            webDocument.setAltTitle(jsonObject.optString("alt"));
            webDocument.setOrganizationName(jsonObject.optString("organization"));
            webDocument.setFullSummary(jsonObject.optString("full"));
        }catch (Exception e){
            e.printStackTrace();
        }
        return webDocument;
    }


    public void setAltTitle(String string){
        this.altTitle=new ArrayList<>();
        this.altTitle.add(string);
    }

    public void addGroupName(String groupName) {
        this.groupName.add(groupName);
    }

    @Override
    public String toString() {
        return this.getTitle()+" -- "+this.getOrganizationName();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeStringList(this.altTitle);
        dest.writeString(this.organizationName);
        dest.writeString(this.url);
        dest.writeString(this.snippet);
        dest.writeString(this.fullSummary);
        dest.writeStringList(this.groupName);
        dest.writeStringList(this.mesh);
    }

    private WebDocument(Parcel in){
        this.title=in.readString();
        in.readStringList(this.altTitle);
        this.organizationName=in.readString();
        this.url=in.readString();
        this.snippet=in.readString();
        this.fullSummary=in.readString();
        in.readStringList(this.groupName);
        in.readStringList(this.mesh);
    }

    public static final Parcelable.Creator<WebDocument> CREATOR = new Parcelable.Creator<WebDocument>() {
        @Override
        public WebDocument createFromParcel(Parcel source) {
            return new WebDocument(source);
        }

        @Override
        public WebDocument[] newArray(int size) {
            return new WebDocument[size];
        }
    };
}
