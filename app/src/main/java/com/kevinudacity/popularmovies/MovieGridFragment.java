package com.kevinudacity.popularmovies;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieGridFragment extends Fragment {


  public MovieGridFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.fragment_movie_grid, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if (id == R.id.menu_sort_by_most_popular) {
      return true;
    }
    if (id == R.id.menu_sort_by_top_rated) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    setHasOptionsMenu(true);

    View rootView = inflater.inflate(R.layout.fragment_movie_grid, container, false);
    GridView gridview = (GridView) rootView.findViewById(R.id.movie_grid_fragment_gridview);
    gridview.setAdapter(new MovieGridAdapter(getActivity()));
    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
      }
    });

    // Inflate the layout for this fragment
    return rootView;
  }

}
