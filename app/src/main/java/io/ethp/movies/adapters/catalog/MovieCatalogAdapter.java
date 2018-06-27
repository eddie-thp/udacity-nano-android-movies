package io.ethp.movies.adapters.catalog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.ethp.movies.R;
import io.ethp.movies.model.Movie;

public class MovieCatalogAdapter extends AbstractMovieCatalogAdapter {

    private List<Movie> movieCatalog = new ArrayList<>();

    @Override
    public void onBindViewHolder(@NonNull MovieCatalogViewHolder holder, int position) {
        final Movie movie = movieCatalog.get(position);
        holder.setMovie(movie);
    }

    @Override
    public int getItemCount() {
        return movieCatalog.size();
    }

    public void addAll(List<Movie> movies) {
        final int count = getItemCount();
        movieCatalog.addAll(movies);
        notifyItemRangeInserted(count, movies.size());
    }

    public void clear() {
        final int count = getItemCount();
        movieCatalog.clear();
        notifyItemRangeRemoved(0, count);
    }
}
