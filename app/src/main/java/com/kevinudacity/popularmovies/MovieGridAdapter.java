package com.kevinudacity.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by KG on 4/23/16.
 */
public class MovieGridAdapter extends BaseAdapter {

  private final String LOG_TAG = "MovieGridAdapter";
  private Context context;
  private ArrayList<MovieModel> movieModels;

  public MovieGridAdapter(Context c) {
    context = c;
    movieModels = new ArrayList<>();
  }

  public void setMovieModels(ArrayList<MovieModel> movieModels) {
    this.movieModels = movieModels;
    this.notifyDataSetChanged();
  }

  public ArrayList<MovieModel> getMovieModels() {
    return movieModels;
  }

  @Override
  public int getCount() {
    return this.movieModels.size();
  }

  @Override
  public long getItemId(int position) {
    return 0;
  }

  @Override
  public Object getItem(int position) {
    return this.movieModels.get(position);
  }


  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    View view = convertView;
    if (view == null) {
      LayoutInflater inflater = ((Activity)context).getLayoutInflater();
      view = inflater.inflate(R.layout.movie_grid_item, parent, false);
    }
    final ImageView imageView = (ImageView) view.findViewById(R.id.image_view);
    if (movieModels.get(position).getPosterBitmap() == null) {
      try {
        URL imageUrl = this.movieModels.get(position).getFullPosterPath(context);
        Log.v(LOG_TAG, imageUrl.toString());
        Picasso.with(context).load(imageUrl.toString()).into(imageView, new Callback() {
          @Override
          public void onSuccess() {
            movieModels.get(position).setPosterBitmap(
              ((BitmapDrawable) imageView.getDrawable()).getBitmap());
          }

          @Override
          public void onError() {
          }
        });
      } catch (MalformedURLException e) {
        Log.e(LOG_TAG, "Could not form url for image");
      }
    } else {
      imageView.setImageBitmap(movieModels.get(position).getPosterBitmap());
    }
    return view;
  }
}
