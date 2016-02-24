package architect.jazzy.medicinereminder.Fragments.OnlineActivity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import architect.jazzy.medicinereminder.Activities.RegistrationActivity;
import architect.jazzy.medicinereminder.HelperClasses.BackendUrls;
import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.Models.Remedy;
import architect.jazzy.medicinereminder.Models.RemedyFeedResult;
import architect.jazzy.medicinereminder.Models.User;
import architect.jazzy.medicinereminder.R;
import architect.jazzy.medicinereminder.Services.BackendInterfacer;

/**
 * A simple {@link Fragment} subclass.
 */
public class RemedyFeedFragment extends Fragment {

    RecyclerView recyclerView;
    Context mContext;
    RemedyFeedResult remedyFeedResult;
    public static final String TAG="RemedyFeedFragment";
    RelativeLayout loadingView;
    ArrayList<Remedy> remedies;
    boolean isUserLoggedIn = false;

    private static final int REMEDY_DETAIL_CODE = 1564;

    public RemedyFeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext = getActivity();
        remedyFeedResult = new RemedyFeedResult();

        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Remedies");

        return inflater.inflate(R.layout.fragment_remedy_feed, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        loadingView = (RelativeLayout) view.findViewById(R.id.loading);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setHasFixedSize(false);

        isUserLoggedIn = User.isUserLoggedIn(mContext);

        BackendInterfacer interfacer = new BackendInterfacer(mContext, BackendUrls.getRemedyFeed(), "GET", null);
        interfacer.setSimpleBackendListener(new BackendInterfacer.SimpleBackendListener() {

            @Override
            public void onPreExecute() {
                if (recyclerView.getAdapter() == null || recyclerView.getAdapter().getItemCount() == 0) {
                    Constants.setColoredProgressText((TextView) loadingView.findViewById(R.id.loadingText), null, 1500);
                } else {
                    Snackbar.make(recyclerView, "Loading...", Snackbar.LENGTH_LONG)
                            .show();
                }
            }

            @Override
            public void onResult(String result) {
                loadingView.setVisibility(View.GONE);
                try {
                    JSONObject responseObject = new JSONObject(result);
                    if (result == null || !responseObject.optBoolean("success")) {
                        Toast.makeText(mContext, "Error Loading: " + responseObject.optString("description").replace("_", " "), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    JSONObject data = responseObject.getJSONObject("data");
                    JSONArray remedies = data.optJSONArray("remedies");
                    ArrayList<Remedy> r = new ArrayList<>();
                    for (int i = 0; i < remedies.length(); i++) {
                        r.add(Remedy.parseRemedy(mContext, remedies.getJSONObject(i)));
                    }
                    remedyFeedResult.addRemedies(r);
                    remedyFeedResult.setCount(data.getInt("totalCount"));
                    remedyFeedResult.setCurrentPage(data.getInt("page"));
                    remedyFeedResult.setLimit(data.getInt("pageLimit"));

                    recyclerView.setAdapter(new RemedyFeedListAdapter(mContext, remedyFeedResult.getRemedies()));
                    //TODO: Add Lazy Loading

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        interfacer.execute();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == REMEDY_DETAIL_CODE) {
            Remedy remedy = data.getParcelableExtra("remedy");
            for (int i = 0; i < remedyFeedResult.getRemedies().size(); i++) {
                Log.e(TAG,"Checking remedy :"+remedyFeedResult.getRemedies().get(i).getId()+" for: "+remedy.getId());
                if (remedyFeedResult.getRemedies().get(i).getId().equalsIgnoreCase(remedy.getId())) {
                    remedyFeedResult.getRemedies().set(i, remedy);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isUserLoggedIn = User.isUserLoggedIn(getActivity());
    }

    /**
     * Recycler View Adapter Class
     */
    class RemedyFeedListAdapter extends RecyclerView.Adapter<RemedyFeedListAdapter.ViewHolder> {

        Context mContext;
        ArrayList<Remedy> remedies;
        int TYPE_NO_IMAGE = 0;
        int TYPE_IMAGE = 1;

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

        int selectionColor = getResources().getColor(R.color.buttonVoted);

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final Remedy remedy = remedies.get(position);
            holder.title.setText(remedy.getTitle());
            holder.description.setText(remedy.getDescription());
            holder.diseases.setText(remedy.getDiseasesString());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, RemedyDetailsAcitvity.class);
                    intent.putExtra("remedy", remedy);
                    startActivityForResult(intent, REMEDY_DETAIL_CODE);
                }
            });

            holder.viewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, RemedyDetailsAcitvity.class);
                    intent.putExtra("remedy", remedy);
                    startActivityForResult(intent, REMEDY_DETAIL_CODE);
                }
            });

            final Drawable upvoteDrawable = getResources().getDrawable(R.drawable.ic_action_like).mutate();
            final Drawable downvoteDrawable = getResources().getDrawable(R.drawable.ic_action_dontlike).mutate();

            if(isUserLoggedIn) {
                if (remedy.isUpvoted()) {
                    upvoteDrawable.setColorFilter(selectionColor, PorterDuff.Mode.SRC_ATOP);
                }
                if (remedy.isDownvoted()) {
                    downvoteDrawable.setColorFilter(selectionColor, PorterDuff.Mode.SRC_ATOP);
                }
            }
            holder.bookmark.setChecked(remedy.isBookmarked());

            holder.upVoteButton.setImageDrawable(upvoteDrawable);
            holder.downVoteButton.setImageDrawable(downvoteDrawable);

            if (isUserLoggedIn) {
                holder.upVoteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        remedy.setUpvoted(!remedy.isUpvoted());
                        remedy.setDownvoted(false);
                        holder.downVoteButton.setImageResource(R.drawable.ic_action_dontlike);
                        if (remedy.isUpvoted()) {
                            upvoteDrawable.setColorFilter(selectionColor, PorterDuff.Mode.SRC_ATOP);
                            holder.upVoteButton.setImageDrawable(upvoteDrawable);
                        } else {
                            holder.upVoteButton.setImageResource(R.drawable.ic_action_like);
                        }
                        remedy.upvote(mContext);
                    }
                });

                holder.downVoteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        remedy.setDownvoted(!remedy.isDownvoted());
                        remedy.setUpvoted(false);
                        holder.upVoteButton.setImageResource(R.drawable.ic_action_like);
                        if (remedy.isDownvoted()) {
                            downvoteDrawable.setColorFilter(selectionColor, PorterDuff.Mode.SRC_ATOP);
                            holder.downVoteButton.setImageDrawable(downvoteDrawable);
                        } else {
                            holder.downVoteButton.setImageResource(R.drawable.ic_action_dontlike);
                        }
                        remedy.downvote(mContext);
                    }
                });
                holder.bookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        remedy.setBookmarked(!remedy.isBookmarked());
                        remedy.bookmark(mContext);
                    }
                });
            } else {
                holder.upVoteButton.setOnClickListener(notLoggedInListener);
                holder.downVoteButton.setOnClickListener(notLoggedInListener);
                holder.bookmark.setOnClickListener(notLoggedInListener);
            }

            if (holder.background != null) {
                holder.background.setImageResource(R.drawable.stethoscope);
                if (remedy.getRemedyImage() != null) {
                    holder.background.setImageBitmap(remedy.getRemedyImage());
                } else {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Bitmap bitmap = Picasso.with(mContext)
                                        .load(BackendUrls.getRemedyImage(remedy.getImage().getFileName()))
                                        .get();

                                try {
                                    ImageView imageView = holder.background;
                                    Log.e("RemedyFeedAdapter", imageView.getWidth() + " " + imageView.getHeight() + " " + imageView.getLayoutParams().width + " " + imageView.getLayoutParams().height);
                                    Bitmap upperBitmap = Bitmap.createBitmap(bitmap, 0, 0, 480, 150);
                                    Palette.Builder paletteBuilder = Palette.from(upperBitmap);
                                    Palette palette = paletteBuilder.generate();
                                    Palette.Swatch swatch = palette.getVibrantSwatch();
                                    int textColor;
                                    try {
                                        textColor = swatch.getTitleTextColor();
                                        float[] hsv = new float[3];
                                        Color.colorToHSV(textColor, hsv);
                                        hsv[2] = hsv[2] >= 0.5 ? 0 : 1;
                                        textColor = Color.HSVToColor(hsv);
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                        textColor = Color.BLACK;
                                    }
                                    final int finalTextColor = textColor;
                                    holder.title.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            holder.title.setTextColor(finalTextColor);
                                        }
                                    });

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } catch (IOException e) {
                                holder.background.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.background.setImageResource(R.drawable.stethoscope);
                                    }
                                });
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();
                }
            }
        }

        @Override
        public int getItemCount() {
            return (remedies == null || remedies.size() == 0) ? 1 : remedies.size();
        }

        View.OnClickListener notLoggedInListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Login Required")
                        .setMessage("You need to login to be able to vote or comment")
                        .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).setNegativeButton("Login", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(mContext, RegistrationActivity.class));
                        dialogInterface.dismiss();
                    }
                })
                        .show();
            }
        };

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView title, description, diseases;
            ImageView background;
            ImageButton upVoteButton, downVoteButton;
            Button viewButton, shareButton;
            CheckBox bookmark;

            public ViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.title);
                description = (TextView) itemView.findViewById(R.id.remedyDescription);
                diseases = (TextView) itemView.findViewById(R.id.remedyDiseases);
                background = (ImageView) itemView.findViewById(R.id.background);
                upVoteButton = (ImageButton) itemView.findViewById(R.id.likeButton);
                downVoteButton = (ImageButton) itemView.findViewById(R.id.dislikeButton);
                viewButton = (Button) itemView.findViewById(R.id.viewButton);
                shareButton = (Button) itemView.findViewById(R.id.shareButton);
                bookmark=(CheckBox)itemView.findViewById(R.id.checkboxBookmarked);


            }
        }
    }
}
