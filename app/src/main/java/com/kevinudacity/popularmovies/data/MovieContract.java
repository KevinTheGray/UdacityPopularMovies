package com.kevinudacity.popularmovies.data;

/**
 * Created by KG on 6/18/16.
 */

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the movie database.
 */
public class MovieContract {

  // The "Content authority" is a name for the entire content provider, similar to the
  // relationship between a domain name and its website.  A convenient string to use for the
  // content authority is the package name for the app, which is guaranteed to be unique on the
  // device.
  public static final String CONTENT_AUTHORITY = "com.kevinudacity.popularmovies";

  // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
  // the content provider.
  public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

  // Possible paths (appended to base content URI for possible URI's)
  public static final String PATH_FAVORITE_MOVIES = "favorite_movies";
  public static final String PATH_REVIEWS = "reviews";
  public static final String PATH_TRAILERS = "trailers";

  /* Inner class that defines the table contents of the favorite movie table */
  public static final class FavoriteMovieEntry implements BaseColumns {
    public static final Uri CONTENT_URI =
      BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE_MOVIES).build();

    public static final String CONTENT_TYPE =
      ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE_MOVIES;
    public static final String CONTENT_ITEM_TYPE =
      ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE_MOVIES;

    // Table name
    public static final String TABLE_NAME = "favorite_movies";

    // Title of the movie, stored as a String
    public static final String COLUMN_ORIGINAL_TITLE = "original_title";

    // Release date of the movie, stored as a String
    public static final String COLUMN_RELEASE_DATE = "release_date";

    // Overview/description of the movie, stored as a String
    public static final String COLUMN_OVERVIEW = "overview";

    // Average vote of the movie, stored as a double
    public static final String COLUMN_VOTE_AVERAGE = "vote_average";

    // Poster for the movie, stored as a blob
    public static final String COLUMN_POSTER = "poster";

    // Id for the move, stored as a String
    public static final String COLUMN_MOVIE_ID = "movie_id";

    public static Uri buildFavoriteMovieUri(long id) {
      return ContentUris.withAppendedId(CONTENT_URI, id);
    }
  }

  /* Inner class that defines the table contents of the reviews table */
  public static final class ReviewEntry implements BaseColumns {
    public static final Uri CONTENT_URI =
      BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();

    public static final String CONTENT_TYPE =
      ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;
    public static final String CONTENT_ITEM_TYPE =
      ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;

    // Table name
    public static final String TABLE_NAME = "reviews";

    // Author of the review, stored as a String
    public static final String COLUMN_AUTHOR = "author";

    // Content of the review, stored as a String
    public static final String COLUMN_CONTENT = "content";

    // Id of the review, stored as a String
    public static final String COLUMN_REVIEW_ID = "review_id";

    // Reference to the movie
    public static final String COLUMN_MOVIE_KEY = "movie_id";


    public static Uri buildReviewUri(long id) {
      return ContentUris.withAppendedId(CONTENT_URI, id);
    }
  }
  /* Inner class that defines the table contents of the trailers table */
  public static final class TrailerEntry implements BaseColumns {
    public static final Uri CONTENT_URI =
      BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILERS).build();

    public static final String CONTENT_TYPE =
      ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILERS;
    public static final String CONTENT_ITEM_TYPE =
      ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILERS;

    // Table name
    public static final String TABLE_NAME = "trailers";

    // Name of the trailer, stored as a String
    public static final String COLUMN_NAME = "name";

    // Key for the trailer, stored as a String
    public static final String COLUMN_KEY = "key";

    // Id of the review, stored as a String
    public static final String COLUMN_TRAILER_ID = "trailer_id";

    // Reference to the movie
    public static final String COLUMN_MOVIE_KEY = "movie_id";


    public static Uri buildTrailerUri(long id) {
      return ContentUris.withAppendedId(CONTENT_URI, id);
    }
  }
}
