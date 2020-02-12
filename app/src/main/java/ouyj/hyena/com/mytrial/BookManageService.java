package ouyj.hyena.com.mytrial;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import ouyj.hyena.com.mytrial.model.Book;
import ouyj.hyena.com.mytrial.model.IBookManage;
import ouyj.hyena.com.mytrial.model.INewBookListener;

public class BookManageService extends Service {

    private static final String TAG = "BookManageService";
    private AtomicBoolean mIsServiceDestoryed = new AtomicBoolean(false);
    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();

    //private CopyOnWriteArrayList<INewBookListener> mListenerList = new CopyOnWriteArrayList<>();
    //完成跨进程的解注册功能
    private RemoteCallbackList<INewBookListener> mListenerList = new RemoteCallbackList<>();

    private Binder mBinder = new IBookManage.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            //返回服务端的数据
            return mBookList;
        }
        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }
        @Override
        public void registerListener(INewBookListener listener)
                throws RemoteException
        {
            //if(!mListenerList.contains(listener))
            //    mListenerList.add(listener);
            mListenerList.register(listener);
        }
        @Override
        public void unregisterListener(INewBookListener listener)
                throws RemoteException
        {
            //if(mListenerList.contains(listener))
            //    mListenerList.remove(listener);

            boolean success = mListenerList.unregister(listener);
            if (success)
                Log.d(TAG, "解除注册成功！");
            else
                Log.d(TAG, "解除注册失败！");

            final int n = mListenerList.beginBroadcast();
            mListenerList.finishBroadcast();
            Log.d(TAG, "已注册的客户端数：" + n);
        }
    };

    public BookManageService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();

        //初始化数据
        mBookList.add(new Book(1, "Android"));
        mBookList.add(new Book(2, "Ios"));
        mBookList.add(new Book(3, "JavaEE"));
        mBookList.add(new Book(4, "Python"));
        mBookList.add(new Book(5, "Qt"));

        //启动一个线程
        new Thread(new ServiceWorker()).start();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    /**
     * 自定义线程
     */
    private class ServiceWorker implements Runnable {
        @Override
        public void run() {
            while (!mIsServiceDestoryed.get()) {
                try {
                    //休眠5秒
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //创建Book新对象
                int bookId = mBookList.size() + 1;
                Book newBook = new Book(bookId, "new book#" + bookId);
                try {
                    //每5秒通知一次客户端（发现新书）
                    newBookArrived(newBook);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void newBookArrived(Book book) throws RemoteException {
        //加入此新书
        mBookList.add(book);

        /*
        //遍历所有需通知到的客户端
        for (int i = 0; i < mListenerList.size(); i++) {
            //调用客户端相应方法
            INewBookListener tmp=mListenerList.get(i);
            tmp.newBookArrived(book);
        }
        */

        //遍历所有需通知到的客户端
        final int N = mListenerList.beginBroadcast();
        for (int i = 0; i < N; i++) {
            INewBookListener l = mListenerList.getBroadcastItem(i);
            if (l != null) {
                try {
                    l.newBookArrived(book);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        mListenerList.finishBroadcast();
    }

}
