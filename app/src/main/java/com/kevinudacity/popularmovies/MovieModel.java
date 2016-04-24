package com.kevinudacity.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by KG on 4/23/16.
 */
public class MovieModel implements Parcelable {
  private String originalTitle;
  private String releaseDate;
  private String overview;
  private double voteAverage;
  private String posterPath;

  public MovieModel(String originalTitle, String releaseDate, String overview, float voteAverage,
                    String posterPath) {
    this.originalTitle = originalTitle;
    this.releaseDate = releaseDate;
    this.overview = overview;
    this.voteAverage = voteAverage;
    this.posterPath = posterPath;
  }

  public MovieModel(Context context, JSONObject object) throws JSONException {
    this.originalTitle = object.getString(context.getString(R.string.movie_model_original_title));
    this.releaseDate = object.getString(context.getString(R.string.movie_model_release_date));
    this.overview = object.getString(context.getString(R.string.movie_model_overview));
    this.voteAverage = object.getDouble(context.getString(R.string.movie_model_vote_average));
    this.posterPath = object.getString(context.getString(R.string.movie_model_poster_path));
  }

  public URL getFullPosterPath(Context context) throws MalformedURLException {
    Uri builtUri = Uri.parse(context.getString(R.string.moviedb_path_images_base)).buildUpon()
      .appendPath(context.getString(R.string.moviedb_path_images_w185))
      // TODO: - How to deal with this properly?
      .appendPath(posterPath.replace("/",""))
      .build();

    URL url = new URL(builtUri.toString());
    return url;
  }

  public String getOriginalTitle() {
    return originalTitle;
  }

  public String getReleaseDate() {
    return releaseDate;
  }

  public String getOverview() {
    return overview;
  }

  public double getVoteAverage() {
    return voteAverage;
  }

  public String getPosterPath() {
    return posterPath;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(originalTitle);
    dest.writeString(releaseDate);
    dest.writeString(overview);
    dest.writeDouble(voteAverage);
    dest.writeString(posterPath);
  }

  public static final Parcelable.Creator<MovieModel> CREATOR
    = new Parcelable.Creator<MovieModel>() {
    public MovieModel createFromParcel(Parcel in) {
      return new MovieModel(in);
    }

    public MovieModel[] newArray(int size) {
      return new MovieModel[size];
    }
  };

  private MovieModel(Parcel in) {
    originalTitle = in.readString();
    releaseDate = in.readString();
    overview = in.readString();
    voteAverage = in.readDouble();
    posterPath = in.readString();
  }
}
