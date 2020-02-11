package ouyj.hyena.com.mytrial;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ouyj.hyena.com.mytrial.model.Book;
import ouyj.hyena.com.mytrial.model.IBookManage;

public class BookManageService extends Service {

    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();
    private Binder mBinder = new IBookManage.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }
        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }
    };

    public BookManageService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1, "Android"));
        mBookList.add(new Book(2, "Ios"));
        mBookList.add(new Book(3, "JavaEE"));
        mBookList.add(new Book(4, "Python"));
        mBookList.add(new Book(5, "Qt"));
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
