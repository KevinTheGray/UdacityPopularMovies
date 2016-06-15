package com.kevinudacity.popularmovies;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailFragment extends Fragment {

  private final String LOG_TAG = "MovieDetailFragment";
  private MovieModel movieModel;

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
      TextView movieNameTextView = (TextView) rootView.findViewById(R.id.textview_movie_title);
      movieNameTextView.setText(movieModel.getOriginalTitle());
      TextView releaseDateTextView = (TextView) rootView.findViewById(R.id.textview_release_date);
      releaseDateTextView.setText(movieModel.getReleaseDate());
      // TextView durationTextView = (TextView) rootView.findViewById(R.id.textview_duration);

      TextView userRatingTextView = (TextView) rootView.findViewById(R.id.textview_user_rating);
      userRatingTextView.setText("" + movieModel.getVoteAverage() + "/10");
      TextView synopsisTextView = (TextView) rootView.findViewById(R.id.textview_plot_synopsis);
      synopsisTextView.setText(movieModel.getOverview());
      ImageView posterImageView = (ImageView) rootView.findViewById(R.id.image_view_movie_poster);
      try {
        URL imageUrl = movieModel.getFullPosterPath(getActivity());
        Picasso.with(getActivity()).load(imageUrl.toString()).into(posterImageView);
      } catch (MalformedURLException e) {
        Log.e(LOG_TAG, "Could not form url for image");
      }
    }
    return rootView;
  }

}
