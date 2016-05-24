package architect.jazzy.medicinereminder.CustomViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.TextView;

import architect.jazzy.medicinereminder.R;


/**
 * Created by Jibin_ism on 03-Mar-16.
 */
public class CapitalEditText extends TextView {

    String thisText = "";
    Context mContext;
    int scaleFactor = 1;
    boolean scaleAllWords = false;

    public CapitalEditText(Context context) {
        this(context, null);
    }

    public CapitalEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CapitalEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(attrs);
    }


    public void init(AttributeSet attributeSet) {

        TypedArray a = mContext.obtainStyledAttributes(attributeSet, R.styleable.CapitalTextView);
        if (attributeSet != null) {
            scaleFactor = a.getInt(R.styleable.CapitalTextView_letterScaleFactor, 2)-1;
            scaleAllWords = a.getBoolean(R.styleable.CapitalTextView_focusAllWords, false);
//            thisText = "My Name is Jibin";
//            thisText =
            thisText= attributeSet.getAttributeValue("http://schemas.android.com/apk/res/android","text");
            styleText();
            this.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    thisText = charSequence.toString();
                    styleText();
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

        }
        a.recycle();
    }

    private void styleText(){
        if(thisText == null || thisText.isEmpty()){
            return;
        }
        thisText = thisText.toUpperCase();
        String[] strings = thisText.split(" ");
        String finalString = "";
        for (int i=0;i<strings.length;i++) {
            String string = strings[i];
            int k = 0;
            int protection = 0;
            char c;
            do {
                c = string.charAt(k);
                int asciiIndex = (int) c;
                if ((asciiIndex <= (int) 'Z' && asciiIndex >= (int) 'A') || (asciiIndex >= (int) '0' && asciiIndex <= (int) '9')) {
                    break;
                }
                k++;
                protection++;
            } while (protection <= 100);
            if (i == 0 || (i > 0 && scaleAllWords)) {
                String startTag = "";
                String endTag = "";
                for (int j = 0; j < scaleFactor; j++) {
                    startTag += "<big>";
                    endTag += "</big>";
                }
                finalString += startTag + string.toCharArray()[0] + endTag;
//                finalString += "<font size=\"" + scaleFactor * this.getTextSize() + "px\">" + string.toCharArray()[0] + "</font>";
                for (int j = 1; j < string.length(); j++) {
                    finalString += string.charAt(j);
                }
            } else {
                finalString += string;
            }
            finalString += " ";
        }
        this.setText(Html.fromHtml(finalString));
    }

    public String getText() {
        return thisText;
    }

    public void setRawText(String text) {
        this.thisText = text;
        styleText();
    }

    public int getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(int scaleFactor) {
        this.scaleFactor = scaleFactor;
        styleText();
    }

    public boolean isScaleAllWords() {
        return scaleAllWords;
    }

    public void setScaleAllWords(boolean scaleAllWords) {
        this.scaleAllWords = scaleAllWords;
        styleText();
    }
}
