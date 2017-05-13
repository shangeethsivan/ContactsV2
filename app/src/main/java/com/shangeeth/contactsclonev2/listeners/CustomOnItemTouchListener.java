package com.shangeeth.contactsclonev2.listeners;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Shangeeth Sivan on 13/05/17.
 */

public class CustomOnItemTouchListener implements RecyclerView.OnItemTouchListener {

    OnItemClickListener mOnItemClickListener;
    GestureDetector mGestureDetector;
    Context mContext;

    private static final String TAG = "CustomOnItemTouchListen";

    public CustomOnItemTouchListener(Context pContext, OnItemClickListener pOnItemClickListener) {
        mContext = pContext;
        mGestureDetector = new GestureDetector(pContext, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                Log.d(TAG, "onLongPress: ");
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.d(TAG, "onFling: "+velocityX+" "+velocityY );
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                return true;
            }
        });
        this.mOnItemClickListener = pOnItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick();
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View lView = rv.findChildViewUnder(e.getX(),e.getY());
        if(lView!=null && mOnItemClickListener!=null && mGestureDetector.onTouchEvent(e)){
            mOnItemClickListener.onItemClick();
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
