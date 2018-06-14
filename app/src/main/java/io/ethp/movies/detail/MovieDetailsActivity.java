package io.ethp.movies.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import io.ethp.movies.R;
import io.ethp.movies.adapters.ReviewAdapter;
import io.ethp.movies.adapters.VideoAdapter;
import io.ethp.movies.detail.ReviewLoaderCallbacks;
import io.ethp.movies.detail.VideoLoaderCallbacks;
import io.ethp.movies.loaders.MovieReviewsAsyncTaskLoader;
import io.ethp.movies.loaders.MovieVideosAsyncTaskLoader;
import io.ethp.movies.model.Movie;
import io.ethp.movies.model.Review;
import io.ethp.movies.model.Video;

public class MovieDetailsActivity extends AppCompatActivity{

    protected static final int LOADER_VIDEO_ID = 10;

    protected static final int LOADER_REVIEW_ID = 20;

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

        // Movie (trailers) recycler view related attributes
        private VideoAdapter mVideosAdapter;
        private RecyclerView mVideosRecyclerView;

        // Review recycler view related attributes
        private ReviewAdapter mReviewsAdapter;
        private RecyclerView mReviewsRecyclerView;

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


                // Setup Recycler View:
                // 1 - Set the layout manager
                // 2 - Set that items will have a fixed size
                // 3 - Set the adapter
                mVideosRecyclerView = (RecyclerView) rootView.findViewById(R.id.trailersRecyclerView);

                RecyclerView.LayoutManager videosLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                mVideosRecyclerView.setLayoutManager(videosLayoutManager);

                mVideosRecyclerView.setHasFixedSize(true);

                mVideosAdapter = new VideoAdapter();
                mVideosRecyclerView.setAdapter(mVideosAdapter);


                // Setup Recycler View:
                // 1 - Set the layout manager
                // 2 - Set that items will have a fixed size
                // 3 - Set the adapter
                mReviewsRecyclerView = (RecyclerView) rootView.findViewById(R.id.reviewsRecyclerView);

                RecyclerView.LayoutManager reviewsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                mReviewsRecyclerView.setLayoutManager(reviewsLayoutManager);

                mReviewsRecyclerView.setHasFixedSize(true);

                mReviewsAdapter = new ReviewAdapter();
                mReviewsRecyclerView.setAdapter(mReviewsAdapter);

                // See: https://stackoverflow.com/questions/31088404/difference-between-getloadermanger-and-getactivity-getsupportloadermanager
                // Explains that getLoaderManager() from v4.app.Fragment should be the same as calling getSupportLoaderManager() from v4.app.FragmentAcivity
                LoaderManager loaderManager = getLoaderManager();

                Bundle movieBundle = new Bundle();
                movieBundle.putLong("MOVIE_ID", movie.getId());

                Loader<List<Video>> videosLoader = loaderManager.getLoader(LOADER_VIDEO_ID);
                if (videosLoader == null) {
                    loaderManager.initLoader(LOADER_VIDEO_ID, movieBundle, new VideoLoaderCallbacks(getContext(), mVideosAdapter));
                }

                Loader<List<Review>> reviewsLoader = loaderManager.getLoader(LOADER_REVIEW_ID);
                if (reviewsLoader == null) {
                    loaderManager.initLoader(LOADER_REVIEW_ID, movieBundle, new ReviewLoaderCallbacks(getContext(), mReviewsAdapter));
                }

            }

            return rootView;
        }

    }


}