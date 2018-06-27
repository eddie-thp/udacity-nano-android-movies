package io.ethp.movies.adapters.catalog;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.ethp.movies.R;
import io.ethp.movies.model.Movie;

public class FavoriteMovieCatalogAdapter extends AbstractMovieCatalogAdapter {

    private Context mContext;
    private Cursor mCursor;

    public FavoriteMovieCatalogAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieCatalogViewHolder holder, int position) {
        // Move the mCursor to the position of the item to be displayed
        if (!mCursor.moveToPosition(position))
            return; // bail if returned null

        Movie movie = new Movie(mCursor);
        holder.setMovie(movie);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        // Close current cursor, replace with new one and notify recycler view to refresh.
        if (mCursor != null) mCursor.close();
        mCursor = newCursor;
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }
}
