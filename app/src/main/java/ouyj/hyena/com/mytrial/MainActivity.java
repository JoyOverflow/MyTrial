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

        //绑定远程服务（使用连接对象）
        Intent intent = new Intent(this, BookManageService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }





    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {

            //得到服务端的AIDL接口对象（通过返回的Binder转换得到）
            IBookManage bookManager = IBookManage.Stub.asInterface(service);
            mRemoteBookManager = bookManager;

            try {
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
            //Log.d(TAG, "断开连接对象" + Thread.currentThread().getName());
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
     * 解除远程服务的绑定
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
}
