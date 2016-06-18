package com.kevinudacity.popularmovies;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailFragment extends Fragment {

  private final String LOG_TAG = "MovieDetailFragment";
  private MovieModel movieModel;
  private MovieDetailAdapter mMovieDetailAdapter;

  public MovieDetailFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
    Bundle bundle = getActivity().getIntent().getExtras();
    if (bundle == null) {
      bundle = getArguments();
    }
    if (bundle != null) {
      movieModel = bundle.getParcelable(getString(R.string.bundle_key_movie_model));
      mMovieDetailAdapter = new MovieDetailAdapter(getActivity(), movieModel);
      ListView listView = (ListView) rootView.findViewById(R.id.movie_detail_fragment_listview);
      listView.setAdapter(mMovieDetailAdapter);
      fetchMovieDetails();
    }

    return rootView;
  }

  // Helpers
  private void fetchMovieDetails() {
    FetchMovieDetailsTask fetchMoviesTask = new FetchMovieDetailsTask();
    fetchMoviesTask.execute();
  }

  public class FetchMovieDetailsTask extends AsyncTask<Void, Void, Void> {

    private final String LOG_TAG = FetchMovieDetailsTask.class.getSimpleName();

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

    @Override
    protected Void doInBackground(Void... params) {
      try {
       String movieTrailersJsonStr = fetchMovieAdditionalData(movieModel.getId(),
          getString(R.string.moviedb_path_movie_videos));
        String movieReviewsJsonStr = fetchMovieAdditionalData(movieModel.getId(),
          getString(R.string.moviedb_path_movie_reviews));
        try {
          // Trailers
          JSONObject trailersJSONObject = new JSONObject(movieTrailersJsonStr);
          JSONArray trailersJSONArray =
            trailersJSONObject.getJSONArray(getString(R.string.generic_json_key_results));
          ArrayList<MovieTrailerModel> trailerModelArrayList = new ArrayList<>();
          for (int i = 0; i < trailersJSONArray.length(); i++) {
            JSONObject trailerJSONObject = trailersJSONArray.getJSONObject(i);
            MovieTrailerModel movieTrailerModel = new MovieTrailerModel(getActivity(), trailerJSONObject);
            trailerModelArrayList.add(movieTrailerModel);
          }
          // Reviews
          JSONObject reviewsJSONObject = new JSONObject(movieReviewsJsonStr);
          JSONArray reviewsJSONArray =
            reviewsJSONObject.getJSONArray(getString(R.string.generic_json_key_results));
          ArrayList<MovieReviewModel> reviewModelArrayList = new ArrayList<>();
          for (int i = 0; i < reviewsJSONArray.length(); i++) {
            JSONObject reviewJSONObject = reviewsJSONArray.getJSONObject(i);
            MovieReviewModel movieReviewModel = new MovieReviewModel(getActivity(), reviewJSONObject);
            reviewModelArrayList.add(movieReviewModel);
          }
          movieModel.setTrailers(trailerModelArrayList);
          movieModel.setReviews(reviewModelArrayList);
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
    protected void onPostExecute(Void aVoid) {
      mMovieDetailAdapter.setmMovieModel(movieModel);
    }
  }

}
