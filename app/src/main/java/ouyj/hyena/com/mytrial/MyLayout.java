package ouyj.hyena.com.mytrial;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class MyLayout extends LinearLayout {

    public MyLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 父容器拦截Move和Up
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //false（不拦截）true（进行拦截）
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return false;
            case MotionEvent.ACTION_MOVE:
                return false;
            case MotionEvent.ACTION_UP:
                return true;
            default:
                break;
        }
        return false;

        //return true;
    }
    /**
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.d(MainActivity.TAG, "父容器ACTION_DOWN！");
                break;
            case MotionEvent.ACTION_UP:
                Log.d(MainActivity.TAG, "父容器ACTION_UP！");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(MainActivity.TAG, "父容器ACTION_MOVE！");
        }
        return true;
    }
}
