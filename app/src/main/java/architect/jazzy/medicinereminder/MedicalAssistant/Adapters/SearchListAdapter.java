package architect.jazzy.medicinereminder.MedicalAssistant.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import architect.jazzy.medicinereminder.MedicalAssistant.Models.WebDocument;
import architect.jazzy.medicinereminder.R;

/**
 * Created by Jibin_ism on 20-Aug-15.
 */
public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder>{

    Context context;
    ArrayList<WebDocument> webDocuments;

    public SearchListAdapter(Context context, ArrayList<WebDocument> webDocuments) {
        super();
        this.context=context;
        this.webDocuments=webDocuments;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView=inflater.inflate(R.layout.recycler_view_search_result,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        if(position==webDocuments.size()-1){
            if(adapterPositionListener!=null)
                adapterPositionListener.onLastItemLoaded();
        }
        WebDocument document=webDocuments.get(position);
        holder.title.setText(document.getTitle());
        holder.summary.setText(document.getFullSummary());
        String s1="";
        ArrayList<String> alts=document.getAltTitle();
        for(int i=0;i<alts.size();i++){
            if(i==alts.size()-1){
                s1+=alts.get(i);
            }else{
                s1+=alts.get(i)+", ";
            }
        }
        if(s1.isEmpty()){
            holder.altTitle.setVisibility(View.GONE);
        }else {
            holder.altTitle.setVisibility(View.VISIBLE);
            holder.altTitle.setText(s1);
        }
        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.OnItemClick(webDocuments,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(webDocuments==null){
            return 0;
        }
        return webDocuments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView title,altTitle,summary;
        RelativeLayout ll;
        public ViewHolder(View itemView) {
            super(itemView);
            title=(TextView)itemView.findViewById(R.id.title);
            altTitle=(TextView)itemView.findViewById(R.id.altTitle);
            summary=(TextView)itemView.findViewById(R.id.fullSummary);
            ll=(RelativeLayout)itemView.findViewById(R.id.ll);
        }
    }

    ItemClickListener itemClickListener;
    public interface ItemClickListener{
        void OnItemClick(ArrayList<WebDocument> documents, int position);
    }
    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener=itemClickListener;
    }

    AdapterPositionListener adapterPositionListener;
    public interface AdapterPositionListener{
        void onLastItemLoaded();
    }
    public void setAdapterPositionListener(AdapterPositionListener adapterPositionListener){
        this.adapterPositionListener=adapterPositionListener;
    }

}
