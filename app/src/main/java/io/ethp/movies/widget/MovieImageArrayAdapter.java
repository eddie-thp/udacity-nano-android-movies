package io.ethp.movies.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.ethp.movies.model.Movie;

/**
 * Adapter responsible for interacting with the GridView that will display movie images
 */
public class MovieImageArrayAdapter extends ArrayAdapter<Movie> {

    private static final String LOG_TAG = MovieImageArrayAdapter.class.getSimpleName();

    private int mResource;

    private int mImageViewId;

    private LayoutInflater mInflater;

    public MovieImageArrayAdapter(Context context, int resource, int imageViewResourceId) {
        this(context, resource, imageViewResourceId, new ArrayList<Movie>());
    }

    public MovieImageArrayAdapter(Context context, int resource, int imageViewResourceId, @NonNull Movie[] objects) {
        this(context, resource, imageViewResourceId, Arrays.asList(objects));
    }

    public MovieImageArrayAdapter(Context context, int resource, int imageViewResourceId, @NonNull List<Movie> objects) {
        super(context, resource, 0, objects);
        this.mResource = resource;
        this.mImageViewId = imageViewResourceId;
        this.mInflater = LayoutInflater.from(context);
    }

    public MovieImageArrayAdapter(Context context, int resource) {
        this(context, resource, 0);
    }

    public MovieImageArrayAdapter(Context context, int resource, Movie[] objects) {
        this(context, resource, 0, objects);
    }

    public MovieImageArrayAdapter(Context context, int resource, List<Movie> objects) {
        this(context, resource, 0, objects);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Instead of copying/pasting the example found in:
        // https://futurestud.io/blog/picasso-adapter-use-for-listview-gridview-etc/
        // I tried implementing similarly to what I've seen in ArrayAdapter.createViewFromResource,
        // but targeting an ImageView and using our mImageViewId member variable.
        // Maybe too much work, but it was interesting to understand the ArrayAdapter idea

        View view;
        ImageView imageView;

        if (convertView == null) {
            view = mInflater.inflate(mResource, parent, false);
        } else {
            view = convertView;
        }

        try {
            if (mImageViewId == 0) {
                //  If no custom field is assigned, assume the whole resource is a ImageView
                imageView = (ImageView) view;
            } else {
                //  Otherwise, find the ImageView field within the layout
                imageView = (ImageView) view.findViewById(mImageViewId);
            }
        } catch (ClassCastException e) {
            Log.e(LOG_TAG, "You must supply a resource ID for a ImageView");
            throw new IllegalStateException(LOG_TAG + " the resource ID to be a ImageView", e);
        }

        Movie movie = getItem(position);
        movie.loadImage(imageView);

        return view;
    }

}
