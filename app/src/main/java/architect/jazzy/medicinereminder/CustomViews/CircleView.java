package architect.jazzy.medicinereminder.CustomViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import architect.jazzy.medicinereminder.R;

/**
 * Created by Jibin_ism on 29-Aug-15.
 */
public class CircleView extends View implements View.OnTouchListener {

    boolean isButton;
    Paint strokePaint;
    Paint fillPaint;
    Paint shadowPaint;
    Paint textPaint, bigTextPaint;
    RectF arcRect;
    Rect bounds, bigTextBound;
    Paint linePaint;
    Integer strokeColor, fillColor, pressedFillColor, textColor;
    float strokeWidth, textSize, padding, bigTextSize;
    int divCount;
    String text, bigText;
    int centerX = 0, centerY = 0;


    public CircleView(Context context) {
        this(context, null);
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleView, 0, 0);

        try {
            text = a.getString(R.styleable.CircleView_text);
            fillColor = a.getInteger(R.styleable.CircleView_fillColor, Color.parseColor("#ffffff"));
            strokeColor = a.getInteger(R.styleable.CircleView_strokeColor, fillColor);
            textColor = a.getInteger(R.styleable.CircleView_textColor, Color.parseColor("#000000"));
            isButton = a.getBoolean(R.styleable.CircleView_isButton, false);
            strokeWidth = a.getDimension(R.styleable.CircleView_strokeWidth, 2);
            textSize = a.getDimension(R.styleable.CircleView_textSize, 16);
            divCount = a.getInteger(R.styleable.CircleView_divCount, 1);
            bigText=a.getString(R.styleable.CircleView_bigText);
            bigTextSize=a.getDimension(R.styleable.CircleView_bigTextSize,20);
        } catch (Exception e) {
            e.printStackTrace();
        }

        a.recycle();

        init();

        this.setOnTouchListener(this);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {

        fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setAntiAlias(true);
        fillPaint.setColor(fillColor);
        fillPaint.setShader(new Shader());

        strokePaint = new Paint();
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setAntiAlias(true);
        strokePaint.setStrokeWidth(strokeWidth);
        strokePaint.setColor(strokeColor);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);

        bigTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
        bigTextPaint.setTextAlign(Paint.Align.CENTER);
        bigTextPaint.setColor(textColor);
        bigTextPaint.setTextSize(bigTextSize);

        shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);

        linePaint = new Paint();
        linePaint.setStrokeWidth(2);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(Color.BLACK);

        setPressedFillColor();

        bounds = new Rect();
        bigTextBound=new Rect();

        arcRect = new RectF(getX(), getY(), getX() + getMeasuredWidth(), getY() + getMeasuredHeight());
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int centerX = this.getMeasuredWidth() / 2;
        int centerY = this.getMeasuredHeight() / 2;
        int width = getMeasuredWidth() - 10 - (int) padding;
        int height = getMeasuredHeight() - 10 - (int) padding;
        int radius;
        if (width > height) {
            radius = height / 2;
        } else {
            radius = width / 2;
        }
        radius -= strokeWidth;
        int yoff = 0;
        fillPaint.setShadowLayer(radius, strokeWidth + 2, strokeWidth + 2, Color.BLACK);
        canvas.drawCircle(centerX, centerY, radius, fillPaint);
        canvas.drawCircle(centerX, centerY, radius, strokePaint);

        try{
            bigTextPaint.getTextBounds(bigText,0,1,bigTextBound);
            textPaint.getTextBounds(text, 0, 1, bounds);
            canvas.drawText(bigText,centerX,centerY+yoff,bigTextPaint);
            yoff+=bigTextBound.height();
            String[] lines = text.split("\n");
            for (String s : lines) {
                canvas.drawText(s, centerX, centerY + yoff, textPaint);
                yoff += 0.75* bigTextBound.height();
            }
        }catch (Exception e){
            try{
                textPaint.getTextBounds(text, 0, 1, bounds);
                String[] lines = text.split("\n");
                yoff =-((lines.length - 1) * bounds.height()) / 2;
                for (String s : lines) {
                    canvas.drawText(s, centerX, centerY + yoff, textPaint);
                    yoff += bounds.height() + 0.5 * bounds.height();
                }
            }catch (Exception e1){
                if(!text.isEmpty())
                    canvas.drawText(text, centerX, centerY + yoff, textPaint);

            }
        }

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        Log.e(TAG,"Is Button: "+isButton+" MotionEvent: "+event.getAction() );
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isButton) {
//                    Log.e(TAG, "Changing color to pressed");
                    this.fillPaint.setColor(this.pressedFillColor);
                    invalidate();
                    requestLayout();
                }
                break;
            case MotionEvent.ACTION_UP:
                this.performClick();
            case MotionEvent.ACTION_CANCEL:
                if (isButton) {
//                    Log.e(TAG, "Changing color to Lifted");
                    this.fillPaint.setColor(this.fillColor);
                    invalidate();
                    requestLayout();
                }
                break;
            default:
                break;
        }
        return true;
    }


    private void setPressedFillColor() {
        float[] hsv = new float[3];
        Color.colorToHSV(fillColor, hsv);
        hsv[2] = hsv[2]>0.3f?hsv[2]-0.5f:hsv[2]>0.1?hsv[2]-0.10f:0;
        this.pressedFillColor = Color.HSVToColor(hsv);
    }

    /**
     * Getter Setter functions
     **/
    public Integer getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(Integer strokeColor) {
        this.strokeColor = strokeColor;
        invalidate();
        requestLayout();
    }

    public Integer getFillColor() {
        return fillColor;
    }

    public void setFillColor(Integer fillColor) {
        this.fillColor = fillColor;
        setPressedFillColor();
        invalidate();
        requestLayout();
    }

    public Integer getTextColor() {
        return textColor;
    }

    public void setTextColor(Integer textColor) {
        this.textColor = textColor;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
        requestLayout();
    }

    public int getCenterX() {
        return centerX;
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public void setCenterY(int centerY) {
        this.centerY = centerY;
    }

    public String getBigText() {
        return bigText;
    }

    public void setBigText(String bigText) {
        this.bigText = bigText;
    }

    public float getBigTextSize() {
        return bigTextSize;
    }

    public void setBigTextSize(float bigTextSize) {
        this.bigTextSize = bigTextSize;
    }
}
