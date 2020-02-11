package ouyj.hyena.com.mytrial.model;

import ouyj.hyena.com.mytrial.model.Book;
//import com.ryg.chapter_2.aidl.IOnNewBookArrivedListener;

interface IBookManage {
     List<Book> getBookList();
     void addBook(in Book book);
     //void registerListener(IOnNewBookArrivedListener listener);
     //void unregisterListener(IOnNewBookArrivedListener listener);
}