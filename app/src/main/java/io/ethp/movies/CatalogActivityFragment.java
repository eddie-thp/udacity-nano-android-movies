package io.ethp.movies;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import io.ethp.movies.widget.MovieImageArrayAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class CatalogActivityFragment extends Fragment {

    private static final String LOG_TAG = CatalogActivityFragment.class.getSimpleName();

    private MovieImageArrayAdapter mCatalogAdapter;

    public CatalogActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_catalog, container, false);

        mCatalogAdapter = new MovieImageArrayAdapter(getActivity(), R.layout.grid_item_movie, R.id.grid_item_movie_image);

        // Setup gridView
        GridView catalogGridView = (GridView) rootView.findViewById(R.id.gridview_catalog);
        catalogGridView.setAdapter(mCatalogAdapter);

        return rootView;
    }

    private void fetchMovies() {

        String apiKey = getString(R.string.movie_db_org_api_key);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sorting = prefs.getString(getString(R.string.pref_catalog_sorting_key), getString(R.string.pref_catalog_sorting_default));

        // Cleaning up the adapter contents
        // TODO - I'm currently not handling data paging, I guess this will have to change
        mCatalogAdapter.clear();

        // Create and execute task
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask(mCatalogAdapter);
        fetchMoviesTask.execute(apiKey, sorting);
    }


    @Override
    public void onStart() {
        super.onStart();
        fetchMovies();
    }
}
