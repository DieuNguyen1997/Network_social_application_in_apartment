package com.example.networksocialapplication;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import androidx.appcompat.widget.SearchView;

public class CustomSearchView extends SearchView {

    private OnBackPressedSearchViewListener mOnBackPressedSearchViewListener;

    public CustomSearchView(Context context) {
        super(context);
    }

    public CustomSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnBackPressedSearchViewListener(OnBackPressedSearchViewListener onBackPressedSearchViewListener) {
        mOnBackPressedSearchViewListener = onBackPressedSearchViewListener;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        Log.d("AMEN", "" + gainFocus);
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (mOnBackPressedSearchViewListener == null) return super.dispatchKeyEventPreIme(event);

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (mOnBackPressedSearchViewListener.needHideSoftKeyboard()) {
                return true;
            }
        }
        return super.dispatchKeyEventPreIme(event);
    }

    public interface OnBackPressedSearchViewListener {
        boolean needHideSoftKeyboard();
    }
}