package com.codeu.teamjacob.groups.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.codeu.teamjacob.groups.R;

public class FloatingButtonView extends View {

    //The log tag of the class
    public static final String LOG_TAG = FloatingButtonView.class.getName();

    private Paint mButtonPaint;
    private Paint mImagePaint;

    //The image to draw on the button
    private Bitmap buttonImage;

    //The destination dimensions of the image
    RectF destinationRect;

    public FloatingButtonView(Context context){
        super(context);
    }

    /**
     * Creates the floating button view using the custom attributes
     * @param context   the context the button is being created from
     * @param attrs     the set of custom attributes
     */
    public FloatingButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //Get the custom attributes for the view
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.FloatingButtonView, 0, 0);

        //Get the color of the button
        try {
            setColor(a.getColor(R.styleable.FloatingButtonView_buttonColor, Color.WHITE));
        } catch (Exception e) {
            Log.d(LOG_TAG, e.toString()); }

        //Get the image to be drawn on the button
        try {
            setImage(a.getDrawable(R.styleable.FloatingButtonView_buttonImage));
        } catch (Exception e) {
            Log.d(LOG_TAG, e.toString()); }


        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        a.recycle();
    }

    /**
     * Redraws the button when invalidated
     * @param canvas    the button canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        //Allow the button to be pressed
        setClickable(true);

        //Get the dimensions of the view
        int width = getWidth();
        int height = getHeight();

        //Draw the button circle
        canvas.drawCircle(width / 2, height / 2, (width / 2.6f), mButtonPaint);

        //Draw the image to the button if present
        if (buttonImage != null) {
            canvas.drawBitmap(buttonImage, (getWidth() - buttonImage.getWidth()) / 2,
                    (getHeight() - buttonImage.getHeight()) / 2, mImagePaint);
        }
    }

    /**
     * Sets the color of the button
     * @param color the new color of the button
     */
    public void setColor(final int color) {
        //Create the paint object for the circular button
        mButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mButtonPaint.setColor(color);
        mButtonPaint.setStyle(Paint.Style.FILL);
        mButtonPaint.setShadowLayer(10.0f, 0.0f, 3.5f, Color.argb(100, 0, 0, 0));

        //Redraw the view
        invalidate();
    }

    /**
     * Sets the image to be displayed on the button
     * @param image the image drawable
     */
    public void setImage(final Drawable image) {
        //Create the paint object for the image on the button
        mImagePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        buttonImage = ((BitmapDrawable) image).getBitmap();

        //Set the destination dimensions of the image
        destinationRect = new RectF(0,0,getWidth(),getHeight());

        //Redraw the view
        invalidate();
    }
}
