package com.kevinudacity.popularmovies;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


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
    }

    return rootView;
  }

}
