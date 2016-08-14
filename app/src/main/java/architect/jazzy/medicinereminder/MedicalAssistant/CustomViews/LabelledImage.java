package architect.jazzy.medicinereminder.MedicalAssistant.CustomViews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import architect.jazzy.medicinereminder.R;

/**
 * Created by Jibin_ism on 25-Mar-15.
 */
public class LabelledImage extends LinearLayout {

    private int h=-1,m=-1;
    private String TAG="",viewSchedule="";
    ViewClickListener viewClickListener;
    public boolean state=true;
    public ImageView image;
    public TextView textView,textViewAuxillary;
    private Context context;
    private String NAMESPACE="http://schemas.android.com/apk/res/android";
    private int imageSrcResource;
    String textStringResource;
    Boolean viewInitialState;
    private int color;
    private LabelledImage thisView;
    private int textColor;
    private boolean noAuxillaryText = false;
    public LabelledImage(Context context) {
        this(context, null);
    }

    public LabelledImage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LabelledImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        initialize(attrs);
    }

    protected void initialize(AttributeSet attrs)
    {
        inflate(getContext(), R.layout.labeled_image, this);
        thisView=this;
        image=(ImageView)findViewById(R.id.jazzy_image);
        textView=(TextView)findViewById(R.id.jazzy_text);
        textViewAuxillary=(TextView)findViewById(R.id.jazzy_textAuxillary);

        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                state=!state;
                if(viewClickListener!=null)
                    viewClickListener.onImageClick(thisView);
            }
        });

        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewClickListener!=null)
                    viewClickListener.onTopTextClick(thisView);
            }
        });

        if(attrs!=null) {
            imageSrcResource = attrs.getAttributeResourceValue(NAMESPACE, "src", 0);
            textStringResource = attrs.getAttributeValue(NAMESPACE,"text");
            color=attrs.getAttributeResourceValue(NAMESPACE,"textColor",Color.BLACK);
            noAuxillaryText=attrs.getAttributeBooleanValue(NAMESPACE,"keepScreenOn",false);
            this.textColor=color;
            viewInitialState=attrs.getAttributeBooleanValue(NAMESPACE,"saveEnabled",false);


            if(imageSrcResource>0)
                image.setImageResource(imageSrcResource);

            if(!textStringResource.isEmpty())
                textViewAuxillary.setText(textStringResource);

            if(!viewInitialState)
            {
                state=false;
                this.setGrayScale();
            }
        }

        if(noAuxillaryText){
            textViewAuxillary.setVisibility(View.GONE);
            LinearLayout.LayoutParams layoutParams=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 5);
            textView.setLayoutParams(layoutParams);
            textView.setGravity(Gravity.CENTER);
            textView.setText(textStringResource);
            textView.setTextSize(14);
        }
    }

    public void setTextColor(int color)
    {
        this.textView.setTextColor(color);
        this.textViewAuxillary.setTextColor(color);
        this.textColor=color;
    }

    public void setText(String s1, String s2)
    {

        if(s2.isEmpty() || s2.contains("---") && !textStringResource.isEmpty())
            this.textViewAuxillary.setText(textStringResource);
        else
            this.textViewAuxillary.setText(s2);
        this.textView.setText(s1);
    }

    public void setTextSize(float size)
    {
        this.textView.setTextSize(size);
    }

    public String getText()
    {
        return this.textView.getText().toString();
    }

    public void setGrayScale(){
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0.1f);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        this.image.setColorFilter(filter);
        this.textView.setTextColor(Color.GRAY);
        this.textViewAuxillary.setTextColor(Color.GRAY);
    }

    public void setNormalColor(){
        this.image.clearColorFilter();
        this.textView.setTextColor(this.textColor);
        this.textViewAuxillary.setTextColor(this.textColor);
    }

    public boolean getState(){
        return this.state;
    }

    public void setState(boolean b)
    {
        this.state=b;
    }


    public interface ViewClickListener{
        public void onImageClick(LabelledImage labelledImageView);
        public void onTopTextClick(LabelledImage labelledImageView);
    }

    public void setViewClickListener(ViewClickListener viewClickListener)
    {
        this.viewClickListener=viewClickListener;
    }

    public void removeViewClickListener()
    {
        this.viewClickListener=null;
    }


    public void setTag(String s)
    {
        this.TAG=s;
    }

    public String getTag()
    {
        return this.TAG;
    }

    public void setTime(int h,int m)
    {
        this.h=h;
        this.m=m;
    }

    public int[] getTime()
    {
        return new int[] {this.h,this.m};
    }

    public void clearTime()
    {
        this.h=-1;
        this.m=-1;
    }

    public String getAuxText()
    {
        return this.viewSchedule;
    }

    public void setTextAux(String s){
        this.viewSchedule=s;
    }
}

