package com.kevinudacity.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by KG on 4/23/16.
 */
public class MovieGridAdapter extends BaseAdapter {

  private Context context;

  public MovieGridAdapter(Context c) {
    context = c;
  }


  @Override
  public int getCount() {
    return 3;
  }

  @Override
  public long getItemId(int position) {
    return 0;
  }

  @Override
  public Object getItem(int position) {
    return null;
  }


  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View view = convertView;
    if (view == null) {
      LayoutInflater inflater = ((Activity)context).getLayoutInflater();
      view = inflater.inflate(R.layout.movie_grid_item, parent, false);
    }
    return view;
  }
}
