package com.kevinudacity.popularmovies;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
  public void onStart() {
    super.onStart();
    fetchMovies("most_popular");
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
        Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
        startActivity(intent);
      }
    });

    // Inflate the layout for this fragment
    return rootView;
  }

  // Helpers
  private void fetchMovies(String sortValue) {
    FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
    fetchMoviesTask.execute(sortValue);
  }

  public class FetchMoviesTask extends AsyncTask<String, Void, String[]> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    @Override
    protected String[] doInBackground(String... params) {
      // These two need to be declared outside the try/catch
      // so that they can be closed in the finally block.
      HttpURLConnection urlConnection = null;
      BufferedReader reader = null;

      // Will contain the raw JSON response as a string.
      String moviesJsonStr = null;

      try {
        Uri builtUri = Uri.parse(getString(R.string.moviedb_path_base)).buildUpon()
          .appendPath(getString(R.string.moviedb_path_movie_most_popular))
          .appendQueryParameter(getString(R.string.moviedb_query_param_api_key), "NOT_REAL")
          .build();

        URL url = new URL(builtUri.toString());
        Log.v(LOG_TAG, "" + url);

        // Create the request to OpenWeatherMap, and open the connection
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();

        // Read the input stream into a String
        InputStream inputStream = urlConnection.getInputStream();
        StringBuffer buffer = new StringBuffer();
        if (inputStream == null) {
          // Nothing to do.
          return null;
        }
        reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
          // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
          // But it does make debugging a *lot* easier if you print out the completed
          // buffer for debugging.
          buffer.append(line + "\n");
          Log.v(LOG_TAG, "" + moviesJsonStr);
        }

        if (buffer.length() == 0) {
          // Stream was empty.  No point in parsing.
          return null;
        }
        moviesJsonStr = buffer.toString();

      } catch (IOException e) {
        Log.e(LOG_TAG, "Error ", e);
        return null;
      } finally {
        if (urlConnection != null) {
          urlConnection.disconnect();
        }
        if (reader != null) {
          try {
            reader.close();
          } catch (final IOException e) {
            Log.e(LOG_TAG, "Error closing stream", e);
          }
        }
      }
      return null;
    }

    @Override
    protected void onPostExecute(String[] result) {
    }
  }
}
