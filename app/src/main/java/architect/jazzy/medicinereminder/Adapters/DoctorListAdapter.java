package architect.jazzy.medicinereminder.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.Models.Doctor;
import architect.jazzy.medicinereminder.R;

/**
 * Created by Jibin_ism on 16-Aug-15.
 */
public class DoctorListAdapter extends RecyclerView.Adapter<DoctorListAdapter.ViewHolder> {

    private final String TAG = "DoctorListAdapter";
    Context context;
    ArrayList<Doctor> doctors;

    public DoctorListAdapter(Context context, ArrayList<Doctor> doctors) {
        super();
        this.context = context;
        this.doctors = doctors;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.recycler_view_doctor_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Doctor doctor = doctors.get(position);
//        Log.e(TAG, "Doctor Name: " + doctor.getName());
        holder.nameView.setText(doctor.getName());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.e(TAG,"Item clicked "+position+" "+doctor.getName());
                itemClickListener.onItemClick(position, doctor);
            }
        });

        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                itemClickListener.onItemLongClick(position,doctor);
                return true;
            }
        });

        holder.nameView.setBackgroundColor(Constants.getThemeColor(context));
        try {
            Bitmap bitmap=null;
//            try {
//                bitmap = Constants.getScaledBitmap(doctor.getPhotoUri().toString(),150,150);
//            } catch (Exception e) {
//                try {
//                    holder.imageView.setImageBitmap(Constants.getScaledBitmap(doctor.getPhotoPath(), holder.imageView.getMeasuredWidth(), holder.imageView.getMeasuredHeight()));
//                } catch (Exception e1) {
//                    e.printStackTrace();
//                }
//            }
            bitmap=Constants.getScaledBitmap(doctor.getPhoto(),150,150);
            if(bitmap!=null) {
                Palette.Builder builder = Palette.from(bitmap);
                Palette palette = builder.generate();
                try {
                    int color = Color.HSVToColor(palette.getSwatches().get(0).getHsl());
                    holder.nameView.setBackgroundColor(color);
                    holder.imageView.setImageBitmap(bitmap);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }else{
                Drawable drawable=context.getResources().getDrawable(R.drawable.userlogin);
                BitmapDrawable bitmapDrawable=(BitmapDrawable)drawable;
                try {
                    bitmap = Bitmap.createBitmap(bitmapDrawable.getBitmap());
                    holder.imageView.setImageBitmap(bitmap);
                }catch (NullPointerException e){
                    e.printStackTrace();
                    holder.imageView.setImageResource(R.drawable.userlogin);
                }
            }

//            try {
//                holder.imageView.setImageURI(doctor.getPhotoUri());
//            } catch (Exception e1) {
//                try {
//                    holder.imageView.setImageBitmap(Constants.getScaledBitmap(doctor.getPhotoPath(), holder.imageView.getMeasuredWidth(), holder.imageView.getMeasuredHeight()));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameView;
        LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            nameView = (TextView) itemView.findViewById(R.id.textView);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.ll);
        }
    }

    ItemClickListener itemClickListener;

    public interface ItemClickListener {
        void onItemClick(int position, Doctor doctor);
        void onItemLongClick(int position, Doctor doctor);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
