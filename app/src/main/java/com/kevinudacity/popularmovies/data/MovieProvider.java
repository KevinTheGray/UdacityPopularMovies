package com.kevinudacity.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by KG on 6/18/16.
 */
public class MovieProvider extends ContentProvider {

  // The URI Matcher used by this content provider.
  private static final UriMatcher sUriMatcher = buildUriMatcher();
  private MovieDBHelper mOpenHelper;

  static final int FAVORITE_MOVIES = 100;
  static final int TRAILERS = 200;
  static final int REVIEWS = 300;

  @Override
  public boolean onCreate() {
    mOpenHelper = new MovieDBHelper(getContext());
    return true;
  }

  @Nullable
  @Override
  public String getType(Uri uri) {
    return null;
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    final int match = sUriMatcher.match(uri);
    int rowsUpdated;

    if (match == FAVORITE_MOVIES) {
      rowsUpdated = db.update(MovieContract.FavoriteMovieEntry.TABLE_NAME, values, selection,
        selectionArgs);
    } else if (match == REVIEWS) {
      rowsUpdated = db.update(MovieContract.ReviewEntry.TABLE_NAME, values, selection,
        selectionArgs);
    } else if (match == TRAILERS) {
      rowsUpdated = db.update(MovieContract.TrailerEntry.TABLE_NAME, values, selection,
        selectionArgs);
    } else {
      throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
    return rowsUpdated;
  }

  @Nullable
  @Override
  public Uri insert(Uri uri, ContentValues values) {
    final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    final int match = sUriMatcher.match(uri);
    Uri returnUri = null;

    if (match == FAVORITE_MOVIES) {
      long _id = db.insert(MovieContract.FavoriteMovieEntry.TABLE_NAME, null, values);
      if ( _id > 0 )
        returnUri = MovieContract.FavoriteMovieEntry.buildFavoriteMovieUri(_id);
      else
        throw new android.database.SQLException("Failed to insert row into " + uri);
    } else if (match == REVIEWS) {
      long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
      if ( _id > 0 )
        returnUri = MovieContract.ReviewEntry.buildReviewUri(_id);
      else
        throw new android.database.SQLException("Failed to insert row into " + uri);
    } else if (match == TRAILERS) {
      long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, values);
      if ( _id > 0 )
        returnUri = MovieContract.TrailerEntry.buildTrailerUri(_id);
      else
        throw new android.database.SQLException("Failed to insert row into " + uri);
    } else {
      throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
    getContext().getContentResolver().notifyChange(uri, null);
    return returnUri;
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
    final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    final int match = sUriMatcher.match(uri);
    int rowsDeleted = 0;

    if ( null == selection ) selection = "1";
    // We only need to delete movies, if a movie is deleted, we should also delete reviews/trailers
    if (match == FAVORITE_MOVIES) {
      rowsDeleted = db.delete(
        MovieContract.FavoriteMovieEntry.TABLE_NAME, selection, selectionArgs);
    } else {
      throw new UnsupportedOperationException("Unknown uri: " + uri);
    }

    if (rowsDeleted != 0) {
      getContext().getContentResolver().notifyChange(uri, null);
    }

    return rowsDeleted;
  }

  @Nullable
  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
    final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    final int match = sUriMatcher.match(uri);
    Cursor retCursor;

    if (match == FAVORITE_MOVIES) {
      retCursor = mOpenHelper.getReadableDatabase().query(
        MovieContract.FavoriteMovieEntry.TABLE_NAME, projection, selection, selectionArgs, null,
        null, sortOrder);
    } else if (match == REVIEWS) {
      retCursor = mOpenHelper.getReadableDatabase().query(
        MovieContract.ReviewEntry.TABLE_NAME, projection, selection, selectionArgs, null,
        null, sortOrder);
    } else if (match == TRAILERS) {
      retCursor = mOpenHelper.getReadableDatabase().query(
        MovieContract.TrailerEntry.TABLE_NAME, projection, selection, selectionArgs, null,
        null, sortOrder);
    } else {
      throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
    return retCursor;
  }

  static UriMatcher buildUriMatcher() {
    final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    final String authority = MovieContract.CONTENT_AUTHORITY;

    // For each type of URI you want to add, create a corresponding code.
    matcher.addURI(authority, MovieContract.PATH_FAVORITE_MOVIES, FAVORITE_MOVIES);
    matcher.addURI(authority, MovieContract.PATH_REVIEWS, REVIEWS);
    matcher.addURI(authority, MovieContract.PATH_TRAILERS, TRAILERS);

    return matcher;
  }
}
