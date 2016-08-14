package architect.jazzy.medicinereminder.MedicalAssistant.Adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import architect.jazzy.medicinereminder.MedicalAssistant.Fragments.SearchFragments.SearchDetailFragment;
import architect.jazzy.medicinereminder.MedicalAssistant.Models.WebDocument;

/**
 * Created by Jibin_ism on 21-Aug-15.
 */
public class SearchResultPagerAdapter extends FragmentStatePagerAdapter {

    ArrayList<WebDocument> documents;

    public SearchResultPagerAdapter(FragmentManager fm, ArrayList<WebDocument> documents) {
        super(fm);
        this.documents=documents;
    }

    @Override
    public Fragment getItem(int position) {
        return SearchDetailFragment.newInstance(documents.get(position));
    }

    @Override
    public int getCount() {
        return documents.size();
    }
}
