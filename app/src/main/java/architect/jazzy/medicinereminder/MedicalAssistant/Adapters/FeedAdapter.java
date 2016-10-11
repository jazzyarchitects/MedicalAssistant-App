package architect.jazzy.medicinereminder.MedicalAssistant.Adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.ArrayList;

import architect.jazzy.medicinereminder.MedicalAssistant.Fragments.NewsFragments.FeedFragment;
import architect.jazzy.medicinereminder.MedicalAssistant.Models.FeedItem;

/**
 * Created by Jibin_ism on 02-Jul-15.
 */
public class FeedAdapter extends FragmentStatePagerAdapter {

  ArrayList<FeedItem> feeds;

  public FeedAdapter(FragmentManager fm, ArrayList<FeedItem> feeds) {
    super(fm);
    if (feeds != null)
      if (feeds.size() > 0)
        Log.e("FeedAdapter", "Got feeds: " + feeds.get(0).getTitle());
    this.feeds = feeds;
  }

  @Override
  public Fragment getItem(int position) {
    return FeedFragment.newInstance(feeds.get(position));
  }

  @Override
  public int getCount() {
    return feeds.size();
  }
}
