package architect.jazzy.medicinereminder.Fragments.OnlineActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import architect.jazzy.medicinereminder.Activities.RegistrationActivity;
import architect.jazzy.medicinereminder.CustomViews.CapitalTextView;
import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.Models.Remedy;
import architect.jazzy.medicinereminder.Models.User;
import architect.jazzy.medicinereminder.R;

public class RemedyDetailsAcitvity extends AppCompatActivity {

    EditText remedyDescription, remedyReferences, remedyDiseases, remedyTags;
    CapitalTextView remedyTitle;
    Button saveButton, addComment;
    Remedy remedy;
    Context mContext;
    LinearLayout statsLayout, commentLayout;
    TextView upvoteCount, downvoteCount;
    LinearLayout upVoteLayout, downVoteLayout;
    ImageView remedyImage;
    FloatingActionButton fab;
    CollapsingToolbarLayout appBarLayout;
    int imageIndex = 0;
    boolean isUserLoggedIn=false;
    Drawable backgroundImage;

    @Override
    protected void onResume() {
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        isUserLoggedIn= User.isUserLoggedIn(mContext);
        setContentView(R.layout.activity_remedy_details_acitvity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Remedy");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        remedyTitle = (CapitalTextView) findViewById(R.id.title);
        remedyDescription = (EditText) findViewById(R.id.remedyDescription);
        remedyDiseases = (EditText) findViewById(R.id.remedyDiseases);
        remedyTags = (EditText) findViewById(R.id.tags);
        remedyReferences = (EditText) findViewById(R.id.references);
        saveButton = (Button) findViewById(R.id.saveButton);
        statsLayout = (LinearLayout) findViewById(R.id.stats);
        upVoteLayout = (LinearLayout) findViewById(R.id.upVoteLayout);
        downVoteLayout = (LinearLayout) findViewById(R.id.downVoteLayout);
        upvoteCount = (TextView) upVoteLayout.findViewById(R.id.upVoteCount);
        downvoteCount = (TextView) downVoteLayout.findViewById(R.id.downVoteCount);
        remedyImage = (ImageView)findViewById(R.id.remedyImage);
        fab = (FloatingActionButton)findViewById(R.id.fab);
        appBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        commentLayout = (LinearLayout)findViewById(R.id.commentLayout);
        addComment = (Button) commentLayout.findViewById(R.id.addComment);

        if(isUserLoggedIn) {
            upVoteLayout.setOnClickListener(voteClickListener);
            downVoteLayout.setOnClickListener(voteClickListener);
        }else{
            upVoteLayout.setOnClickListener(notLoggedInListener);
            downVoteLayout.setOnClickListener(notLoggedInListener);
        }

        selectionColor = getResources().getColor(R.color.buttonVoted);

        Intent in = getIntent();
        remedy = in.getParcelableExtra("remedy");
        imageIndex = in.getIntExtra("image",0);
        remedyImage.setImageResource(Constants.backgroundImages[imageIndex]);
        backgroundImage = getResources().getDrawable(Constants.backgroundImages[imageIndex]);

        Palette.Builder builder = Palette.from(((BitmapDrawable)backgroundImage).getBitmap());
        Palette palette = builder.generate();
        selectionColor = palette.getVibrantColor(0);
        fab.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{Constants.getFABColor(selectionColor)}));
        appBarLayout.setContentScrimColor(selectionColor);
        appBarLayout.setStatusBarScrimColor(selectionColor);
        commentLayout.setBackgroundColor(selectionColor);
        if (remedy == null) {
            newRemedy();
            return;

        }
        remedyDetails();
    }

    int selectionColor;
    View.OnClickListener voteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int factor=1;
            if (view.getId() == R.id.upVoteLayout) {
                remedy.setDownvoted(false);
                //Make Down vote icon to default
                ((ImageView)downVoteLayout.getChildAt(0)).setImageResource(R.drawable.ic_action_dontlike);

                //id already upvoted then remove upvote
                //else reduce downvote and increment upvote
                if(remedy.isUpvoted()){
                    ((ImageView)upVoteLayout.getChildAt(0)).setImageResource(R.drawable.ic_action_like);
                    factor=-1;
                    remedy.setUpvoted(false);
                }else{
                    //Setting selected icon
                    Drawable drawable=getResources().getDrawable(R.drawable.ic_action_like).mutate();
                    drawable.setColorFilter(selectionColor, PorterDuff.Mode.SRC_ATOP);
                    ((ImageView)upVoteLayout.getChildAt(0)).setImageDrawable(drawable);
                    remedy.setUpvoted(true);
                    if(remedy.isDownvoted()){
                        remedy.getStats().setDownvote(remedy.getStats().getDownvote()-1);
                    }
                }
                remedy.getStats().setUpvote(remedy.getStats().getUpvote()+factor);
                remedy.upvote(mContext);
            } else if (view.getId() == R.id.downVoteLayout) {
                remedy.setUpvoted(false);
                ((ImageView)upVoteLayout.getChildAt(0)).setImageResource(R.drawable.ic_action_like);
                if(remedy.isDownvoted()){
                    factor=-1;
                    ((ImageView)downVoteLayout.getChildAt(0)).setImageResource(R.drawable.ic_action_dontlike);
                    remedy.setDownvoted(false);
                }else{
                    Drawable drawable=getResources().getDrawable(R.drawable.ic_action_dontlike).mutate();
                    drawable.setColorFilter(selectionColor, PorterDuff.Mode.SRC_ATOP);
                    ((ImageView)downVoteLayout.getChildAt(0)).setImageDrawable(drawable);
                    remedy.setDownvoted(true);
                    if(remedy.isUpvoted()){
                        remedy.getStats().setUpvote(remedy.getStats().getUpvote()-1);
                    }
                }
                remedy.getStats().setDownvote(remedy.getStats().getDownvote()+factor);
                remedy.downvote(mContext);
            }
            ((TextView)upVoteLayout.getChildAt(1)).setText(Html.fromHtml(remedy.getStats().getUpvote()+" Upvotes"));
            ((TextView)downVoteLayout.getChildAt(1)).setText(Html.fromHtml(remedy.getStats().getDownvote()+" Downvotes"));
        }
    };


    void newRemedy(){
        clearLayout();
    }

    void editRemedy() {
        clearLayout();
    }

    void clearLayout(){
        saveButton.setVisibility(View.VISIBLE);
        statsLayout.setVisibility(View.GONE);
        remedyDescription.setFocusable(true);
        remedyTags.setFocusable(true);
//        remedyTitle.setFocusable(true);
        remedyReferences.setFocusable(true);
        remedyDiseases.setFocusable(true);
    }




    void remedyDetails() {
        saveButton.setVisibility(View.GONE);
        statsLayout.setVisibility(View.VISIBLE);

        remedyTitle.setRawText(remedy.getTitle());
        
        int editTextBackgroundId =R.drawable.login_edit_text;
        remedyTitle.setBackgroundResource(editTextBackgroundId);

        remedyDescription.setText(Html.fromHtml(remedy.getDescription()));
        remedyDescription.setBackgroundResource(editTextBackgroundId);

        upvoteCount.setText(Html.fromHtml("<b>" + remedy.getStats().getUpvote() + "</b> Upvotes"));
        downvoteCount.setText(Html.fromHtml("<b>" + remedy.getStats().getDownvote() + "</b> Downvotes"));

        if(remedy.getTagsString().isEmpty()){
            remedyTags.setVisibility(View.GONE);
        }else {
            remedyTags.setText(Html.fromHtml("<i>Tags:</i> " + remedy.getTagsString()));
            remedyTags.setBackgroundResource(editTextBackgroundId);
        }

        if(remedy.getDiseasesString().isEmpty()){
            remedyDiseases.setVisibility(View.GONE);
        }else {
            remedyDiseases.setText(Html.fromHtml("<i>Diseases:</i> " + remedy.getDiseasesString()));
            remedyDiseases.setBackgroundResource(editTextBackgroundId);
        }

        if(remedy.getReferencesString().isEmpty()){
            remedyReferences.setVisibility(View.GONE);
        }else {
            remedyReferences.setText(Html.fromHtml("<i>References:</i> " + remedy.getReferencesString()));
            remedyReferences.setBackgroundResource(editTextBackgroundId);
        }

        if(remedy.isDownvoted()){
            Drawable drawable=getResources().getDrawable(R.drawable.ic_action_dontlike).mutate();
            drawable.setColorFilter(selectionColor, PorterDuff.Mode.SRC_ATOP);
            ((ImageView)downVoteLayout.getChildAt(0)).setImageDrawable(drawable);
        }

        if(remedy.isUpvoted()){
            Drawable drawable=getResources().getDrawable(R.drawable.ic_action_like).mutate();
            drawable.setColorFilter(selectionColor, PorterDuff.Mode.SRC_ATOP);
            ((ImageView)upVoteLayout.getChildAt(0)).setImageDrawable(drawable);
        }

        remedyDescription.setFocusable(false);
        remedyTags.setFocusable(false);
//        remedyTitle.setFocusable(false);
        remedyReferences.setFocusable(false);
        remedyDiseases.setFocusable(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent();
            intent.putExtra("remedy", remedy);
            setResult(RESULT_OK, intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("remedy", remedy);
        setResult(RESULT_OK, intent);
        finish();
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
}
