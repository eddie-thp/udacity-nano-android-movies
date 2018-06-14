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
import io.ethp.movies.model.Video;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private static final String LOG_TAG = VideoAdapter.class.getSimpleName();

    private List<Video> mVideos = new ArrayList<>();

    class VideoViewHolder extends RecyclerView.ViewHolder {

        private final TextView mNameTextView;

        public VideoViewHolder(final View itemView) {
            super(itemView);

            mNameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
        }
    }

    @NonNull
    @Override
    public VideoAdapter.VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        final Video video = mVideos.get(position);
        holder.mNameTextView.setText(video.getName());
    }

    @Override
    public int getItemCount() {
        return mVideos.size();
    }

    public void addAll(List<Video> videos) {
        final int count = getItemCount();
        mVideos.addAll(videos);
        notifyDataSetChanged();
        // Try understanding why notifyItemRangeInserted is not working
        // notifyItemRangeInserted(count, videos.size());
    }

    public void clear() {
        final int count = getItemCount();
        mVideos.clear();
        notifyItemRangeRemoved(0, count);
    }
}
