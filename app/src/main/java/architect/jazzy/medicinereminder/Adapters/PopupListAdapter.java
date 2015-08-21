package architect.jazzy.medicinereminder.Adapters;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import architect.jazzy.medicinereminder.R;

/**
 * Created by Jibin_ism on 07-Jun-15.
 */
public class PopupListAdapter extends BaseAdapter {

    ArrayList<Pair<Integer, String>> dataSet;
    Context mContext;
    LayoutInflater layoutInflater;

    public PopupListAdapter(Context context, ArrayList<Pair<Integer,String>> dataSet) {
        this.dataSet=dataSet;
        this.mContext=context;
        layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public Object getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v=convertView;
        if(convertView==null){
            v=layoutInflater.inflate(R.layout.popup_list_item,parent,false);
        }
        TextView textView=(TextView)v.findViewById(R.id.medName);
        ImageView imageView=(ImageView)v.findViewById(R.id.medIcon);

        Pair<Integer, String> data=dataSet.get(position);
        textView.setText(data.second);
        imageView.setImageResource(ImageAdapter.emojis[data.first]);

        return v;
    }
}
