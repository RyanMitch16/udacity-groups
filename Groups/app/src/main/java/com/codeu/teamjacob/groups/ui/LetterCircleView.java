package com.codeu.teamjacob.groups.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.codeu.teamjacob.groups.R;

import java.util.Random;

public class LetterCircleView extends View{

    Paint mCirclePaint;
    TextPaint letterPaint;
    private final Rect mBounds = new Rect();

    String mCharcter;

    public LetterCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setColor(Color.GRAY);
        setLetter('A');

        //setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Allow the button to be pressed
        setClickable(true);

        //Get the dimensions of the view
        int width = getWidth();
        int height = getHeight();

        //Draw the button circle
        if (mCirclePaint != null){
            canvas.drawCircle(width / 2, height / 2, (width / 4f), mCirclePaint);}

        if (letterPaint != null) {
            canvas.drawText(mCharcter, width / 2,
                    height / 2 - ((letterPaint.descent() + letterPaint.ascent()) / 2), letterPaint);
        }

    }

    /**
     * Sets the color of the button
     * @param color the new color of the button
     */
    public void setColor(final int color) {
        //Create the paint object for the circular button
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(color);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setShadowLayer(10.0f, 0.0f, 3.5f, Color.argb(100, 0, 0, 0));

        //Redraw the view
        invalidate();
    }

    public void setLetter(char letter){

        letterPaint = new TextPaint();
        letterPaint.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        letterPaint.setColor(Color.WHITE);
        letterPaint.setTextAlign(Paint.Align.CENTER);
        letterPaint.setAntiAlias(true);
        letterPaint.getTextBounds((letter + ""), 0, 1, mBounds);

        letterPaint.setTextSize(80);
        mCharcter = letter+"";
        invalidate();

        Random rand = new Random(74*letter);

        float[] hsv = new float[3];
        Color.colorToHSV(getResources().getColor(R.color.material_primary), hsv);
        float begin = '0';
        float end = 'z';
        hsv[0] = (((((float) letter) - begin) / (end - begin) + rand.nextFloat())*360.f) % 360.f;
        Log.d("XX", hsv[0]+"");
        setColor(Color.HSVToColor(hsv));
    }

}
