package io.ethp.movies.detail;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.ImageViewCompat;
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
import io.ethp.movies.data.MovieDatabaseContract;
import io.ethp.movies.data.MovieDbHelper;
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

        private SQLiteDatabase mMovieDatabase;

        private Long mMovieId;

        // Movie (trailers) recycler view related attributes
        private VideoAdapter mVideosAdapter;
        private RecyclerView mVideosRecyclerView;
        private VideoLoaderCallbacks mVideosLoaderCallback;

        // Review recycler view related attributes
        private ReviewAdapter mReviewsAdapter;
        private RecyclerView mReviewsRecyclerView;
        private ReviewLoaderCallbacks mReviewsLoaderCallback;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            // Create a DB helper (this will create the DB when executed for the first time)
            // Get a writable database, as we will be adding favorite movies
            MovieDbHelper dbHelper = new MovieDbHelper(getContext());
            mMovieDatabase = dbHelper.getWritableDatabase();

            View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);

            Intent intent = getActivity().getIntent();
            if(intent!=null && intent.getSerializableExtra(Intent.EXTRA_TEXT) != null) {

                Movie movie  = (Movie) intent.getSerializableExtra(Intent.EXTRA_TEXT);
                mMovieId = movie.getId();

                ((TextView) rootView.findViewById(R.id.textViewTitle)).setText(movie.getTitle());
                movie.loadImage((ImageView) rootView.findViewById(R.id.imageViewPoster));
                ((TextView) rootView.findViewById(R.id.textViewReleaseDate)).setText(sdf.format(movie.getRelease()));
                ((RatingBar) rootView.findViewById(R.id.ratingBarUserRating)).setRating((float) movie.getUserRating() / 2);
                ((TextView) rootView.findViewById(R.id.textViewRating)).setText("(" + String.valueOf(movie.getUserRating()) + ")");
                ((TextView) rootView.findViewById(R.id.textViewOverview)).setText(movie.getOverview());

                // Update Favorite ImageView tint as described in: https://stackoverflow.com/questions/20121938/how-to-set-tint-for-an-image-view-programmatically-in-android/45571812#45571812
                ImageView favoriteImageView = rootView.findViewById(R.id.favoriteImageView);
                configureFavoriteImageView(favoriteImageView, movie);

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
                movieBundle.putLong("MOVIE_ID", mMovieId);

                Loader<List<Video>> videosLoader = loaderManager.getLoader(LOADER_VIDEO_ID);
                mVideosLoaderCallback = new VideoLoaderCallbacks(getContext(), mVideosAdapter);
                if (videosLoader == null) {
                    loaderManager.initLoader(LOADER_VIDEO_ID, movieBundle, mVideosLoaderCallback);
                } else {
                    // TODO Why can't I call - videosLoader.startLoading(); // Although the loader exists it seems to have lost the callback
                    loaderManager.restartLoader(LOADER_VIDEO_ID, movieBundle, mVideosLoaderCallback);
                }

                Loader<List<Review>> reviewsLoader = loaderManager.getLoader(LOADER_REVIEW_ID);
                mReviewsLoaderCallback = new ReviewLoaderCallbacks(getContext(), mReviewsAdapter);
                if (reviewsLoader == null) {
                    loaderManager.initLoader(LOADER_REVIEW_ID, movieBundle, mReviewsLoaderCallback);
                } else {
                    // TODO Why can't I call - reviewsLoader.startLoading(); // Although the loader exists it seems to have lost the callback
                    loaderManager.restartLoader(LOADER_REVIEW_ID, movieBundle, mReviewsLoaderCallback);
                }

            }

            return rootView;
        }

        private void configureFavoriteImageView(final ImageView favoriteImageView, final Movie movie) {
            final boolean isFavorite = movie.isFavorite(mMovieDatabase);

            if (isFavorite) {
                int colorAccent = ContextCompat.getColor(getContext(), R.color.colorAccent);
                ImageViewCompat.setImageTintList(favoriteImageView, ColorStateList.valueOf(colorAccent));
                favoriteImageView.setImageResource(R.drawable.ic_star_black_24dp);
            } else {
                int colorAccent = ContextCompat.getColor(getContext(), android.R.color.darker_gray);
                ImageViewCompat.setImageTintList(favoriteImageView, ColorStateList.valueOf(colorAccent));
                favoriteImageView.setImageResource(R.drawable.ic_star_border_black_24dp);
            }

            favoriteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentResolver contentResolver = getActivity().getContentResolver();
                    movie.setFavorite(!isFavorite, contentResolver);
                    configureFavoriteImageView(favoriteImageView, movie);
                }
            });
        }
    }
}