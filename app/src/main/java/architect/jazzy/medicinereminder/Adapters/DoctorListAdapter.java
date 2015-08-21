package architect.jazzy.medicinereminder.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView nameView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView=(ImageView)itemView.findViewById(R.id.imageView);
            nameView=(TextView)itemView.findViewById(R.id.textView);

        }
    }
}
