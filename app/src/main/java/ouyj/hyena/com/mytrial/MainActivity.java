package ouyj.hyena.com.mytrial;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import ouyj.hyena.com.mytrial.model.Book;
import ouyj.hyena.com.mytrial.model.IBookManage;

public class MainActivity extends AppCompatActivity {


    public static final String TAG = "MainActivity";



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
            IBookManage bookManager = IBookManage.Stub.asInterface(service);
            try {
                List<Book> list = bookManager.getBookList();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        public void onServiceDisconnected(ComponentName className) {
        }
    };
    @Override
    protected void onDestroy() {
        //解绑远程服务
        unbindService(mConnection);
        super.onDestroy();
    }




}
