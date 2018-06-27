package io.ethp.movies.adapters.catalog;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import io.ethp.movies.R;
import io.ethp.movies.detail.MovieDetailsActivity;
import io.ethp.movies.model.Movie;

public class MovieCatalogViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mPosterImageView;

        private Movie mMovie;

        public MovieCatalogViewHolder(final View itemView) {
            super(itemView);

            mPosterImageView = (ImageView) itemView.findViewById(R.id.posterImageView);

            mPosterImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    Intent launchDetailActivityIntent = new Intent(context, MovieDetailsActivity.class).putExtra(Intent.EXTRA_TEXT, mMovie);
                    context.startActivity(launchDetailActivityIntent);
                }
            });

        }

        public void setMovie(Movie movie) {
            this.mMovie = movie;
            movie.loadImage(mPosterImageView);
        }
    }