package architect.jazzy.medicinereminder.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;

import java.util.ArrayList;

import architect.jazzy.medicinereminder.Models.Medicine;
import architect.jazzy.medicinereminder.R;

/**
 * Created by Jibin_ism on 18-Jun-15.
 */
public class MedicineListAdapter extends RecyclerView.Adapter<MedicineListAdapter.ViewHolder>
        implements SwipeableItemAdapter<MedicineListAdapter.ViewHolder> {

    private String TAG = "MedicineListAdapter";

    ArrayList<String> deleted = new ArrayList<String>();
    public static final String MEDICINE_NAME = "medname";
    public static final String IMAGE_ID = "imageID";
    ImageAdapter imageAdapter;
    View v;
    View deletedView;
    String name;
    int layout_id=0;


    private View.OnClickListener mItemViewOnClickListener;
    private View.OnClickListener mSwipeableViewContainerOnClickListener;

    private Context context;
    LayoutInflater inflater;
    ArrayList<Medicine> medicines;
    ArrayList<String> medicineNames;
    Activity parent = null;

    private EventListener mEventListener;

    public interface EventListener {
        void onItemRemoved(int position, Medicine medicine);

        void onItemViewClicked(int position, ArrayList<String> medicineList);
    }


    public MedicineListAdapter() {
        this(null, null, null);
    }

    public MedicineListAdapter(Context context, ArrayList<Medicine> medicines, Activity parent) {
        Log.e("List", "Adaptor Called");
        this.context = context;
        this.medicines=medicines;
        this.parent = parent;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageAdapter = new ImageAdapter(context);
        medicineNames = new ArrayList<String>();
        for (int i = 0; i < medicines.size(); i++) {
            medicineNames.add(medicines.get(i).getMedName());
        }
        init();
    }

    public void setLayout(@LayoutRes int layout_id){
        this.layout_id=layout_id;
    }

    public MedicineListAdapter(Context context, ArrayList<Medicine> medicines) {
        this(context, medicines, null);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(this.layout_id==0)
            v = inflater.inflate(R.layout.listitem_custom, parent, false);
        else
            v=inflater.inflate(layout_id, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Medicine data=medicines.get(position);


        mItemViewOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemViewClick(position, medicineNames);
            }
        };
        mSwipeableViewContainerOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwipeableViewContainerClick(position, medicineNames);
            }
        };

        // set listeners
        // (if the item is *not pinned*, click event comes to the itemView)
        holder.itemView.setOnClickListener(mItemViewOnClickListener);
        // (if the item is *pinned*, click event comes to the mContainer)
//        holder.cardView.setOnClickListener(mSwipeableViewContainerOnClickListener);


        name = "";;
        holder.medName.setText(data.getMedName());

        holder.medIcon.setImageResource(ImageAdapter.emojis[getImageIndex(data.getIcon())]);


        try {
            if (parent == null) {
                holder.goIcon.setVisibility(View.GONE);
            }
        }catch (NullPointerException e){
            Log.e("MedicineListAdapter","Lock Screen popup");
        }

        holder.setSwipeItemSlideAmount(0);

        final int swipeState = holder.getSwipeStateFlags();
        Log.e(TAG, "Swipe State= " + swipeState);
    }

    void init() {

        // SwipeableItemAdapter requires stable ID, and also
        // have to implement the getItemId() method appropriately.
        setHasStableIds(true);
    }


    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return medicines.size();
    }

    @Override
    public int onGetSwipeReactionType(ViewHolder viewHolder, int position, int x, int y) {
        //swipes to left
//        return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_MOVE_TO_SWIPED_DIRECTION;
        //swipes right only
        return RecyclerViewSwipeManager.REACTION_CAN_SWIPE_RIGHT;
//        return RecyclerViewSwipeManager.REACTION_CAN_SWIPE_BOTH;
    }

    @Override
    public void onSetSwipeBackground(ViewHolder viewHolder, int position, int type) {
    }

    @Override
    public int onSwipeItem(ViewHolder viewHolder, int position, int result) {
        Log.e(TAG, "Item swiped " + position + " result=" + result);
        switch (result) {
            // swipe right
            case RecyclerViewSwipeManager.RESULT_SWIPED_RIGHT:
                return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_REMOVE_ITEM;

            // other --- do nothing
            case RecyclerViewSwipeManager.RESULT_CANCELED:
            default:
                return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_DEFAULT;
        }
    }

    @Override
    public void onPerformAfterSwipeReaction(ViewHolder viewHolder,final int position, int result, int reaction) {
        Log.d(TAG, "onPerformAfterSwipeReaction(position = " + position + ", result = " + result + ", reaction = " + reaction + ")");
        final Medicine item = medicines.get(position);

        if (reaction == RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_REMOVE_ITEM) {

            if(parent!=null){
                AlertDialog.Builder builder=new AlertDialog.Builder(parent);
                builder.setTitle("Confirm Delete");
                builder.setMessage(item.getMedName()+" will be removed from the list. This cannot be undone.")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                medicineNames.remove(item.getMedName());
                                medicines.remove(position);
                                notifyItemRemoved(position);
                                if (mEventListener != null) {
                                    mEventListener.onItemRemoved(position,item);
                                }
                            }
                        })
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }

        }
    }

    public EventListener getEventListener() {
        return mEventListener;
    }

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }

    private void onItemViewClick(int position, ArrayList<String> mednames) {
        if (mEventListener != null) {
            mEventListener.onItemViewClicked(position, mednames); // true --- pinned
        }
    }

    private void onSwipeableViewContainerClick(int position, ArrayList<String> medicineNames) {
        if (mEventListener != null) {
            mEventListener.onItemViewClicked(position, medicineNames);  // false --- not pinned
        }
    }

    private int getImageIndex(Integer i) {
        if (i < 0 || i > ImageAdapter.emojis.length) {
            return 0;
        }
        return i;
    }

    public static class ViewHolder extends AbstractSwipeableItemViewHolder {

        public TextView medName;
        //        medDays;
        public ImageView medIcon, goIcon;
        public LinearLayout medicineHolder;
        public FrameLayout relativeLayout;
        public CardView cardView;


        public ViewHolder(View itemView) {
            super(itemView);
            goIcon = (ImageView) itemView.findViewById(R.id.goIcon);
            medName = (TextView) itemView.findViewById(R.id.medName);
            medIcon = (ImageView) itemView.findViewById(R.id.icon);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            medicineHolder = (LinearLayout) itemView.findViewById(R.id.medicineHolder);
            relativeLayout = (FrameLayout) itemView.findViewById(R.id.backParent);
        }

        @Override
        public View getSwipeableContainerView() {
            return cardView;
        }
    }

}
