package com.kevinudacity.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
    return 1;
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public int getItemViewType(int position) {
    if (position == 0) {
      return VIEW_TYPE_DETAIL;
    }
    return VIEW_TYPE_UNKNOWN;
  }

  @Override
  public Object getItem(int position) {
    return null;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
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
      return convertView;
    }
    return null;
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
