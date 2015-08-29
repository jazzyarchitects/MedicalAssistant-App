package architect.jazzy.medicinereminder.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import architect.jazzy.medicinereminder.Models.Doctor;
import architect.jazzy.medicinereminder.R;

/**
 * Created by Jibin_ism on 16-Aug-15.
 */
public class DoctorListAdapter extends RecyclerView.Adapter<DoctorListAdapter.ViewHolder>{

    private final String TAG="DoctorListAdapter";
    Context context;
    ArrayList<Doctor> doctors;
    public DoctorListAdapter(Context context, ArrayList<Doctor> doctors) {
        super();
        this.context=context;
        this.doctors=doctors;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView=inflater.inflate(R.layout.recycler_view_doctor_list,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Doctor doctor=doctors.get(position);
        Log.e(TAG,"Doctor Name: "+doctor.getName());
        holder.nameView.setText(doctor.getName());
        if(doctor.getPhotoUri()!=null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), doctor.getPhotoUri());
                Palette.Builder builder=Palette.from(bitmap);
                Palette palette=builder.generate();
                try {
                    int color=Color.HSVToColor(palette.getSwatches().get(0).getHsl());
                    holder.nameView.setBackgroundColor(color);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }

                holder.imageView.setImageURI(doctor.getPhotoUri());
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView nameView;
        LinearLayout linearLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView=(ImageView)itemView.findViewById(R.id.imageView);
            nameView=(TextView)itemView.findViewById(R.id.textView);
            linearLayout=(LinearLayout)itemView.findViewById(R.id.ll);
        }
    }
}
