package com.movies.bruno.udacity.popularmovies.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by BRUNO on 21/12/2015.
 */
public class Review implements Parcelable{

    private int idMovie;
    private String author;
    private String content;

    //Constructor
    public Review(int idMovie, String author, String content){
        this.idMovie = idMovie;
        this.author = author;
        this.content = content;
    }

    //Parcelling
    public Review(Parcel in){
        String[] data = new String[3];

        in.readStringArray(data);
        this.idMovie = Integer.parseInt(data[0]);
        this.author = data[1];
        this.content = data[2];
    }

    public int getIdMovie(){
        return this.idMovie;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                String.valueOf(this.idMovie),
                this.author,
                this.content
        });
    }

    public static final Parcelable.Creator CREATOR = new Creator() {
        @Override
        public Object createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Object[] newArray(int size) {
            return new Review[size];
        }
    };
}
