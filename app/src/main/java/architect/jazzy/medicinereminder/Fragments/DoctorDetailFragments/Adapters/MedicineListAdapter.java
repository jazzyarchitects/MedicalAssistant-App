package architect.jazzy.medicinereminder.Fragments.DoctorDetailFragments.Adapters;

import android.content.Context;
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

import java.util.ArrayList;

import architect.jazzy.medicinereminder.Adapters.ImageAdapter;
import architect.jazzy.medicinereminder.Models.Medicine;
import architect.jazzy.medicinereminder.R;

/**
 * Created by Jibin_ism on 02-Sep-15.
 */
public class MedicineListAdapter extends RecyclerView.Adapter<MedicineListAdapter.ViewHolder> {

    private static final String TAG="DDListAdapter";
    ArrayList<Medicine> medicines;
    Context context;
    ArrayList<String> medicineNames;
    public MedicineListAdapter(Context context,ArrayList<Medicine> medicines) {
        this.context=context;
        this.medicines=medicines;

//        Log.e(TAG,"Constructor");
        medicineNames=new ArrayList<>();
        for(Medicine medincine: medicines){
            Log.e(TAG,"Adding medicine Name: "+medincine.getMedName());
//            this.medicineNames.add(medincine.getMedName());
        }


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.recycler_view_item_medicine_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        Medicine medicine=medicines.get(position);

        Log.e(TAG,"OnBindViewHolder");
        holder.medicineHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener==null){
                    throw new UnknownError("Need to implement itemClickListener in the activity");
                }
                itemClickListener.onItemClick(position,medicineNames);
            }
        });
        holder.medName.setText(medicine.getMedName());
        holder.medIcon.setImageResource(ImageAdapter.emojis[getImageIndex(medicine.getIcon())]);
    }

    @Override
    public int getItemCount() {
        return medicines==null?0:medicines.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView medName;
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
    }

    private int getImageIndex(Integer i) {
        if (i < 0 || i > ImageAdapter.emojis.length) {
            return 0;
        }
        return i;
    }

    OnItemClickListener itemClickListener;
    public interface OnItemClickListener{
        void onItemClick(int position, ArrayList<String> medicines);
    }
    public void setItemClickListener(OnItemClickListener onItemClickListener){
        this.itemClickListener=onItemClickListener;
    }



}
