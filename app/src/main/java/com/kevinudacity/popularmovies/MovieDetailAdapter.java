package com.kevinudacity.popularmovies;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kevinudacity.popularmovies.data.MovieContract;

import java.io.ByteArrayOutputStream;

/**
 * Created by KG on 6/14/16.
 */
public class MovieDetailAdapter extends BaseAdapter {
  private final String LOG_TAG = "MovieDetailAdapter";
  // TODO: Use view holders
  // TODO: Add trailers
  // TODO: Add reviews
  private static final int VIEW_TYPE_COUNT = 3;
  private static final int VIEW_TYPE_DETAIL = 0;
  private static final int VIEW_TYPE_VIDEO = 1;
  private static final int VIEW_TYPE_REVIEW = 2;
  private static final int VIEW_TYPE_UNKNOWN = 3;

  private MovieModel mMovieModel;
  private boolean favorited = false;
  private Context mContext;

  public MovieDetailAdapter(Context c, MovieModel movieModel) {
    mContext = c;
    mMovieModel = movieModel;
    // Check if it is favorited
    Cursor cursor = mContext.getContentResolver().query(MovieContract.FavoriteMovieEntry.CONTENT_URI,
      new String[]{MovieContract.FavoriteMovieEntry._ID},
      MovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + " = ?",
      new String[]{mMovieModel.getId()},
      null);
    if (cursor != null) {
      if (cursor.moveToFirst()) {
        favorited = true;
      }
      cursor.close();
    }
  }

  @Override
  public int getViewTypeCount() {
    return VIEW_TYPE_COUNT;
  }

