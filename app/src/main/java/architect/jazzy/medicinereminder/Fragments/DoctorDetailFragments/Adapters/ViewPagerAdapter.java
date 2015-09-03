package architect.jazzy.medicinereminder.Fragments.DoctorDetailFragments.Adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import architect.jazzy.medicinereminder.Fragments.DoctorDetailFragments.ContactDetailFragment;
import architect.jazzy.medicinereminder.Fragments.DoctorDetailFragments.DoctorAppointmentFragment;
import architect.jazzy.medicinereminder.Fragments.DoctorDetailFragments.DoctorMedicineListFragment;
import architect.jazzy.medicinereminder.Models.Doctor;

/**
 * Created by Jibin_ism on 02-Sep-15.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    Doctor doctor;
    public ViewPagerAdapter(FragmentManager fm, Doctor doctor) {
        super(fm);
        this.doctor=doctor;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment=null;
        switch (position){
            case 0:
                fragment= ContactDetailFragment.newInstance(doctor);
                ((ContactDetailFragment)fragment).setDoctorStateListener(new ContactDetailFragment.DoctorStateListener() {
                    @Override
                    public void onDoctorSaved(Doctor doctor) {
                        listener.onDoctorSaved(doctor);
                    }
                });
                break;
            case 1:
                fragment= DoctorMedicineListFragment.newInstance(doctor);
                break;
            case 2:
                fragment= new DoctorAppointmentFragment();
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Contact";
            case 1:
                return "Medicines";
            case 2:
                return "Appointments";
        }
        return super.getPageTitle(position);
    }

    ViewPagerFragmentInteractionListener listener;
    public interface ViewPagerFragmentInteractionListener{
        void onDoctorSaved(Doctor doctor);
    }
    public void setViewPagerFragmentListener(ViewPagerFragmentInteractionListener viewPagerFragmentListener){
        this.listener=viewPagerFragmentListener;
    }
}
