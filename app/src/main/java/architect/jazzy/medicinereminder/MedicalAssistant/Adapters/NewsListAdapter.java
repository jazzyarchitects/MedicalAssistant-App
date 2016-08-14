package architect.jazzy.medicinereminder.MedicalAssistant.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;

import architect.jazzy.medicinereminder.MedicalAssistant.Models.FeedItem;
import architect.jazzy.medicinereminder.R;

/**
 * Created by Jibin_ism on 14-Aug-15.
 */
public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.ViewHolder> {

    ArrayList<FeedItem> news;
    Context context;

    public NewsListAdapter(Context context, ArrayList<FeedItem> news, Activity activity) {
        this.news=news;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView=inflater.inflate(R.layout.recycler_item_news_feed,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final FeedItem item=news.get(position);
        holder.textView.setText(item.getTitle());
        holder.holderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedClickListener.onFeedClick(item);
            }
        });
        holder.colorIndicator.setBackgroundColor(getBackgroundColor(item));
        holder.dateView.setText(DateFormat.getDateInstance().format(item.getModified()));
    }

    @Override
    public int getItemCount() {
        return news==null?0:news.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView, dateView;
        LinearLayout holderLayout;
        View colorIndicator;
        public ViewHolder(View itemView) {
            super(itemView);
            textView=(TextView)itemView.findViewById(R.id.textView);
            holderLayout=(LinearLayout)itemView.findViewById(R.id.ll);
            colorIndicator=itemView.findViewById(R.id.colorIndicator);
            dateView=(TextView)itemView.findViewById(R.id.dateView);
        }
    }

    int getBackgroundColor(FeedItem item){
        String title=item.getTitle().toLowerCase();
        if(title.contains("tip")){
            return context.getResources().getColor(R.color.color_healthTip);
        }
        if(title.contains("vaccine") || title.contains("medicine") || title.contains("injection")){
            return context.getResources().getColor(R.color.color_vaccine);
        }
        if(title.contains("death") || title.contains("cancer") || title.contains("blood") || title.contains("aids")){
            return context.getResources().getColor(R.color.color_death);
        }
        return context.getResources().getColor(R.color.color_default);

    }

    FeedClickListener feedClickListener;
    public interface FeedClickListener{
        void onFeedClick(FeedItem item);
    }

    public void setFeedClickListener(FeedClickListener listener){
        this.feedClickListener=listener;
    }


}
