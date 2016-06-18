package com.kevinudacity.popularmovies;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by KG on 6/17/16.
 */
public class MovieTrailerModel implements Parcelable {
  private String name;
  private String key;

  public MovieTrailerModel(String name, String key) {
    this.name = name;
    this.key = key;
  }

  public MovieTrailerModel(Context context, JSONObject movieTrailerJSONObject) throws JSONException {
    this.name = movieTrailerJSONObject.getString(context.getString(R.string.movie_trailer_model_name));
    this.key = movieTrailerJSONObject.getString(context.getString(R.string.movie_trailer_model_key));
  }

  public String getName() {
    return name;
  }

  public String getKey() {
    return key;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(name);
    dest.writeString(key);
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
  }
}
