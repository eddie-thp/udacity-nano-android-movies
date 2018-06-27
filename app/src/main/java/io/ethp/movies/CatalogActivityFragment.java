package io.ethp.movies;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.ethp.movies.adapters.catalog.FavoriteMovieCatalogAdapter;
import io.ethp.movies.adapters.catalog.MovieCatalogAdapter;
import io.ethp.movies.data.MovieDatabaseContract.MovieEntry;
import io.ethp.movies.data.MovieDbHelper;
import io.ethp.movies.loaders.MovieCatalogAsyncTaskLoader;
import io.ethp.movies.model.Movie;

/**
 * A placeholder fragment containing a simple view.
 */
public class CatalogActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Movie>> {

    private static final String LOG_TAG = CatalogActivityFragment.class.getSimpleName();

    private FavoriteMovieCatalogAdapter mFavoriteCatalogAdapter;

    private MovieCatalogAdapter mCatalogAdapter;

    private RecyclerView mCatalogRecyclerView;

    public CatalogActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_catalog, container, false);

        if (savedInstanceState != null) {
            // Restore state - nothing to restore yet
        }

        Context context = getActivity();

        // Setup Recycler View:
        // 1 - Set the layout manager
        // 2 - Set that items will have a fixed size
        // 3 - Set the adapter
        mCatalogRecyclerView = (RecyclerView) rootView.findViewById(R.id.gridview_catalog);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
        mCatalogRecyclerView.setLayoutManager(gridLayoutManager);

        mCatalogRecyclerView.setHasFixedSize(true);

        // Set the appropriate adapter, accordingly to the preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sortBy = prefs.getString(context.getString(R.string.pref_catalog_sorting_key), context.getString(R.string.pref_catalog_sorting_default));
        if (sortBy.equals(context.getString(R.string.pref_favorite))) {
            configureFavoriteAdapter(context);
        } else {
            configureWebServiceAdapter();
        }

        return rootView;
    }

    private void configureWebServiceAdapter() {
        if (mCatalogAdapter == null) {
            mCatalogAdapter = new MovieCatalogAdapter();
        }

        mCatalogRecyclerView.setAdapter(mCatalogAdapter);

        // See: https://stackoverflow.com/questions/31088404/difference-between-getloadermanger-and-getactivity-getsupportloadermanager
        // Explains that getLoaderManager() from v4.app.Fragment should be the same as calling getSupportLoaderManager() from v4.app.FragmentAcivity
        LoaderManager loaderManager = getLoaderManager();

        Loader<List<Movie>> movieCatalogLoader = loaderManager.getLoader(LOADER_MOVIE_CATALOG_ID);

        // TODO ASK: Why would I use the bundle, if the loader callbacks are implemented in this class ???
        Bundle loadMovieCatalogBundle = new Bundle();
        if (movieCatalogLoader == null) {
            loaderManager.initLoader(LOADER_MOVIE_CATALOG_ID, loadMovieCatalogBundle, this);
        } else {
            // TODO Why can't I call - movieCatalogLoader.startLoading(); // Although the loader exists it seems to have lost the callback
            loaderManager.restartLoader(LOADER_MOVIE_CATALOG_ID, loadMovieCatalogBundle, this);
        }
    }

    private void configureFavoriteAdapter(Context context) {
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase movieDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = movieDatabase.query(
                MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                MovieEntry._ID);

        if (mFavoriteCatalogAdapter == null) {
            mFavoriteCatalogAdapter = new FavoriteMovieCatalogAdapter(context, cursor);
        } else {
            mFavoriteCatalogAdapter.swapCursor(cursor);
        }
        mCatalogRecyclerView.setAdapter(mFavoriteCatalogAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        Context context = getActivity();

        // Set the appropriate adapter, accordingly to the preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sortBy = prefs.getString(context.getString(R.string.pref_catalog_sorting_key), context.getString(R.string.pref_catalog_sorting_default));
        if (sortBy.equals(context.getString(R.string.pref_favorite))) {
            configureFavoriteAdapter(context);
        } else {
            configureWebServiceAdapter();
        }
    }

    //// LOADER MANAGER CALLBACKS

    private static final int LOADER_MOVIE_CATALOG_ID = 101;

    @NonNull
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {

            case LOADER_MOVIE_CATALOG_ID:
                return new MovieCatalogAsyncTaskLoader(getContext());
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> data) {
        if(data != null) {
            // Cleaning up the adapter contents
            // TODO - I'm currently not handling data paging, I guess this will have to change
            mCatalogAdapter.clear();
            mCatalogAdapter.addAll(data);
        } else {
            // TODO Log / Toast
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {
        // TODO - nothing at the moment
    }
}