  @Override
  public int getCount() {
    return 1 + mMovieModel.getTrailers().size() + mMovieModel.getReviews().size();
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public int getItemViewType(int position) {
    if (position == 0) {
      return VIEW_TYPE_DETAIL;
    } else if (position <= mMovieModel.getTrailers().size()) {
      return VIEW_TYPE_VIDEO;
    }
    return VIEW_TYPE_REVIEW;
  }

  public void setmMovieModel(final MovieModel movieModel) {

    Handler mainHandler = new Handler(mContext.getMainLooper());

    Runnable myRunnable = new Runnable() {
      @Override
      public void run() {
        mMovieModel = movieModel;
        notifyDataSetChanged();
      } // This is your code
    };
    mainHandler.post(myRunnable);
  }

  @Override
  public Object getItem(int position) {
    return null;
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    if (getItemViewType(position) == VIEW_TYPE_DETAIL) {
      final MainDetailViewHolder viewHolder;
      if (convertView == null) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        convertView = inflater.inflate(R.layout.movie_detail_main_list_item, parent, false);
        viewHolder = new MainDetailViewHolder(convertView);
        convertView.setTag(viewHolder);
      } else {
        viewHolder = (MainDetailViewHolder) convertView.getTag();
      }
      viewHolder.movieNameTextView.setText(mMovieModel.getOriginalTitle());
      viewHolder.releaseDateTextView.setText(mMovieModel.getReleaseDate());
      viewHolder.synopsisTextView.setText(mMovieModel.getReleaseDate());
      // TODO: use that thing they use in the lessons so you don't have to manually add "/10"
      viewHolder.userRatingTextView.setText("" + mMovieModel.getVoteAverage() + "/10");
      viewHolder.synopsisTextView.setText(mMovieModel.getOverview());
      viewHolder.posterImageView.setImageBitmap(mMovieModel.getPosterBitmap());
      if (favorited) {
        viewHolder.favoritesButton.setText(mContext.getString(R.string.movie_detail_favorite_button_remove));
      } else {
        viewHolder.favoritesButton.setText(mContext.getString(R.string.movie_detail_favorite_button_save));
      }
      viewHolder.favoritesButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (favorited) {
            int deletedRowCount =
              mContext.getContentResolver().delete(MovieContract.FavoriteMovieEntry.CONTENT_URI,
              MovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + " = ?",
              new String[]{mMovieModel.getId()});
            if (deletedRowCount > 0) {
              favorited = false;
              viewHolder.favoritesButton.setText(mContext.getString(R.string.movie_detail_favorite_button_save));
            }
          } else {
            Bitmap bitmap = ((BitmapDrawable) (viewHolder.posterImageView.getDrawable())).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            byte[] imageBlob = stream.toByteArray();

            ContentValues movieValues = new ContentValues();

            // Then add the data, along with the corresponding name of the data type,
            // so the content provider knows what kind of value is being inserted.
            movieValues.put(MovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID, mMovieModel.getId());
            movieValues.put(MovieContract.FavoriteMovieEntry.COLUMN_ORIGINAL_TITLE, mMovieModel.getOriginalTitle());
            movieValues.put(MovieContract.FavoriteMovieEntry.COLUMN_OVERVIEW, mMovieModel.getOverview());
            movieValues.put(MovieContract.FavoriteMovieEntry.COLUMN_POSTER, imageBlob);
            movieValues.put(MovieContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE, mMovieModel.getReleaseDate());
            movieValues.put(MovieContract.FavoriteMovieEntry.COLUMN_VOTE_AVERAGE, mMovieModel.getVoteAverage());
            Uri insertedUri = mContext.getContentResolver().insert(
              MovieContract.FavoriteMovieEntry.CONTENT_URI,
              movieValues
            );
            if (insertedUri != null) {
              favorited = true;
              viewHolder.favoritesButton.setText(mContext.getString(R.string.movie_detail_favorite_button_remove));
              long movieId = ContentUris.parseId(insertedUri);
              for (MovieTrailerModel movieTrailerModel : mMovieModel.getTrailers()) {
                // Check if it's in there already
                Cursor cursor = mContext.getContentResolver().query(MovieContract.TrailerEntry.CONTENT_URI,
                  new String[]{MovieContract.TrailerEntry._ID},
                  MovieContract.TrailerEntry.COLUMN_TRAILER_ID + " = ?",
                  new String[]{movieTrailerModel.getId()},
                  null);
                if (cursor == null || !cursor.moveToFirst()) {
                  // insert it
                  ContentValues trailerValues = new ContentValues();

                  trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID, movieTrailerModel.getId());
                  trailerValues.put(MovieContract.TrailerEntry.COLUMN_KEY, movieTrailerModel.getKey());
                  trailerValues.put(MovieContract.TrailerEntry.COLUMN_NAME, movieTrailerModel.getName());
                  trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_KEY, movieId);
                  mContext.getContentResolver().insert(
                    MovieContract.TrailerEntry.CONTENT_URI,
                    trailerValues
                  );
                }
                if (cursor != null) {
                  cursor.close();
                }
              }

              for (MovieReviewModel movieReviewModel : mMovieModel.getReviews()) {
                // Check if it's in there already
                Cursor cursor = mContext.getContentResolver().query(MovieContract.ReviewEntry.CONTENT_URI,
                  new String[]{MovieContract.ReviewEntry._ID},
                  MovieContract.ReviewEntry.COLUMN_REVIEW_ID + " = ?",
                  new String[]{movieReviewModel.getId()},
                  null);
                if (cursor == null || !cursor.moveToFirst()) {
                  // insert it
                  ContentValues reviewValues = new ContentValues();

                  reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, movieReviewModel.getId());
                  reviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, movieReviewModel.getAuthor());
                  reviewValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, movieReviewModel.getContent());
                  reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_KEY, movieId);
                  mContext.getContentResolver().insert(
                    MovieContract.ReviewEntry.CONTENT_URI,
                    reviewValues
                  );
                }
                if (cursor != null) {
                  cursor.close();
                }
              }
            }
          }
          if (mContext instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) mContext;
            if (mainActivity != null) {
              mainActivity.onFavoritesUpdated();
            }
          }
        }
      });
    } else if (getItemViewType(position) == VIEW_TYPE_VIDEO) {
      final TrailerViewHolder viewHolder;
      if (convertView == null) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        convertView = inflater.inflate(R.layout.movie_detail_trailer_item, parent, false);
        viewHolder = new TrailerViewHolder(convertView);
        convertView.setTag(viewHolder);
      } else {
        viewHolder = (TrailerViewHolder) convertView.getTag();
      }
      if (position != 1) {
        viewHolder.trailerHeaderTextView.setVisibility(View.GONE);
      } else {
        viewHolder.trailerHeaderTextView.setVisibility(View.VISIBLE);
      }
      viewHolder.trailerNameTextView.setText(mMovieModel.getTrailers().get(position - 1).getName());
      viewHolder.trailerPlayButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          mContext.
            startActivity(new Intent(
              Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" +
              mMovieModel.getTrailers().get(position - 1).getKey())));
        }
      });
    } else {
      ReviewViewHolder viewHolder;
      if (convertView == null) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        convertView = inflater.inflate(R.layout.movie_detail_review_item, parent, false);
        viewHolder = new ReviewViewHolder(convertView);
        convertView.setTag(viewHolder);
      } else {
        viewHolder = (ReviewViewHolder) convertView.getTag();
      }
      if (position != 1 + mMovieModel.getTrailers().size()) {
        viewHolder.reviewHeaderTextView.setVisibility(View.GONE);
      } else {
        viewHolder.reviewHeaderTextView.setVisibility(View.VISIBLE);
      }
      viewHolder.reviewAuthorTextView
        .setText(mMovieModel.getReviews().get(position - mMovieModel.getTrailers().size() - 1).getAuthor());
      viewHolder.reviewTextView
        .setText(mMovieModel.getReviews().get(position - mMovieModel.getTrailers().size() - 1).getContent());
    }
    return convertView;
  }

  public static class ReviewViewHolder {
    public final TextView reviewHeaderTextView;
    public final TextView reviewAuthorTextView;
    public final TextView reviewTextView;

    public ReviewViewHolder(View view) {
      reviewHeaderTextView = (TextView) view.findViewById(R.id.textview_review_header);
      reviewAuthorTextView = (TextView) view.findViewById(R.id.textview_review_author);
      reviewTextView = (TextView) view.findViewById(R.id.textview_review);
    }
  }

  public static class TrailerViewHolder {
    public final TextView trailerHeaderTextView;
    public final TextView trailerNameTextView;
    public final Button trailerPlayButton;

    public TrailerViewHolder(View view) {
      trailerHeaderTextView = (TextView) view.findViewById(R.id.textview_trailer_header);
      trailerNameTextView = (TextView) view.findViewById(R.id.textview_trailer_name);
      trailerPlayButton = (Button) view.findViewById(R.id.button_trailer_play);
    }
  }

  public static class MainDetailViewHolder {
    public final TextView movieNameTextView;
    public final TextView releaseDateTextView;
    public final TextView userRatingTextView;
    public final TextView synopsisTextView;
    public final ImageView posterImageView;
    public final Button favoritesButton;

    public MainDetailViewHolder(View view) {
      movieNameTextView = (TextView) view.findViewById(R.id.textview_movie_title);
      releaseDateTextView = (TextView) view.findViewById(R.id.textview_release_date);
      userRatingTextView = (TextView) view.findViewById(R.id.textview_user_rating);
      synopsisTextView = (TextView) view.findViewById(R.id.textview_plot_synopsis);
      posterImageView = (ImageView) view.findViewById(R.id.image_view_movie_poster);
      favoritesButton = (Button) view.findViewById(R.id.favorites_button);
    }
  }
}
