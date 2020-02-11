package ouyj.hyena.com.mytrial;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import ouyj.hyena.com.mytrial.utils.Constants;

public class MainActivity extends AppCompatActivity {


    public static final String TAG = "MainActivity";


    private Messenger serverMessenger;

    //创建一个Messenger处理服务端返回信息（封装Handler）
    private Messenger clientMessenger = new Messenger(new MsgHandler());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //绑定远程服务（使用连接对象）
        Intent intent = new Intent(this,MessageService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }
    @Override
    protected void onDestroy() {
        //解绑远程服务
        unbindService(connection);
        super.onDestroy();
    }


    /**
     *处理服务端的返回
     */
    private static class MsgHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MSG_SERVER:
                    //显示来自服务端的消息
                    String message=msg.getData().getString("server");
                    Log.i(TAG, "服务端信息：" + message);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    //创建连接对象
    private ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {

            Bundle data = new Bundle();
            data.putString("client", "你好呀.");

            //获取从服务端返回的IBinder对象（构建服务端Messenger）
            serverMessenger = new Messenger(service);

            //向服务端发送消息（）
            Message msg = Message.obtain(null, Constants.MSG_CLIENT);
            msg.setData(data);
            //设置处理服务端返回数据的客户端Messenger
            msg.replyTo = clientMessenger;
            try {
                serverMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        public void onServiceDisconnected(ComponentName className) { }
    };









}
