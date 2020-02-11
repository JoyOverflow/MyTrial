package ouyj.hyena.com.mytrial;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.concurrent.CopyOnWriteArrayList;

import ouyj.hyena.com.mytrial.model.Book;

public class BookManageService extends Service {

    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();
    private Binder mBinder = new IBookManage.Stub() {

    };

    public BookManageService() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
