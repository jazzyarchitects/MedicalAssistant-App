package architect.jazzy.medicinereminder.MedicalAssistant.Adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

import architect.jazzy.medicinereminder.MedicalAssistant.Fragments.MedicineDetailFragment;
import architect.jazzy.medicinereminder.MedicalAssistant.Models.Medicine;

/**
 * Created by Jibin_ism on 25-Mar-15.
 */
public class MedicineDetailsViewPagerAdapter extends android.support.v13.app.FragmentStatePagerAdapter {

  HashMap<Integer, MedicineDetailFragment> fragmentMap = new HashMap<>();
  private ArrayList<Medicine> dataSet;

  public MedicineDetailsViewPagerAdapter(FragmentManager fm, ArrayList<Medicine> medNames) {
    super(fm);
    fragmentMap.clear();
//        Log.e("Fragment map",fragmentMap.size()+"  "+fragmentMap.toString());
    this.dataSet = medNames;
  }

  @Override
  public Fragment getItem(int position) {
    MedicineDetailFragment medFragment = MedicineDetailFragment.newInstance(dataSet.get(position));
    fragmentMap.put(position, medFragment);

//        Log.e("Fragment map",fragmentMap.size()+"  "+fragmentMap.toString());
    return medFragment;
  }


  @Override
  public int getCount() {
    return dataSet.size();
  }


  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    super.destroyItem(container, position, object);
  }

  public MedicineDetailFragment getFragment(int key) {
    return fragmentMap.get(key);
  }

}
