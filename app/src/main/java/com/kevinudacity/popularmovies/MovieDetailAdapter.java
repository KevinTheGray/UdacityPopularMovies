package com.kevinudacity.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;

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
  private Context mContext;

  public MovieDetailAdapter(Context c, MovieModel movieModel) {
    mContext = c;
    mMovieModel = movieModel;
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

  public void setmMovieModel(MovieModel mMovieModel) {
    this.mMovieModel = mMovieModel;
    this.notifyDataSetChanged();
  }

  @Override
  public Object getItem(int position) {
    return null;
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    if (getItemViewType(position) == VIEW_TYPE_DETAIL) {
      MainDetailViewHolder viewHolder;
      if (convertView == null) {
        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
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
      try {
        URL imageUrl = mMovieModel.getFullPosterPath(mContext);
        Picasso.with(mContext).load(imageUrl.toString()).into(viewHolder.posterImageView);
      } catch (MalformedURLException e) {
        Log.e(LOG_TAG, "Could not form url for image");
      }
    } else if (getItemViewType(position) == VIEW_TYPE_VIDEO) {
      final TrailerViewHolder viewHolder;
      if (convertView == null) {
        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
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
        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
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

    public MainDetailViewHolder(View view) {
      movieNameTextView = (TextView) view.findViewById(R.id.textview_movie_title);
      releaseDateTextView = (TextView) view.findViewById(R.id.textview_release_date);
      userRatingTextView = (TextView) view.findViewById(R.id.textview_user_rating);
      synopsisTextView = (TextView) view.findViewById(R.id.textview_plot_synopsis);
      posterImageView = (ImageView) view.findViewById(R.id.image_view_movie_poster);
    }
  }
}
