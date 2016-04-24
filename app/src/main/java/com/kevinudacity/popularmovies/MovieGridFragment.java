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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieGridFragment extends Fragment {

  private MovieGridAdapter movieGridAdapter;

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
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if (id == R.id.menu_sort_by_most_popular) {
      fetchMovies(getString(R.string.moviedb_path_movie_most_popular));
      return true;
    }
    if (id == R.id.menu_sort_by_top_rated) {
      fetchMovies(getString(R.string.moviedb_path_movie_top_rated));
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    setHasOptionsMenu(true);
    movieGridAdapter = new MovieGridAdapter(getActivity());

    View rootView = inflater.inflate(R.layout.fragment_movie_grid, container, false);
    GridView gridview = (GridView) rootView.findViewById(R.id.movie_grid_fragment_gridview);
    gridview.setAdapter(movieGridAdapter);
    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
        MovieModel movieModel = (MovieModel) movieGridAdapter.getItem(position);
        String movieModelBundleKey = getString(R.string.bundle_key_movie_model);
        intent.putExtra(movieModelBundleKey, movieModel);
        startActivity(intent);
      }
    });

    fetchMovies(getString(R.string.moviedb_path_movie_most_popular));
    // Inflate the layout for this fragment
    return rootView;
  }

  // Helpers
  private void fetchMovies(String sortValue) {
    FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
    fetchMoviesTask.execute(sortValue);
  }

  public class FetchMoviesTask extends AsyncTask<String, Void, MovieModel[]> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    private MovieModel[] getMovieDataFromJson(String stringMoviesJson) throws JSONException {
      JSONObject moviesJson = new JSONObject(stringMoviesJson);
      JSONArray moviesJsonArray =
        moviesJson.getJSONArray(getString(R.string.generic_json_key_results));

      MovieModel[] returnedMovieModels = new MovieModel[moviesJsonArray.length()];
      for (int i = 0; i < moviesJsonArray.length(); i++) {
        JSONObject object = moviesJsonArray.getJSONObject(i);
        MovieModel model = new MovieModel(getActivity(), object);
        returnedMovieModels[i] = model;
      }
      return returnedMovieModels;
    }

    @Override
    protected MovieModel[] doInBackground(String... params) {
      // These two need to be declared outside the try/catch
      // so that they can be closed in the finally block.
      HttpURLConnection urlConnection = null;
      BufferedReader reader = null;

      // Will contain the raw JSON response as a string.
      String moviesJsonStr = null;
      String filter = getString(R.string.moviedb_path_movie_most_popular);
      if (params.length >= 1) {
        filter = params[0];
      }

      try {
        Uri builtUri = Uri.parse(getString(R.string.moviedb_path_base)).buildUpon()
          .appendPath(filter)
          .appendQueryParameter(getString(R.string.moviedb_query_param_api_key), "PUT API KEY HERE")
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
        }

        if (buffer.length() == 0) {
          // Stream was empty.  No point in parsing.
          return null;
        }
        moviesJsonStr = buffer.toString();

        try {
          return getMovieDataFromJson(moviesJsonStr);
        } catch (JSONException e) {
          Log.e(LOG_TAG, e.getMessage(), e);
          e.printStackTrace();
        }

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
    protected void onPostExecute(MovieModel[] result) {
      if (result != null) {
        movieGridAdapter.setMovieModels(new ArrayList<MovieModel>(Arrays.asList(result)));
      }
    }
  }
}
