package architect.jazzy.medicinereminder.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import architect.jazzy.medicinereminder.R;

/**
 * Created by Jibin_ism on 23-Dec-14.
 */
public class ImageAdapter extends BaseAdapter{
    private Context mcontext;
    /*public Integer[] emojis={R.drawable.plus,R.drawable.heart,
            R.drawable.pills,R.drawable.capsule,R.drawable.syringe,
            R.drawable.ic_action_new,R.drawable.ic_action_edit,R.drawable.ic_action_delete,
            R.drawable.ic_action_search,R.drawable.ic_action_new,R.drawable.ic_action_edit,
            R.drawable.ic_action_delete};*/

    public static Integer[] emojis = new Integer[]{ R.drawable.ic_action_star_10, R.drawable.ic_action_pill,R.drawable.ic_action_picker,
            R.drawable.ic_action_eye_open,R.drawable.ic_action_contrast,R.drawable.ic_action_pie_chart,
            R.drawable.ic_action_heart, R.drawable.ic_action_sun,R.drawable.ic_action_temperature,

            R.drawable.yic_action_star_10, R.drawable.yic_action_pill,R.drawable.yic_action_picker,
            R.drawable.yic_action_eye_open,R.drawable.yic_action_contrast,R.drawable.yic_action_pie_chart,
            R.drawable.yic_action_heart, R.drawable.yic_action_sun,R.drawable.yic_action_temperature,

            R.drawable.gic_action_star_10, R.drawable.gic_action_pill,R.drawable.gic_action_picker,
            R.drawable.gic_action_eye_open,R.drawable.gic_action_contrast,R.drawable.gic_action_pie_chart,
            R.drawable.gic_action_heart, R.drawable.gic_action_sun,R.drawable.gic_action_temperature,

            R.drawable.ric_action_star_10, R.drawable.ric_action_pill,R.drawable.ric_action_picker,
            R.drawable.ric_action_eye_open,R.drawable.ric_action_contrast,R.drawable.ric_action_pie_chart,
            R.drawable.ric_action_heart, R.drawable.ric_action_sun,R.drawable.ric_action_temperature,

            R.drawable.pic_action_star_10, R.drawable.pic_action_pill,R.drawable.pic_action_picker,
            R.drawable.pic_action_eye_open,R.drawable.pic_action_contrast,R.drawable.pic_action_pie_chart,
            R.drawable.pic_action_heart, R.drawable.pic_action_sun,R.drawable.pic_action_temperature,

            R.drawable.bic_action_star_10, R.drawable.bic_action_pill,R.drawable.bic_action_picker,
            R.drawable.bic_action_eye_open,R.drawable.bic_action_contrast,R.drawable.bic_action_pie_chart,
            R.drawable.bic_action_heart, R.drawable.bic_action_sun,R.drawable.bic_action_temperature,

            R.drawable.capsule_icon_emoji

    };

    public ImageAdapter(Context c) {
        mcontext=c;
    }

    @Override
    public int getCount() {
        return emojis.length;
    }

    @Override
    public Object getItem(int position) {
        return emojis[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView=new ImageView(mcontext);
            imageView.setImageResource(emojis[position]);
            imageView.setTag(emojis[position]);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setLayoutParams(new GridView.LayoutParams(52, 52));

        return imageView;
    }
}
