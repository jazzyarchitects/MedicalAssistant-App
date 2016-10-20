package architect.jazzy.medicinereminder.MedicalAssistant.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import architect.jazzy.medicinereminder.MedicalAssistant.Models.Medicine;
import architect.jazzy.medicinereminder.R;

/**
 * Created by Jibin_ism on 02-Sep-15.
 */
public class HorizontalMedicineListAdapter extends RecyclerView.Adapter<HorizontalMedicineListAdapter.ViewHolder> {
  
  private static final String TAG = "DDListAdapter";
  ArrayList<Medicine> medicines;
  Context context;
  ArrayList<String> medicineNames;
  OnItemClickListener itemClickListener;
  
  public HorizontalMedicineListAdapter(Context context, ArrayList<Medicine> medicines) {
    this.context = context;
    this.medicines = medicines;
  }
  
  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View v = inflater.inflate(R.layout.horizontal_medicine_list, parent, false);
    return new ViewHolder(v);
  }
  
  @Override
  public void onBindViewHolder(final ViewHolder holder, int position) {
    Medicine medicine = medicines.get(position);
    
    holder.holder.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (itemClickListener == null) {
          throw new UnknownError("Need to implement itemClickListener in the activity");
        }
        itemClickListener.onItemClick(holder.getAdapterPosition(), medicines);
      }
    });
    holder.medName.setText(medicine.getMedName());
    holder.medIcon.setImageResource(ImageAdapter.emojis[getImageIndex(medicine.getIcon())]);
  }
  
  @Override
  public int getItemCount() {
    return medicines == null ? 0 : medicines.size();
  }
  
  private int getImageIndex(Integer i) {
    if (i < 0 || i > ImageAdapter.emojis.length) {
      return 0;
    }
    return i;
  }
  
  public void setItemClickListener(OnItemClickListener onItemClickListener) {
    this.itemClickListener = onItemClickListener;
  }
  
  public interface OnItemClickListener {
    void onItemClick(int position, ArrayList<Medicine> medicines);
  }
  
  public class ViewHolder extends RecyclerView.ViewHolder {
    
    TextView medName;
    ImageView medIcon;
    CardView holder;
    
    
    public ViewHolder(View itemView) {
      super(itemView);
      medName = (TextView) itemView.findViewById(R.id.medicineName);
      medIcon = (ImageView) itemView.findViewById(R.id.medicineIcon);
      holder = (CardView) itemView.findViewById(R.id.holder);
    }
  }
  
  
}
