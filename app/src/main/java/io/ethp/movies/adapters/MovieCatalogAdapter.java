package io.ethp.movies.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import io.ethp.movies.MovieDetailsActivity;
import io.ethp.movies.R;
import io.ethp.movies.model.Movie;

public class MovieCatalogAdapter extends RecyclerView.Adapter<MovieCatalogAdapter.MovieViewHolder> {

    private static final String LOG_TAG = MovieCatalogAdapter.class.getSimpleName();

    private List<Movie> movieCatalog = new ArrayList<>();

    class MovieViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mPosterImageView;

        public MovieViewHolder(final View itemView) {
            super(itemView);

            mPosterImageView = (ImageView) itemView.findViewById(R.id.posterImageView);

            mPosterImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    int position = getAdapterPosition();
                    Movie clickedMovie = (Movie) movieCatalog.get(position);
                    Intent launchDetailActivityIntent = new Intent(context, MovieDetailsActivity.class).putExtra(Intent.EXTRA_TEXT, clickedMovie);
                    context.startActivity(launchDetailActivityIntent);
                }
            });

        }
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie_catalog, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        final Movie movie = movieCatalog.get(position);
        movie.loadImage(holder.mPosterImageView);
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
