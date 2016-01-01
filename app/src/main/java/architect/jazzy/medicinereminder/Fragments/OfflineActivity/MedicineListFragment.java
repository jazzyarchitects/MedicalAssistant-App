package architect.jazzy.medicinereminder.Fragments.OfflineActivity;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import java.util.ArrayList;

import architect.jazzy.medicinereminder.Activities.MainActivity;
import architect.jazzy.medicinereminder.Adapters.MedicineListAdapter;
import architect.jazzy.medicinereminder.Handlers.DataHandler;
import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.Models.Medicine;
import architect.jazzy.medicinereminder.R;
import architect.jazzy.medicinereminder.ThisApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class MedicineListFragment extends Fragment {

    final int SHOW_LIST_REQUEST_CODE = 123;
    DataHandler dataHandler;
//    ArrayList<HashMap<String, ArrayList<String>>> dataSet;
    ArrayList<Medicine> medicines;
    RecyclerView medicineList;
    MedicineListAdapter listAdaptor;
    RelativeLayout root;
    FloatingActionButton floatingActionButton;
    ArrayList<String> removedItems = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewSwipeManager mRecyclerViewSwipeManager;
    private RecyclerViewTouchActionGuardManager mRecyclerViewTouchActionGuardManager;
    FragmentInteractionListener fragmentInteractionListener;
    View v;
    Context context;

    public MedicineListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.fragment_list, container, false);
        context=getActivity();

        LinearLayout emptyList = (LinearLayout)v.findViewById(R.id.emptyList);

        emptyList.setVisibility(View.GONE);
        /**Analytics Code*/
        Tracker t = ((ThisApplication) getActivity().getApplication()).getTracker(
                ThisApplication.TrackerName.APP_TRACKER);
        t.setScreenName("Medicine List");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.AppViewBuilder().build());

        medicineList = (RecyclerView) v.findViewById(R.id.recyclerView);
        root = (RelativeLayout) v.findViewById(R.id.rl);
        floatingActionButton=(FloatingActionButton)v.findViewById(R.id.floatingActionButton);
        floatingActionButton.setBackgroundTintList(Constants.getFabBackground(getActivity()));
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentInteractionListener.addMedicine();
            }
        });
        getMedicineData();

        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Medicine List");
        }catch (Exception e){
            e.printStackTrace();
        }

        createOptionalView();
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try{
            ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        fragmentInteractionListener=(FragmentInteractionListener)activity;

        ((MainActivity)activity).setActivityResultListener(new MainActivity.ActivityResultListener() {
            @Override
            public void medicineListActivityResult(int requestCode, int resultCode, Intent data) {
                getMedicineData();
                createOptionalView();
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (mRecyclerViewSwipeManager != null) {
            mRecyclerViewSwipeManager.release();
            mRecyclerViewSwipeManager = null;
        }

        if (mRecyclerViewTouchActionGuardManager != null) {
            mRecyclerViewTouchActionGuardManager.release();
            mRecyclerViewTouchActionGuardManager = null;
        }

        if (medicineList != null) {
            medicineList.setItemAnimator(null);
            medicineList.setAdapter(null);
            medicineList = null;
        }

        if (mWrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(mWrappedAdapter);
            mWrappedAdapter = null;
        }
        mAdapter = null;
    }



    void createOptionalView() {
        if (medicines==null || medicines.isEmpty()) {
            root.removeAllViews();
            root.setBackgroundColor(getResources().getColor(R.color.actionBackground));
            TextView textView = new TextView(context);
            textView.setTextSize(20);
            textView.setTextColor(Color.WHITE);
            textView.setText(Html.fromHtml("<big><b>...Congratulation...</b></big><br /><br />You are not taking any medicines"));
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            textView.setPadding(10, 10, 10, 10);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(layoutParams);
            textView.setId(0);

            Button addNew = new Button(context);
            addNew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragmentInteractionListener.addMedicine();
                }
            });
            RelativeLayout.LayoutParams butLP = new RelativeLayout.LayoutParams(300, ViewGroup.LayoutParams.WRAP_CONTENT);
//            butLP.addRule(RelativeLayout.BELOW,textView.getId());
            butLP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            butLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
            butLP.setMargins(10, 10, 10, 50);
            addNew.setLayoutParams(butLP);
            addNew.setText("Add Medicines");
            addNew.setCompoundDrawables(getResources().getDrawable(R.drawable.ic_action_new), null, null, null);
            addNew.setTextColor(Color.WHITE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(getResources().getColor(R.color.path_orange));
            drawable.setCornerRadius(20);
            addNew.setBackgroundDrawable(drawable);
            addNew.setGravity(Gravity.CENTER);

            root.addView(textView);
            root.addView(addNew);
        } else {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
            medicineList.setHasFixedSize(true);
            medicineList.setLayoutManager(layoutManager);


            // touch guard manager  (this class is required to suppress scrolling while swipe-dismiss animation is running)
            mRecyclerViewTouchActionGuardManager = new RecyclerViewTouchActionGuardManager();
            mRecyclerViewTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
            mRecyclerViewTouchActionGuardManager.setEnabled(true);


            // swipe manager
            mRecyclerViewSwipeManager = new RecyclerViewSwipeManager();

            medicineList.setItemAnimator(new DefaultItemAnimator());
            listAdaptor = new MedicineListAdapter(context, medicines, getActivity());
            listAdaptor.setEventListener(new MedicineListAdapter.EventListener() {
                @Override
                public void onItemViewClicked(int position, ArrayList<Medicine> medicines) {
                    showDetails(position, medicines);
                }

                @Override
                public void onItemRemoved(int position, Medicine medicine) {
                    itemRemoved(position, medicine);
                }
            });

            mAdapter = listAdaptor;
            mWrappedAdapter = mRecyclerViewSwipeManager.createWrappedAdapter(listAdaptor);      // wrap for swiping

            final GeneralItemAnimator animator = new SwipeDismissItemAnimator();


            medicineList.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
            medicineList.setItemAnimator(animator);

            // additional decorations
            //noinspection StatementWithEmptyBody
            if (supportsViewElevation()) {
                // Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
            } else {
                medicineList.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) getResources().getDrawable(R.drawable.material_shadow_z1)));
            }
            medicineList.addItemDecoration(new SimpleListDividerDecorator(getResources().getDrawable(R.drawable.list_divider), true));
            medicineList.getAdapter().notifyDataSetChanged();

            // NOTE:
            // The initialization order is very important! This order determines the priority of touch event handling.
            //
            // priority: TouchActionGuard > Swipe > DragAndDrop
            mRecyclerViewSwipeManager.attachRecyclerView(medicineList);
            mRecyclerViewTouchActionGuardManager.attachRecyclerView(medicineList);

//            medicineList.setAdapter(listAdaptor);
        }
    }


    private boolean supportsViewElevation() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.menu_common,menu);
//    }

    public void itemRemoved(final int position, final Medicine item) {
        final String name = item.getMedName();
        removedItems.add(name);
        medicines.remove(item);
        Log.e("MedicineList", "To be deleted: " + name);
        DataHandler handler=new DataHandler(context);
        handler.deleteRow(name);
        handler.close();
//        createNoDataView();
        createOptionalView();

        try {
            final CoordinatorLayout coordinatorView = (CoordinatorLayout) v.findViewById(R.id.snackbarPosition);
            Snackbar snackbar = Snackbar.make(coordinatorView, name + " has been removed from your list", Snackbar.LENGTH_LONG);
            snackbar.show();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }



    private void getMedicineData() {
        dataHandler = new DataHandler(context);
        medicines = dataHandler.getMedicineList();
        dataHandler.close();

    }


    public void showDetails(int position, ArrayList<Medicine> medicines) {
        fragmentInteractionListener.showDetails(position,medicines);
    }

    public interface FragmentInteractionListener{
        void addMedicine();
        void showDetails(int position, ArrayList<Medicine> medicines);
    }



}
