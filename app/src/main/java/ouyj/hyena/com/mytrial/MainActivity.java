package ouyj.hyena.com.mytrial;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import ouyj.hyena.com.mytrial.model.Book;
import ouyj.hyena.com.mytrial.model.IBookManage;
import ouyj.hyena.com.mytrial.model.INewBookListener;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private static final int MESSAGE_NEW_BOOK_ARRIVED = 1;

    //服务端的AIDL接口对象
    private IBookManage mRemoteBookManager;

    //用于切换到UI主线程
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_NEW_BOOK_ARRIVED:
                    Log.d(TAG, "客户端接收到新书：" + msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //绑定远程服务（使用连接对象）服务端会触发onBind
        Intent intent = new Intent(this, BookManageService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }


    /**
     * 设置Binder对象的DeathRecipient监听
     * 服务端Binder死亡后（会调用onServiceDisconnected），客户端需重新连接服务
     */
    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.d(TAG, "服务端Binder已被死亡了！:" + Thread.currentThread().getName());

            if (mRemoteBookManager == null)
                return;

            //为Binder对象解除死亡代理
            mRemoteBookManager.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mRemoteBookManager = null;
            //TODO:这里重新绑定远程Service
            //bindService(new Intent("demo.action.aidl.IAidlCall")
            //        .setPackage("com.example.severdemo"), mConnection, BIND_AUTO_CREATE);
        }
    };


    private ServiceConnection mConnection = new ServiceConnection() {

        //服务端绑定成功后，客户端连接成功（触发）
        public void onServiceConnected(ComponentName className, IBinder service) {

            //得到服务端的AIDL接口对象（通过返回的Binder转换得到）
            IBookManage bookManager = IBookManage.Stub.asInterface(service);
            mRemoteBookManager = bookManager;
            Log.d(TAG, "连接已成功：" + Thread.currentThread().getName());

            try {
                //为Binder对象设置死亡代理
                mRemoteBookManager.asBinder().linkToDeath(mDeathRecipient, 0);

                //调用服务端方法（服务端触发onTransact）
                List<Book> list = bookManager.getBookList();
                Log.i(TAG, "第一次数据：" + list.toString());
                Book newBook = new Book(6, "Flutter");
                bookManager.addBook(newBook);
                Log.i(TAG, "添加记录：" + newBook);
                List<Book> newList = bookManager.getBookList();
                Log.i(TAG, "第二次数据：" + newList.toString());

                //注册（能接收新书信息）
                bookManager.registerListener(mOnNewBookArrivedListener);

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        public void onServiceDisconnected(ComponentName className) {
            mRemoteBookManager = null;
            Log.d(TAG, "连接被断开：" + Thread.currentThread().getName());
        }
    };
    /**
     * 创建Binder对象来实现“通知接口”（服务端会每隔5秒调用一次）
     */
    private INewBookListener mOnNewBookArrivedListener = new INewBookListener.Stub() {
        @Override
        public void newBookArrived(Book newBook) throws RemoteException {
            mHandler.obtainMessage(
                    MESSAGE_NEW_BOOK_ARRIVED,
                    newBook
            ).sendToTarget();
        }
    };

    /**
     * 活动回退时触发（解除远程服务的绑定）
     */
    @Override
    protected void onDestroy() {

        //判断服务端AIDL接口对象是否为null
        if (mRemoteBookManager != null && mRemoteBookManager.asBinder().isBinderAlive())
        {
            try {
                Log.i(TAG, "unregister listener:" + mOnNewBookArrivedListener);
                //在服务端解除注册
                mRemoteBookManager.unregisterListener(mOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        //解绑远程服务
        unbindService(mConnection);
        super.onDestroy();
    }


    /**
     * 按钮请求服务端方法（调用放在线程中，不会引发ANR）
     * @param view
     */
    public void onButton1Click(View view) {
        Toast.makeText(this, "点击按钮！", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mRemoteBookManager != null) {
                    try {
                        List<Book> newList = mRemoteBookManager.getBookList();
                        Log.i(TAG, "单击按钮取得数据：" + newList.toString());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


}
