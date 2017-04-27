package com.example.foad.doodle;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by foad on 27/4/17.
 */

public class ColorPicker extends LinearLayout {

    int mSelectedColor;

    public ColorPicker(Context context) {
        super(context);
        init();
    }

    public ColorPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setOrientation(HORIZONTAL);
        final int[] colors = getContext().getResources().getIntArray(R.array.color_picker_colors_array);

        for (int i = 0; i < colors.length; i++){


            View view = new View(getContext());
            view.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
            view.setBackgroundColor(colors[i]);
            final int color = colors[i];
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectedColor = color;
                    ((View)v.getParent()).performClick();
                }
            });

            addView(view);

        }
    }

    public int getSelectedColor(){
        return mSelectedColor;
    }

}
