package io.ethp.movies;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.ethp.movies.adapters.MovieCatalogAdapter;
import io.ethp.movies.tasks.FetchMoviesTask;

/**
 * A placeholder fragment containing a simple view.
 */
public class CatalogActivityFragment extends Fragment {

    private static final String LOG_TAG = CatalogActivityFragment.class.getSimpleName();

    private MovieCatalogAdapter mCatalogAdapter;

    public CatalogActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_catalog, container, false);

        Context context = getActivity();

        // Setup Recycler View:
        // 1 - Set the layout manager
        // 2 - Set that items will have a fixed size
        // 3 - Set the adapter
        final RecyclerView catalogRecyclerView = (RecyclerView) rootView.findViewById(R.id.gridview_catalog);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
        catalogRecyclerView.setLayoutManager(gridLayoutManager);

        catalogRecyclerView.setHasFixedSize(true);

        mCatalogAdapter = new MovieCatalogAdapter();
        catalogRecyclerView.setAdapter(mCatalogAdapter);

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
