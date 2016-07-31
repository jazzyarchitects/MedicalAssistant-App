package architect.jazzy.medicinereminder.Fragments.OfflineActivity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import architect.jazzy.medicinereminder.Activities.MedicineDetails;
import architect.jazzy.medicinereminder.CustomComponents.CyclicTransitionDrawable;
import architect.jazzy.medicinereminder.CustomViews.CircleView;
import architect.jazzy.medicinereminder.Handlers.DataHandler;
import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.Models.MedTime;
import architect.jazzy.medicinereminder.Models.Medicine;
import architect.jazzy.medicinereminder.R;

public class DashboardFragment extends Fragment {

    private static final String TAG = "Dashboard";
    View v;
    TextView medicineCountView;
    ImageView backParent;
    FloatingActionButton floatingActionButton;
    RelativeLayout relativeLayout;
    boolean isMenuOpen = false;
    boolean is24hr;

    Drawable[] drawables;


    CircleView circleSearch, circleNews, circleAddMedicine, circleMedicineList, circleDoctorList;

    int[] medicineViewIds = {R.id.medicine1, R.id.medicine2, R.id.medicine3, R.id.medicine4, R.id.medicine5, R.id.medicine6};

    String[] medicineViewTimes = {"Before Breakfast", "After Breakfast", "Before Lunch", "After Lunch", "Before Dinner", "After Dinner"};

    public static final int ADD_DOCTOR_FAB_ID = 305;
    public static final int ADD_MEDICINE_FAB_ID = 218;
    MedTime[][] medTimes = new MedTime[3][2];

    public static final int ADD_DOCTOR_ID = 789655;
    public static final int SEARCH_ID = 968974;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_dashboard, container, false);
        medTimes = MedTime.getDefaultTimes(getActivity());
        isMenuOpen = false;
        ImageView appIcon = (ImageView) v.findViewById(R.id.appIcon);
        appIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DrawerLayout) getActivity().findViewById(R.id.drawerLayout)).openDrawer(GravityCompat.START);
            }
        });

        return v;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int color2 = Constants.getThemeColor(getActivity());
        int color = Color.argb(0xCC, Color.red(color2), Color.green(color2), Color.blue(color2));
        ((LinearLayout) view.findViewById(R.id.countBack)).setBackgroundColor(color);

        floatingActionButton = (FloatingActionButton) v.findViewById(R.id.floatingActionButton);
        relativeLayout = (RelativeLayout) v.findViewById(R.id.r1);
        medicineCountView = (TextView) v.findViewById(R.id.medicineCount);
        backParent = (ImageView) v.findViewById(R.id.backParent);

        circleNews = (CircleView) v.findViewById(R.id.circleNews);
        circleAddMedicine = (CircleView) v.findViewById(R.id.circleAddMedicine);
        circleDoctorList = (CircleView) v.findViewById(R.id.circleDoctorList);
        circleMedicineList = (CircleView) v.findViewById(R.id.circleMedicineList);
        circleSearch = (CircleView) v.findViewById(R.id.circleSearch);

        circleSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFragmentInteractionListener.performNavAction(DashboardFragment.SEARCH_ID);
            }
        });

        circleAddMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFragmentInteractionListener.performNavAction(DashboardFragment.ADD_DOCTOR_ID);
            }
        });

        circleMedicineList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFragmentInteractionListener.performNavAction(R.id.showMedicineList);
            }
        });

        circleDoctorList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFragmentInteractionListener.performNavAction(R.id.addDoctor);
            }
        });

        circleNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFragmentInteractionListener.performNavAction(R.id.news);
            }
        });


        drawables = new Drawable[]{getResources().getDrawable(R.drawable.back_car),
                getResources().getDrawable(R.drawable.back_night),
                getResources().getDrawable(R.drawable.back_street)};
        int ra = getRandomDrawableIndex();
        int ra2 = getNextIndex(ra);
        int ra3 = getNextIndex(ra2);
        Drawable[] toDisplayDrawables = new Drawable[]{
                drawables[ra],
                drawables[ra2],
                drawables[ra3]
        };

        CyclicTransitionDrawable transitionDrawable = new CyclicTransitionDrawable(toDisplayDrawables);
        transitionDrawable.startTransition(3000, 10000);
        backParent.setImageDrawable(transitionDrawable);


        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        DataHandler handler = new DataHandler(getActivity());
        final ArrayList<Medicine> medicines = handler.getTodaysMedicine();
        medicineCountView.setText(String.valueOf(medicines.size()));
        int i = 0;
        while (i < 6) {
            if (i >= medicines.size()) {
                break;
            }
            TextView view1 = (TextView) v.findViewById(medicineViewIds[i]);
            final Pair<Medicine, String> pair = getMedicineDetailDisplayString(medicines.get(i), i);
            view1.setText(Html.fromHtml(pair.second));
            view1.setTag(pair.first);
            view1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), MedicineDetails.class);
                    ArrayList<Medicine> medicines1=new ArrayList<Medicine>();
                    medicines1.add(pair.first);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(Constants.MEDICINE_NAME_LIST, medicines1);
                    i.putExtra(Constants.MEDICINE_POSITION, 0);
                    i.putExtras(bundle);
                    startActivity(i);
                }
            });
            i++;
        }

//        initialize();
    }

