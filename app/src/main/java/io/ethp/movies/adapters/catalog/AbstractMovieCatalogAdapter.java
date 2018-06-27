package io.ethp.movies.adapters.catalog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.ethp.movies.R;
import io.ethp.movies.model.Movie;

public abstract class AbstractMovieCatalogAdapter extends RecyclerView.Adapter<MovieCatalogViewHolder> {

    @NonNull
    @Override
    public MovieCatalogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie_catalog, parent, false);
        return new MovieCatalogViewHolder(view);
    }

}
