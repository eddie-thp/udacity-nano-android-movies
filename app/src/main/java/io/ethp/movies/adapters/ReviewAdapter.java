package io.ethp.movies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.ethp.movies.R;
import io.ethp.movies.model.Review;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private static final String LOG_TAG = ReviewAdapter.class.getSimpleName();

    private List<Review> mReviews = new ArrayList<>();

    class ReviewViewHolder extends RecyclerView.ViewHolder {

        private final TextView mContentTextView;

        private final TextView mAuthorTextView;

        public ReviewViewHolder(final View itemView) {
            super(itemView);

            mContentTextView = (TextView) itemView.findViewById(R.id.contentTextView);
            mAuthorTextView = (TextView) itemView.findViewById(R.id.authorTextView);
        }
    }

    @NonNull
    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        final Review review = mReviews.get(position);
        holder.mContentTextView.setText(review.getContent());
        holder.mAuthorTextView.setText(review.getAuthor());
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public void addAll(List<Review> reviews) {
        final int count = getItemCount();
        mReviews.addAll(reviews);
        //notifyItemRangeInserted(count, reviews.size());
        notifyDataSetChanged();
    }

    public void clear() {
        final int count = getItemCount();
        mReviews.clear();
        notifyItemRangeRemoved(0, count);
    }
}
