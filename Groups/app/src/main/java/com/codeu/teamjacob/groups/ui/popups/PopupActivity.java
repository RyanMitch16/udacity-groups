package com.codeu.teamjacob.groups.ui.popups;

import android.app.Activity;
import android.content.res.Resources;

public class PopupActivity extends Activity {

    //Get the dpi density of the device
    private static final int densityDpi = Resources.getSystem().getDisplayMetrics().densityDpi;

    private int widthPixels = Resources.getSystem().getDisplayMetrics().widthPixels;
    private int heightPixels = Resources.getSystem().getDisplayMetrics().heightPixels;

    /**
     * Convert the dp value into pixels
     * @param dp    the dp value to convert to pixels
     * @return      the number of pixels the dp value represents
     */
    public int convertDpToPixels(int dp){
        return (int) (dp * (densityDpi / 160f));
    }

    /**
     * Convert the percentage that represents percent of the screen width to pixels
     * @param percent   the percentage of the screen width (fraction from 0 to 1)
     * @return          the number of pixels the percentage represents
     */
    public int convertWidthPercentToPixels(float percent){

        //Get the number of pixels
        return (int) (percent * widthPixels);
    }

    /**
     * Convert the percentage that represents percent of the screen height to pixels
     * @param percent   the percentage of the screen height (fraction from 0 to 1)
     * @return          the number of pixels the percentage represents
     */
    public int convertHeightPercentToPixels(float percent){

        //Get the number of pixels
        return (int) (percent * heightPixels);
    }
}
