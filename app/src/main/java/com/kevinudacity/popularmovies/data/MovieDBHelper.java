package com.kevinudacity.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by KG on 6/18/16.
 */
public class MovieDBHelper extends SQLiteOpenHelper {
  // If you change the database schema, you must increment the database version.
  private static final int DATABASE_VERSION = 1;

  static final String DATABASE_NAME = "favorite_movies.db";

  public MovieDBHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onOpen(SQLiteDatabase db) {
    super.onOpen(db);
    if (!db.isReadOnly()) {
      // Enable foreign key constraints
      db.execSQL("PRAGMA foreign_keys=ON;");
    }
  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    // Create a table to hold trailers
    final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE "
      + MovieContract.TrailerEntry.TABLE_NAME + " (" +
      MovieContract.TrailerEntry._ID + " INTEGER PRIMARY KEY," +
      MovieContract.TrailerEntry.COLUMN_NAME + " TEXT NOT NULL, " +
      MovieContract.TrailerEntry.COLUMN_KEY + " TEXT NOT NULL, " +
      MovieContract.TrailerEntry.COLUMN_TRAILER_ID + " TEXT UNIQUE NOT NULL, " +
      MovieContract.TrailerEntry.COLUMN_MOVIE_KEY + " TEXT NOT NULL, " +
      // Set up the location column as a foreign key to location table.
      " FOREIGN KEY (" + MovieContract.TrailerEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
      MovieContract.FavoriteMovieEntry.TABLE_NAME +
      " (" + MovieContract.FavoriteMovieEntry._ID + ") ON DELETE CASCADE " +
      " );";

    // Create a table to hold reviews
    final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE "
      + MovieContract.ReviewEntry.TABLE_NAME + " (" +
      MovieContract.ReviewEntry._ID + " INTEGER PRIMARY KEY," +
      MovieContract.ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
      MovieContract.ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
      MovieContract.ReviewEntry.COLUMN_REVIEW_ID + " TEXT UNIQUE NOT NULL, " +
      MovieContract.ReviewEntry.COLUMN_MOVIE_KEY + " TEXT NOT NULL, " +
      // Set up the location column as a foreign key to location table.
      " FOREIGN KEY (" + MovieContract.ReviewEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
      MovieContract.FavoriteMovieEntry.TABLE_NAME +
      " (" + MovieContract.FavoriteMovieEntry._ID + ") ON DELETE CASCADE " +
      " );";

    final String SQL_CREATE_FAVORITE_MOVIE_TABLE =
      "CREATE TABLE " + MovieContract.FavoriteMovieEntry.TABLE_NAME + " (" +
        MovieContract.FavoriteMovieEntry._ID + " INTEGER PRIMARY KEY," +
        MovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + " TEXT UNIQUE NOT NULL, " +
        MovieContract.FavoriteMovieEntry.COLUMN_ORIGINAL_TITLE+ " TEXT NOT NULL, " +
        MovieContract.FavoriteMovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
        MovieContract.FavoriteMovieEntry.COLUMN_POSTER + " BLOB NOT NULL, " +
        MovieContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
        MovieContract.FavoriteMovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL " +
        " );";

    sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);
    sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
    sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_MOVIE_TABLE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    // This database is only a cache for online data, so its upgrade policy is
    // to simply to discard the data and start over
    // Note that this only fires if you change the version number for your database.
    // It does NOT depend on the version number for your application.
    // If you want to update the schema without wiping data, commenting out the next 2 lines
    // should be your top priority before modifying this method.
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavoriteMovieEntry.TABLE_NAME);
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewEntry.TABLE_NAME);
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.TrailerEntry.TABLE_NAME);
    onCreate(sqLiteDatabase);
  }
}
