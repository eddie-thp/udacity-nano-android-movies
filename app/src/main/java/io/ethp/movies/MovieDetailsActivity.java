package io.ethp.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import io.ethp.movies.model.Movie;

public class MovieDetailsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private static final SimpleDateFormat sdf = new SimpleDateFormat("mm/yyyy");

        private static final String LOG_TAG = PlaceholderFragment.class.getSimpleName();

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);

            Intent intent = getActivity().getIntent();
            if(intent!=null && intent.getSerializableExtra(Intent.EXTRA_TEXT) != null) {
                Movie movie  = (Movie) intent.getSerializableExtra(Intent.EXTRA_TEXT);
                ((TextView) rootView.findViewById(R.id.textViewTitle)).setText(movie.getTitle());
                movie.loadImage((ImageView) rootView.findViewById(R.id.imageViewPoster));
                ((TextView) rootView.findViewById(R.id.textViewReleaseDate)).setText(sdf.format(movie.getRelease()));
                ((RatingBar) rootView.findViewById(R.id.ratingBarUserRating)).setRating((float) movie.getUserRating() / 2);
                ((TextView) rootView.findViewById(R.id.textViewRating)).setText("(" + String.valueOf(movie.getUserRating()) + ")");
                ((TextView) rootView.findViewById(R.id.textViewOverview)).setText(movie.getOverview());
            }

            return rootView;
        }

    }
}