package ouyj.hyena.com.mytrial.model;

import ouyj.hyena.com.mytrial.model.Book;
import ouyj.hyena.com.mytrial.model.INewBookListener;

interface IBookManage {
     List<Book> getBookList();
     void addBook(in Book book);
     void registerListener(INewBookListener listener);
     void unregisterListener(INewBookListener listener);
}