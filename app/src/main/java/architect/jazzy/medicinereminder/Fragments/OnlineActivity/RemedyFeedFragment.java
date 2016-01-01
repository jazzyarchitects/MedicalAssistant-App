package architect.jazzy.medicinereminder.Fragments.OnlineActivity;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import architect.jazzy.medicinereminder.Models.Remedy;
import architect.jazzy.medicinereminder.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RemedyFeedFragment extends Fragment {

    RecyclerView recyclerView;
    Context mContext;

    public RemedyFeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext = getActivity();
        return inflater.inflate(R.layout.fragment_remedy_feed, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setHasFixedSize(false);

        ArrayList<Remedy> remedies=new ArrayList<>();
        {
            Remedy remedy = new Remedy();
            remedy.setTitle("Remedy 1");
            remedy.setDescription("Hello description");
            remedy.getImage().setFileName(null, "helloworld");
            remedies.add(remedy);
        }
        {
            Remedy remedy = new Remedy();
            remedy.getImage().setFileName(null, "");
            remedy.setTitle("Remedy 2");
            remedies.add(remedy);
        }
        {
            Remedy remedy = new Remedy();
            remedy.setTitle("Remedy 3");
            remedies.add(remedy);
        }
        {
            Remedy remedy = new Remedy();
            remedy.getImage().setFileName(null, "helloworld");
            remedy.setTitle("Remedy 4");
            remedies.add(remedy);
        }
        {
            Remedy remedy = new Remedy();
            remedy.setTitle("Remedy 5");
            remedy.setDescription("Hello description for 5");
            remedy.getImage().setFileName(null, "helloworld");
            remedies.add(remedy);
        }
        {
            Remedy remedy = new Remedy();
            remedy.setTitle("Remedy 6");
            remedy.getImage().setFileName(null, "");
            remedies.add(remedy);
        }
        {
            Remedy remedy = new Remedy();
            remedy.setTitle("Remedy 7");
            remedy.getImage().setFileName(null, "helloworld");
            remedies.add(remedy);
        }

        RemedyFeedListAdapter remedyFeedListAdapter=new RemedyFeedListAdapter(mContext, remedies);
        recyclerView.setAdapter(remedyFeedListAdapter);
    }


    /**
     * Recycler View Adapter Class
     */
    class RemedyFeedListAdapter extends RecyclerView.Adapter<RemedyFeedListAdapter.ViewHolder> {

        Context mContext;
        ArrayList<Remedy> remedies;
        int TYPE_NO_IMAGE=0;
        int TYPE_IMAGE=1;

        public RemedyFeedListAdapter(Context context, ArrayList<Remedy> remedies) {
            this.mContext = context;
            this.remedies = remedies;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            int layoutId=R.layout.recycler_item_remedyfeed_no_image;
//            if(viewType==TYPE_IMAGE){
//                layoutId=R.layout.recycler_item_remedyfeed;
//            }
            View view = inflater.inflate(R.layout.recycler_item_remedyfeed, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if(remedies==null || remedies.size()==0){
                holder.title.setText("Remedy #"+position);
                return;
            }
            Remedy remedy=remedies.get(position);
            holder.title.setText(remedy.getTitle());
            holder.description.setText(remedy.getDescription());
            holder.diseases.setText(remedy.getDiseasesString());



//            if(holder.background!=null) {
                holder.background.setImageResource(R.drawable.stethoscope);
//                if(remedy.getRemedyImage()!=null){
//                    holder.background.setImageBitmap(remedy.getRemedyImage());
//                }else {
//                    Picasso.with(mContext).load(BackendUrls.REMEDY_IMAGE + remedy.getImage().getFileName()).into(holder.background);
//                }
            Bitmap bitmap=((BitmapDrawable)holder.background.getDrawable()).getBitmap();

            ImageView imageView=holder.background;
            Log.e("RemedyFeedAdapter",imageView.getWidth()+" "+imageView.getHeight()+" "+imageView.getLayoutParams().width+" "+imageView.getLayoutParams().height);
            Bitmap upperBitmap = Bitmap.createBitmap(bitmap,0,0,480,300/2);
            Palette.Builder paletteBuilder=Palette.from(upperBitmap);
            Palette palette=paletteBuilder.generate();
            Palette.Swatch swatch=palette.getVibrantSwatch();
            int textColor;
            try {
                textColor = swatch.getTitleTextColor();
                float[] hsv = new float[3];
                Color.colorToHSV(textColor,hsv);
                hsv[2]=hsv[2]>=0.5?0:1;
                textColor = Color.HSVToColor(hsv);
            }catch (NullPointerException e){
                e.printStackTrace();
                textColor= Color.BLACK;
            }
            holder.title.setTextColor(textColor);





//            }
        }

        @Override
        public int getItemCount() {
            return (remedies == null || remedies.size()==0) ? 1 : remedies.size();
        }

//        @Override
//        public int getItemViewType(int position) {
//            if(remedies!=null && remedies.size()!=0){
//                String fileName = remedies.get(position).getImage().getFileName();
//                if(fileName!=null && !fileName.isEmpty()){
//                    return TYPE_IMAGE;
//                }
//            }
//            return TYPE_NO_IMAGE;
//        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView title, description, diseases;
            ImageView background;
            ImageButton likeButton, dislikeButton;
            Button viewButton, shareButton;

            public ViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.title);
                description = (TextView) itemView.findViewById(R.id.remedyDescription);
                diseases = (TextView) itemView.findViewById(R.id.remedyDiseases);
                background = (ImageView) itemView.findViewById(R.id.background);
                likeButton = (ImageButton) itemView.findViewById(R.id.likeButton);
                dislikeButton = (ImageButton) itemView.findViewById(R.id.dislikeButton);
                viewButton = (Button) itemView.findViewById(R.id.viewButton);
                shareButton = (Button) itemView.findViewById(R.id.shareButton);


            }
        }
    }
}
