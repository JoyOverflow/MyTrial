package ouyj.hyena.com.mytrial.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {

    public int bookId;
    public String bookName;

    public Book() { }
    public Book(int bookId, String bookName) {
        this.bookId = bookId;
        this.bookName = bookName;
    }
    private Book(Parcel in) {
        bookId = in.readInt();
        bookName = in.readString();
    }


    public int describeContents() {
        return 0;
    }
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(bookId);
        out.writeString(bookName);
    }
    public static final Creator<Book> CREATOR = new Creator<Book>() {
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public String toString() {
        return String.format("[bookId:%s, bookName:%s]", bookId, bookName);
    }
}
