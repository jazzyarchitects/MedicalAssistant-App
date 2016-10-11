package architect.jazzy.medicinereminder.RemedySharing.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.MedicalAssistant.CustomViews.GraduallyTextView;
import architect.jazzy.medicinereminder.R;
import architect.jazzy.medicinereminder.RemedySharing.HelperClasses.BackendUrls;
import architect.jazzy.medicinereminder.RemedySharing.Models.Remedy;
import architect.jazzy.medicinereminder.RemedySharing.Models.RemedyFeedResult;
import architect.jazzy.medicinereminder.RemedySharing.Models.User;
import architect.jazzy.medicinereminder.RemedySharing.RemedyDetailsAcitvity;
import architect.jazzy.medicinereminder.RemedySharing.Services.BackendInterfacer;

/**
 * A simple {@link Fragment} subclass.
 */
public class RemedyFeedFragment extends Fragment {

  public static final String TAG = "RemedyFeedFragment";
  private static final int REMEDY_DETAIL_CODE = 1564;
  RecyclerView recyclerView;
  Context mContext;
  RemedyFeedResult remedyFeedResult;
  RelativeLayout loadingView;
  GraduallyTextView graduallyTextView;
  boolean isUserLoggedIn = false;

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
    graduallyTextView = (GraduallyTextView) loadingView.findViewById(R.id.loadingText);
    recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    recyclerView.setHasFixedSize(false);

    isUserLoggedIn = User.isUserLoggedIn(mContext);

    BackendInterfacer interfacer = new BackendInterfacer(mContext, BackendUrls.getRemedyFeed(), "GET", null);
    interfacer.setSimpleBackendListener(new BackendInterfacer.SimpleBackendListener() {

      @Override
      public void onPreExecute() {
        if (recyclerView.getAdapter() == null || recyclerView.getAdapter().getItemCount() == 0) {
          graduallyTextView.startLoading();
        } else {
          Snackbar.make(recyclerView, "Loading...", Snackbar.LENGTH_LONG)
              .show();
        }
      }

      @Override
      public void onResult(String result) {
        graduallyTextView.stopLoading();
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
//        super.onActivityResult(requestCode, resultCode, data);
//        if (data == null) {
//            return;
//        }
//        if (requestCode == REMEDY_DETAIL_CODE) {
//            Remedy remedy = data.getParcelableExtra("remedy");
//            for (int i = 0; i < remedyFeedResult.getRemedies().size(); i++) {
//                Log.e(TAG, "Checking remedy :" + remedyFeedResult.getRemedies().get(i).getId() + " for: " + remedy.getId());
//                if (remedyFeedResult.getRemedies().get(i).getId().equalsIgnoreCase(remedy.getId())) {
//                    remedyFeedResult.getRemedies().set(i, remedy);
//                }
//            }
//        }
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
    ArrayList<Integer> images = new ArrayList<>();


    public RemedyFeedListAdapter(Context context, ArrayList<Remedy> remedies) {
      this.mContext = context;
      this.remedies = remedies;
      images = new ArrayList<>(Constants.backgroundImages);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      View view = inflater.inflate(R.layout.recycler_item_remedyfeed, parent, false);
      return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
      final Remedy remedy = remedies.get(position);
      holder.title.setText(remedy.getTitle());
      holder.description.setText(Html.fromHtml(remedy.getDescription()).toString());
//            holder.diseases.setText(Html.fromHtml(remedy.getDiseasesString()));

      if (images == null || images.isEmpty()) {
        images = new ArrayList<>(Constants.backgroundImages);
      }
      final int random = remedy.getImageIndex() < 0 ? (new Random()).nextInt(images.size()) : remedy.getImageIndex();
      final int imageIndex = Constants.backgroundImages.indexOf(images.get(random));

      Log.e(TAG, "Random: " + random + ", Image: " + images.get(random) + "\nimageIndex: " + imageIndex + ", Image: " + Constants.backgroundImages.get(imageIndex));

      holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Intent intent = new Intent(mContext, RemedyDetailsAcitvity.class);
          intent.putExtra("remedy", remedy);
          intent.putExtra("image", imageIndex);
          startActivityForResult(intent, REMEDY_DETAIL_CODE);
        }
      });

      holder.viewButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Intent intent = new Intent(mContext, RemedyDetailsAcitvity.class);
          intent.putExtra("remedy", remedy);
          intent.putExtra("image", imageIndex);
          startActivityForResult(intent, REMEDY_DETAIL_CODE);
        }
      });

      remedies.get(position).setImageIndex(imageIndex);
      holder.background.setImageResource(Constants.backgroundImages.get(imageIndex));
      images.remove(random);
      Log.e(TAG, "Images size: " + images.size());
      Log.e(TAG, "BackgroundImages size: " + Constants.backgroundImages.size());

    }

    @Override
    public int getItemCount() {
      return (remedies == null || remedies.size() == 0) ? 1 : remedies.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
      TextView title, description;
      ImageView background;
      Button viewButton;
      Button shareButton;


      //--------------------------------------------------------------------------------------
//            TextView diseases;
//            ImageButton upVoteButton, downVoteButton;
//            ImageButton shareButton;
//            CheckBox bookmark;
      //--------------------------------------------------------------------------------------

      public ViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.title);
        description = (TextView) itemView.findViewById(R.id.remedyDescription);
        background = (ImageView) itemView.findViewById(R.id.background);
        viewButton = (Button) itemView.findViewById(R.id.viewButton);
        shareButton = (Button) itemView.findViewById(R.id.shareButton);

        //----------------------------------------------------------------------------------
//                diseases = (TextView) itemView.findViewById(R.id.remedyDiseases);
//                shareButton = (ImageButton) itemView.findViewById(R.id.shareButton);
//                bookmark = (CheckBox) itemView.findViewById(R.id.checkboxBookmarked);
//                upVoteButton = (ImageButton) itemView.findViewById(R.id.likeButton);
//                downVoteButton = (ImageButton) itemView.findViewById(R.id.dislikeButton);
        //----------------------------------------------------------------------------------


      }
    }
  }
}
