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
        switch (position){
            case 0:
                return new ContactDetailFragment();
            case 1:
                return DoctorMedicineListFragment.newInstance(doctor);
            case 2:
                return new DoctorAppointmentFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
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
}