//    void initialize() {
//        floatingActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e(TAG, "fab add onclick");
//                addNewItems();
//            }
//        });
//    }

    int getRandomDrawableIndex() {
        Random random = new Random();
        return random.nextInt(3);
    }

    int getNextIndex(int i) {
        return i >= 2 ? 0 : i + 1;
    }



    private Pair<Medicine, String> getMedicineDetailDisplayString(Medicine medicine, int viewIndex) {
        MedTime breakfast = getBreakfastTime(medicine.getBreakfast());
        MedTime lunch = getLunchTime(medicine.getLunch());
        MedTime dinner = getDinnerTime(medicine.getDinner());
        MedTime customTime = medicine.getCustomTime();

        String s = "<b>" + medicine.getMedName() + "</b>" + "<small>";
        if (breakfast != null && !MedTime.hasPassed(breakfast)) {
            s +=  "<br />"+breakfast.toString(is24hr);
        }
        if (lunch != null && !MedTime.hasPassed(lunch)) {
            s += "<br />"+lunch.toString(is24hr);
        }
        if (dinner != null && !MedTime.hasPassed(dinner)) {
            s += "<br />"+dinner.toString(is24hr);
        }
        if (customTime != null && !MedTime.hasPassed(customTime)) {
            s += "<br />"+customTime.toString(is24hr);
        }
        s += "</small>";
        return Pair.create(medicine, s);
    }

//    void addNewItems() {
//        if (isMenuOpen) {
//            return;
//        }
//        isMenuOpen = true;
//        Log.e(TAG, "fab doc creating");
//        FloatingActionButton addDocFab = new FloatingActionButton(getActivity());
//        addDocFab.setImageResource(R.drawable.ic_action_user);
//        addDocFab.setClickable(true);
//        addDocFab.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{getResources().getColor(R.color.actionBackground)}));
//        addDocFab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e(TAG, "fab add doctor onclick");
//                onFragmentInteractionListener.addDoctor();
//            }
//        });
//        RelativeLayout.LayoutParams docFabLayoutParams = new RelativeLayout.LayoutParams((int) getResources().getDimension(R.dimen.floatingActionButtonSize), (int) getResources().getDimension(R.dimen.floatingActionButtonSize));
//
//        int bottom = 10 + (int) getResources().getDimension(R.dimen.floatingActionButtonSize) + 10 + 10;
//        docFabLayoutParams.setMargins(10, 10, 10, bottom);
//        docFabLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        docFabLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        addDocFab.setLayoutParams(docFabLayoutParams);
//
//
//        FloatingActionButton addMedFab = new FloatingActionButton(getActivity());
//        try {
//            Drawable drawable = getResources().getDrawable(R.drawable.ic_action_pill).mutate();
//            drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
//            addMedFab.setImageDrawable(drawable);
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        }
//        relativeLayout.addView(addDocFab);
//        addMedFab.setClickable(true);
//        addMedFab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onFragmentInteractionListener.addMedicine();
//            }
//        });
//        addMedFab.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{getResources().getColor(R.color.color_death)}));
//        RelativeLayout.LayoutParams addMedLayoutParams = new RelativeLayout.LayoutParams((int) getResources().getDimension(R.dimen.floatingActionButtonSize), (int) getResources().getDimension(R.dimen.floatingActionButtonSize));
//        addMedLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        addMedLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        bottom *= 2;
//        bottom -= 10;
//        addMedLayoutParams.setMargins(10, 10, 10, bottom);
//        addMedFab.setLayoutParams(addMedLayoutParams);
//
//
//        relativeLayout.addView(addMedFab);
//
//        addDocFab.startAnimation(getShowAnimation(true));
//        addMedFab.startAnimation(getShowAnimation(false));
//
//    }

    private Animation getShowAnimation(boolean isFirst) {
        Log.e(TAG, "fab getting animation");
        int fromX = 20 + (int) getResources().getDimension(R.dimen.floatingActionButtonSize);
        TranslateAnimation animation = new TranslateAnimation(fromX, 0, 0, 0);
        animation.setFillEnabled(true);
        animation.setFillAfter(true);
        animation.setDuration(getResources().getInteger(R.integer.fabAnimDuration));
        if (!isFirst) {
            animation.setStartOffset(getResources().getInteger(R.integer.fabAnimDuration) / 2);
        }
        return animation;
    }

    OnFragmentInteractionListener onFragmentInteractionListener;

    public interface OnFragmentInteractionListener {
        void performNavAction(int menuId);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onFragmentInteractionListener = (OnFragmentInteractionListener) context;
    }

    private MedTime getBreakfastTime(String beforeOrAfter) {
        if (beforeOrAfter.equalsIgnoreCase("before")) {
            return medTimes[0][0];
        } else if (beforeOrAfter.equalsIgnoreCase("after")) {
            return medTimes[0][1];
        }
        return null;
    }

    private MedTime getLunchTime(String beforeOrAfter) {
        if (beforeOrAfter.equalsIgnoreCase("before")) {
            return medTimes[1][0];
        } else if (beforeOrAfter.equalsIgnoreCase("after")) {
            return medTimes[1][1];
        }
        return null;
    }

    private MedTime getDinnerTime(String beforeOrAfter) {
        if (beforeOrAfter.equalsIgnoreCase("before")) {
            return medTimes[2][0];
        } else if (beforeOrAfter.equalsIgnoreCase("after")) {
            return medTimes[2][1];
        }
        return null;
    }

    private final int BREAKFAST = 0;
    private final int LUNCH = 1;
    private final int DINNER = 2;

}
