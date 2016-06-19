package com.kevinudacity.popularmovies;


import android.database.Cursor;
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

import com.kevinudacity.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieGridFragment extends Fragment {

  private MovieGridAdapter movieGridAdapter;
  public boolean favoritesSelected;

  public MovieGridFragment() {
    // Required empty public constructor
  }

  public interface Callback {
    /**
     * MovieGridFragment for when an item has been selected.
     */
    public void onItemSelected(MovieModel movieModel);
    //public void onItemSelectable(MovieModel movieModel);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.fragment_movie_grid, menu);
  }

  @Override
  public void onStart() {
    super.onStart();
    if (favoritesSelected) {
      fetchFavoritedMovies();
    }
  }

  public void fetchFavoritedMovies() {
    // Fetch all the favorited movies from the db
    favoritesSelected = true;
    Cursor cursor =
      getActivity().getContentResolver().query(MovieContract.FavoriteMovieEntry.CONTENT_URI,
        null,
        null,
        null,
        null);
    ArrayList<MovieModel> movieModels = new ArrayList<>();
    if (cursor != null) {
      while (cursor.moveToNext()) {
        MovieModel movieModel = new MovieModel(getActivity(), cursor);
        // Get the trailers and reviews
        String movieId = cursor.getString(cursor.getColumnIndex(MovieContract.FavoriteMovieEntry._ID));
        Cursor trailerCursor =
          getActivity().getContentResolver().query(MovieContract.TrailerEntry.CONTENT_URI,
            null,
            MovieContract.TrailerEntry.COLUMN_MOVIE_KEY + " = ?",
            new String[]{movieId},
            null);
        Cursor reviewCursor =
          getActivity().getContentResolver().query(MovieContract.ReviewEntry.CONTENT_URI,
            null,
            MovieContract.ReviewEntry.COLUMN_MOVIE_KEY + " = ?",
            new String[]{movieId},
            null);
        ArrayList<MovieTrailerModel> movieTrailerModels = new ArrayList<>();
        ArrayList<MovieReviewModel> movieReviewModels = new ArrayList<>();
        if (trailerCursor != null) {
          while (trailerCursor.moveToNext()) {
            movieTrailerModels.add(new MovieTrailerModel(getActivity(), trailerCursor));
          }
          trailerCursor.close();
        }
        if (reviewCursor != null) {
          while (reviewCursor.moveToNext()) {
            movieReviewModels.add(new MovieReviewModel(getActivity(), reviewCursor));
          }
          reviewCursor.close();
        }
        movieModel.setReviews(movieReviewModels);
        movieModel.setTrailers(movieTrailerModels);
        movieModels.add(movieModel);
      }
      cursor.close();
    }
    movieGridAdapter.setMovieModels(movieModels);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if (id == R.id.menu_sort_by_most_popular) {
      favoritesSelected = false;
      fetchMovies(getString(R.string.moviedb_path_movie_most_popular));
      return true;
    }
    if (id == R.id.menu_sort_by_top_rated) {
      favoritesSelected = false;
      fetchMovies(getString(R.string.moviedb_path_movie_top_rated));
      return true;
    }
    if (id == R.id.menu_favorites) {
      favoritesSelected = true;
      fetchFavoritedMovies();
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    ArrayList<MovieModel> movieModels = movieGridAdapter.getMovieModels();
    outState.putParcelableArrayList("movieModels", movieModels);
    super.onSaveInstanceState(outState);
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
        MovieModel movieModel = (MovieModel) movieGridAdapter.getItem(position);
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.onItemSelected(movieModel);
      }
    });

    if (savedInstanceState != null && savedInstanceState.containsKey("movieModels")) {
      ArrayList<MovieModel> movieModels = savedInstanceState.getParcelableArrayList("movieModels");
      movieGridAdapter.setMovieModels(movieModels);
    } else {
      fetchMovies(getString(R.string.moviedb_path_movie_most_popular));
    }
    return rootView;
  }

  // Helpers
  private void fetchMovies(String sortValue) {
    FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
    fetchMoviesTask.execute(sortValue);
  }

  public class FetchMoviesTask extends AsyncTask<String, Void, MovieModel[]> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    private String baseFetchRequest(URL url) {
      // These two need to be declared outside the try/catch
      // so that they can be closed in the finally block.
      HttpURLConnection urlConnection = null;
      BufferedReader reader = null;

      try {
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
        return buffer.toString();
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
    }

    private String fetchBaseMovieData(String filter) throws MalformedURLException {
      Uri builtUri = Uri.parse(getString(R.string.moviedb_path_base)).buildUpon()
        .appendPath(filter)
        .appendQueryParameter(getString(R.string.moviedb_query_param_api_key), "")
        .build();

      URL url = new URL(builtUri.toString());
      Log.v(LOG_TAG, "" + url);
      return baseFetchRequest(url);
    }

    private String fetchMovieAdditionalData(String id, String dataPath) throws MalformedURLException {
      Uri builtUri = Uri.parse(getString(R.string.moviedb_path_base)).buildUpon()
        .appendPath(id)
        .appendPath(dataPath)
        .appendQueryParameter(getString(R.string.moviedb_query_param_api_key), "")
        .build();

      URL url = new URL(builtUri.toString());
      Log.v(LOG_TAG, "" + url);
      return baseFetchRequest(url);
    }

    private MovieModel[] getMovieDataFromJson(String stringMoviesJson) throws JSONException {
      if (stringMoviesJson == null) {
        return null;
      }
      JSONObject moviesJson = new JSONObject(stringMoviesJson);
      JSONArray moviesJsonArray =
        moviesJson.getJSONArray(getString(R.string.generic_json_key_results));

      MovieModel[] returnedMovieModels = new MovieModel[moviesJsonArray.length()];
      for (int i = 0; i < moviesJsonArray.length(); i++) {
          JSONObject movieJSONObject = moviesJsonArray.getJSONObject(i);
          // Get the id out so we can get additional informtion
          String movieId = movieJSONObject.getString(getActivity().getString(R.string.movie_model_id));

          // Get the trailers
          /*String trailersJSONString = fetchMovieAdditionalData(movieId,
            getString(R.string.moviedb_path_movie_videos));
          JSONObject trailersJSONObject = new JSONObject(trailersJSONString);
          JSONArray trailersJSONArray =
            trailersJSONObject.getJSONArray(getString(R.string.generic_json_key_results));

          // Get the reviews
          String reviewsJSONString = fetchMovieAdditionalData(movieId,
            getString(R.string.moviedb_path_movie_reviews));
          JSONObject reviewsJSONObject = new JSONObject(reviewsJSONString);
          JSONArray reviewsJSONArray =
            reviewsJSONObject.getJSONArray(getString(R.string.generic_json_key_results));*/

          MovieModel model = new MovieModel(getActivity(), movieJSONObject);
          returnedMovieModels[i] = model;
      }
      return returnedMovieModels;
    }

    @Override
    protected MovieModel[] doInBackground(String... params) {
      // Will contain the raw JSON response as a string.
      String moviesJsonStr = null;
      String filter = getString(R.string.moviedb_path_movie_most_popular);
      if (params.length >= 1) {
        filter = params[0];
      }

      try {
        moviesJsonStr = fetchBaseMovieData(filter);
        try {
          return getMovieDataFromJson(moviesJsonStr);
        } catch (JSONException e) {
          Log.e(LOG_TAG, e.getMessage(), e);
          e.printStackTrace();
        }
      } catch (MalformedURLException e){
        Log.e(LOG_TAG, "Malformed URL Exception", e);
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
