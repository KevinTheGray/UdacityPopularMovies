package com.kevinudacity.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements MovieGridFragment.Callback {

  private final String LOG_TAG = MainActivity.class.getSimpleName();
  private static final String DETAILFRAGMENT_TAG = "DFTAG";

  private boolean mTwoPane;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    if (findViewById(R.id.movie_detail_container) != null) {
      mTwoPane = true;
      if (savedInstanceState == null) {
        getSupportFragmentManager().beginTransaction()
          .replace(R.id.movie_detail_container, new MovieDetailFragment(), DETAILFRAGMENT_TAG)
          .commit();
      }
    } else {
      mTwoPane = false;
    }
  }

  @Override
  public void onItemSelected(MovieModel movieModel) {
    String movieModelBundleKey = getString(R.string.bundle_key_movie_model);

    if (mTwoPane == true) {
      Bundle args = new Bundle();
      args.putParcelable(movieModelBundleKey, movieModel);
      MovieDetailFragment fragment = new MovieDetailFragment();
      fragment.setArguments(args);
      getSupportFragmentManager().beginTransaction()
        .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
        .commit();
    } else {
      Intent intent = new Intent(this, MovieDetailActivity.class);
      intent.putExtra(movieModelBundleKey, movieModel);
      startActivity(intent);
    }
  }
}
