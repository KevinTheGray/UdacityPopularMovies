package com.kevinudacity.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by KG on 4/23/16.
 */
public class MovieModel implements Parcelable {
  private String originalTitle;
  private String releaseDate;
  private String overview;
  private double voteAverage;
  private String posterPath;
  private String id;
  private ArrayList<MovieReviewModel> reviews = new ArrayList<MovieReviewModel>();
  private ArrayList<MovieTrailerModel> trailers = new ArrayList<MovieTrailerModel>();

  public MovieModel(String originalTitle, String releaseDate, String overview, float voteAverage,
                    String posterPath, String id) {
    this.originalTitle = originalTitle;
    this.releaseDate = releaseDate;
    this.overview = overview;
    this.voteAverage = voteAverage;
    this.posterPath = posterPath;
    this.id = id;
  }

  public MovieModel(Context context, JSONObject movieJSONObject) throws JSONException {
    this.originalTitle = movieJSONObject.getString(context.getString(R.string.movie_model_original_title));
    this.releaseDate = movieJSONObject.getString(context.getString(R.string.movie_model_release_date));
    this.overview = movieJSONObject.getString(context.getString(R.string.movie_model_overview));
    this.voteAverage = movieJSONObject.getDouble(context.getString(R.string.movie_model_vote_average));
    this.posterPath = movieJSONObject.getString(context.getString(R.string.movie_model_poster_path));
    this.id = movieJSONObject.getString(context.getString(R.string.movie_model_id));
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

  public String getId() {
    return id;
  }

  public ArrayList<MovieReviewModel> getReviews() {
    return reviews;
  }

  public ArrayList<MovieTrailerModel> getTrailers() {
    return trailers;
  }

  public void setTrailers(ArrayList<MovieTrailerModel> trailers) {
    this.trailers = trailers;
  }

  public void setReviews(ArrayList<MovieReviewModel> reviews) {
    this.reviews = reviews;
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
    dest.writeString(id);
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
    id = in.readString();
  }
}
