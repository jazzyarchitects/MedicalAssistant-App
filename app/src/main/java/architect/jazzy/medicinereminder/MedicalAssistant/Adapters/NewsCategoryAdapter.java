package architect.jazzy.medicinereminder.MedicalAssistant.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.R;

/**
 * Created by Jibin_ism on 15-Aug-15.
 */
public class NewsCategoryAdapter extends RecyclerView.Adapter<NewsCategoryAdapter.ViewHolder>{

    Context context;
    ArrayList<Pair<String, String>> medTopics;

    public NewsCategoryAdapter(Context context) {
        super();
        this.context=context;
        this.medTopics= Constants.MedicineNetUrls.getUrls();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView=inflater.inflate(R.layout.recycler_view_news_topics,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Pair<String, String> topic=medTopics.get(position);
        holder.textView.setText(topic.first);
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryClickListener.onCategoryClick(topic);
            }
        });
    }

    @Override
    public int getItemCount() {
        return medTopics.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout relativeLayout;
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView=(TextView)itemView.findViewById(R.id.textView);
            relativeLayout=(RelativeLayout)itemView.findViewById(R.id.ll);
        }
    }

    CategoryClickListener categoryClickListener;
    public interface CategoryClickListener{
        void onCategoryClick(Pair<String, String> category);
    }

    public void setCategoryClickListener(CategoryClickListener categoryClickListener){
        this.categoryClickListener=categoryClickListener;
    }

}
