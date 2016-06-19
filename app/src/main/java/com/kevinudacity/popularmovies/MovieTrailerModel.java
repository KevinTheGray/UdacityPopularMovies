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
public class MovieTrailerModel implements Parcelable {
  private String name;
  private String key;
  private String id;

  public MovieTrailerModel(String name, String key, String id) {
    this.name = name;
    this.key = key;
    this.id = id;
  }

  public MovieTrailerModel(Context context, JSONObject movieTrailerJSONObject) throws JSONException {
    this.name = movieTrailerJSONObject.getString("name");
    this.key = movieTrailerJSONObject.getString("key");
    this.id = movieTrailerJSONObject.getString("id");
  }

  public MovieTrailerModel(Context context, Cursor cursor) {
    this.name = cursor.getString(
      cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_NAME));
    this.key = cursor.getString(
      cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_KEY));
    this.id = cursor.getString(
      cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_TRAILER_ID));
  }

  public String getName() {
    return name;
  }

  public String getKey() {
    return key;
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
    dest.writeString(name);
    dest.writeString(key);
    dest.writeString(id);
  }

  public static final Parcelable.Creator<MovieTrailerModel> CREATOR
    = new Parcelable.Creator<MovieTrailerModel>() {
    public MovieTrailerModel createFromParcel(Parcel in) {
      return new MovieTrailerModel(in);
    }

    public MovieTrailerModel[] newArray(int size) {
      return new MovieTrailerModel[size];
    }
  };

  private MovieTrailerModel(Parcel in) {
    name = in.readString();
    key = in.readString();
    id = in.readString();
  }
}
