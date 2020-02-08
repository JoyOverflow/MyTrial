package ouyj.hyena.com.mytrial;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MyLayout myLayout;
    private MyButton myButton;
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myLayout = findViewById(R.id.myLayout);
        myButton = findViewById(R.id.myButton);

        int processId=android.os.Process.myPid();
        String processName = getProcessName(getApplicationContext(),processId);
        Log.d(TAG, String.format("进程ID：%d 进程名称：%s",processId,processName));
    }
    public String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runApps = am.getRunningAppProcesses();
        if (runApps == null) return null;
        for (ActivityManager.RunningAppProcessInfo info : runApps) {
            if (info.pid == pid)
                return info.processName;
        }
        return null;
    }




}
