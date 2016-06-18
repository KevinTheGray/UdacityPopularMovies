package com.kevinudacity.popularmovies;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by KG on 6/17/16.
 */
public class MovieReviewModel implements Parcelable {
  private String author;
  private String content;

  public MovieReviewModel(String author, String content) {
    this.author = author;
    this.content = content;
  }

  public MovieReviewModel(Context context, JSONObject movieReviewJSONObject) throws JSONException {
    this.author = movieReviewJSONObject.getString(context.getString(R.string.movie_review_model_author));
    this.content = movieReviewJSONObject.getString(context.getString(R.string.movie_review_model_content));
  }

  public String getAuthor() {
    return author;
  }

  public String getContent() {
    return content;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(author);
    dest.writeString(content);
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
  }
}
