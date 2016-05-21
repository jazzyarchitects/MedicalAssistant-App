package architect.jazzy.medicinereminder.Models;

import java.util.ArrayList;

/**
 * Created by Jibin_ism on 08-Jan-16.
 */
public class RemedyFeedResult {

    ArrayList<Remedy> remedies;
    int currentPage=1;
    int count=0;
    int limit=10;

    public RemedyFeedResult() {
        remedies=new ArrayList<>();
    }

    public int getNextPageNumber(){
        return currentPage+1;
    }

    public ArrayList<Remedy> getRemedies() {
        return remedies;
    }

    public void setRemedies(ArrayList<Remedy> remedies) {
        this.remedies = remedies;
    }

    public void addRemedies(ArrayList<Remedy> remedies) {
        this.remedies.addAll(remedies);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
