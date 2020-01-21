package ouyj.hyena.com.mytrial;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class MyButton extends AppCompatButton {


    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                Log.d(MainActivity.TAG, "子视图ACTION_DOWN");
                break;
            case MotionEvent.ACTION_UP:
                Log.d(MainActivity.TAG, "子视图ACTION_UP");
                break;
            case MotionEvent.ACTION_MOVE:
                //getParent().requestDisallowInterceptTouchEvent(false);
                Log.d(MainActivity.TAG, "子视图ACTION_MOVE");
        }
        return true;
    }
}
