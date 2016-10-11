package architect.jazzy.medicinereminder.MedicalAssistant.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
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

import architect.jazzy.medicinereminder.MedicalAssistant.Models.Medicine;
import architect.jazzy.medicinereminder.R;

/**
 * Created by Jibin_ism on 18-Jun-15.
 */
public class MedicineListAdapter extends RecyclerView.Adapter<MedicineListAdapter.ViewHolder> {

  public static final String MEDICINE_NAME = "medname";
  public static final String IMAGE_ID = "imageID";
  ArrayList<String> deleted = new ArrayList<String>();
  ImageAdapter imageAdapter;
  View v;
  View deletedView;
  String name;
  int layout_id = 0;
  LayoutInflater inflater;
  ArrayList<Medicine> medicines;
  ArrayList<String> medicineNames;
  Activity parent = null;
  EventListener eventListener;
  private String TAG = "MedicineListAdapter";
  private Context context;

  public MedicineListAdapter() {
    this(null, null, null);
  }

  public MedicineListAdapter(Context context, ArrayList<Medicine> medicines, Activity parent) {
    Log.e("List", "Adaptor Called");
    this.context = context;
    this.medicines = medicines;
    this.parent = parent;
    inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    imageAdapter = new ImageAdapter(context);
    medicineNames = new ArrayList<String>();
    for (int i = 0; i < medicines.size(); i++) {
      medicineNames.add(medicines.get(i).getMedName());
    }
  }

  public MedicineListAdapter(Context context, ArrayList<Medicine> medicines) {
    this(context, medicines, null);
  }

  public void setLayout(@LayoutRes int layout_id) {
    this.layout_id = layout_id;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (this.layout_id == 0)
      v = inflater.inflate(R.layout.recycler_view_item_medicine_list, parent, false);
    else
      v = inflater.inflate(layout_id, parent, false);
    return new ViewHolder(v);
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, int position) {
    assert eventListener != null;
    final Medicine data = medicines.get(position);

    name = "";
    ;
    holder.medName.setText(data.getMedName());

    holder.medIcon.setImageResource(ImageAdapter.emojis[getImageIndex(data.getIcon())]);


    try {
      if (parent == null) {
        holder.goIcon.setVisibility(View.GONE);
      }
    } catch (NullPointerException e) {
      Log.e("MedicineListAdapter", "Lock Screen popup " + e.getMessage());
    }

    if (holder.cardView != null) {
      holder.cardView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          eventListener.onMedClick(holder.getAdapterPosition(), medicines);
        }
      });
    }
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public int getItemCount() {
    return medicines.size();
  }

  private int getImageIndex(Integer i) {
    if (i < 0 || i > ImageAdapter.emojis.length) {
      return 0;
    }
    return i;
  }

  public void setEventListener(EventListener eventListener) {
    this.eventListener = eventListener;
  }

  public interface EventListener {
    void onMedClick(int position, ArrayList<Medicine> medicines);
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

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
  }
}
