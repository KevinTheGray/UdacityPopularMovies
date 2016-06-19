package com.kevinudacity.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.kevinudacity.popularmovies.data.MovieContract;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by KG on 6/17/16.
 */
public class MovieReviewModel implements Parcelable {
  private String author;
  private String content;
  private String id;

  public MovieReviewModel(String author, String content, String id) {
    this.author = author;
    this.content = content;
    this.id = id;
  }

  public MovieReviewModel(Context context, JSONObject movieReviewJSONObject) throws JSONException {
    this.author = movieReviewJSONObject.getString("author");
    this.content = movieReviewJSONObject.getString("content");
    this.id = movieReviewJSONObject.getString("id");
  }

  public MovieReviewModel(Context context, Cursor cursor) {
    this.author = cursor.getString(
      cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_AUTHOR));
    this.content = cursor.getString(
      cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_CONTENT));
    this.id = cursor.getString(
      cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_REVIEW_ID));
  }

  public String getAuthor() {
    return author;
  }

  public String getContent() {
    return content;
  }

  public String getId() {
    return id;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(author);
    dest.writeString(content);
    dest.writeString(id);
  }

  public static final Parcelable.Creator<MovieReviewModel> CREATOR
    = new Parcelable.Creator<MovieReviewModel>() {
    public MovieReviewModel createFromParcel(Parcel in) {
      return new MovieReviewModel(in);
    }

    public MovieReviewModel[] newArray(int size) {
      return new MovieReviewModel[size];
    }
  };

  private MovieReviewModel(Parcel in) {
    author = in.readString();
    content = in.readString();
    id = in.readString();
  }
}
