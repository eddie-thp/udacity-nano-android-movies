package io.ethp.movies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import io.ethp.movies.adapters.catalog.FavoriteMovieCatalogAdapter;
import io.ethp.movies.adapters.catalog.MovieCatalogAdapter;
import io.ethp.movies.data.MovieDatabaseContract;
import io.ethp.movies.loaders.MovieCatalogAsyncTaskLoader;
import io.ethp.movies.model.Movie;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>> {

    private static final String STATE_CATALOG_LAYOUT_MANAGER = "CATALOG_LAYOUT_MANAGER_STATE";

    private GridLayoutManager mCatalogLayoutManager;

    Parcelable mCatalogSavedState;

    private FavoriteMovieCatalogAdapter mFavoriteCatalogAdapter;

    private MovieCatalogAdapter mCatalogAdapter;

    private RecyclerView mCatalogRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup Recycler View:
        // 1 - Set the layout manager
        // 2 - Set that items will have a fixed size
        // 3 - Set the adapter
        mCatalogRecyclerView = (RecyclerView) findViewById(R.id.gridview_catalog);

        mCatalogLayoutManager = new GridLayoutManager(this, 2);
        mCatalogRecyclerView.setLayoutManager(mCatalogLayoutManager);

        mCatalogRecyclerView.setHasFixedSize(true);
    }

    private void configureWebServiceAdapter() {
        if (mCatalogAdapter == null) {
            mCatalogAdapter = new MovieCatalogAdapter();
        }

        mCatalogRecyclerView.setAdapter(mCatalogAdapter);

        // See: https://stackoverflow.com/questions/31088404/difference-between-getloadermanger-and-getactivity-getsupportloadermanager
        // Explains that getLoaderManager() from v4.app.Fragment should be the same as calling getSupportLoaderManager() from v4.app.FragmentAcivity
        LoaderManager loaderManager = getSupportLoaderManager();

        Loader<List<Movie>> movieCatalogLoader = loaderManager.getLoader(LOADER_MOVIE_CATALOG_ID);

        // TODO ASK: Why would I use the bundle, if the loader callbacks are implemented in this class ???
        Bundle loadMovieCatalogBundle = new Bundle();
        if (movieCatalogLoader == null) {
            loaderManager.initLoader(LOADER_MOVIE_CATALOG_ID, loadMovieCatalogBundle, this);
        } else {
            // TODO Why can't I call - movieCatalogLoader.startLoading(); // Although the loader exists it seems to have lost the callback
            loaderManager.restartLoader(LOADER_MOVIE_CATALOG_ID, loadMovieCatalogBundle, this);
            // movieCatalogLoader.startLoading();
        }
    }

    private void configureFavoriteAdapter() {
        Cursor cursor = getContentResolver().query(MovieDatabaseContract.MovieEntry.CONTENT_URI,
                null, null, null, MovieDatabaseContract.MovieEntry._ID);

        if (mFavoriteCatalogAdapter == null) {
            mFavoriteCatalogAdapter = new FavoriteMovieCatalogAdapter(this, cursor);
        } else {
            mFavoriteCatalogAdapter.swapCursor(cursor);
        }
        mCatalogRecyclerView.setAdapter(mFavoriteCatalogAdapter);

        // Restore RecyclerView state
        if (mCatalogSavedState != null) {
            mCatalogLayoutManager.onRestoreInstanceState(mCatalogSavedState);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        // Set the appropriate adapter, accordingly to the preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String sortBy = prefs.getString(getString(R.string.pref_catalog_sorting_key), getString(R.string.pref_catalog_sorting_default));
        if (sortBy.equals(getString(R.string.pref_favorite))) {
            configureFavoriteAdapter();
        } else {
            configureWebServiceAdapter();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Saving RecyclerView state accordingly to: https://stackoverflow.com/questions/28236390/recyclerview-store-restore-state-between-activities
        // NOTE: During testing and debugging, I've noticed that the state wasn't being restored, this other thread clarified the issue: https://stackoverflow.com/questions/5574462/why-onrestoreinstancestate-never-gets-called
        mCatalogSavedState = mCatalogLayoutManager.onSaveInstanceState();
        outState.putParcelable(STATE_CATALOG_LAYOUT_MANAGER, mCatalogSavedState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            mCatalogSavedState = savedInstanceState.getParcelable(STATE_CATALOG_LAYOUT_MANAGER);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent launchSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(launchSettingsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //// LOADER MANAGER CALLBACKS

    private static final int LOADER_MOVIE_CATALOG_ID = 101;

    @NonNull
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case LOADER_MOVIE_CATALOG_ID:
                return new MovieCatalogAsyncTaskLoader(this);
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

            // Restore RecyclerView state
            if (mCatalogSavedState != null) {
                mCatalogLayoutManager.onRestoreInstanceState(mCatalogSavedState);
            }

        } else {
            // TODO Log / Toast
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {
        // TODO - nothing at the moment
    }

}
