package me.mikecasper.musicvoice.track;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.mikecasper.musicvoice.R;
import me.mikecasper.musicvoice.api.responses.TrackResponseItem;
import me.mikecasper.musicvoice.models.Album;
import me.mikecasper.musicvoice.models.Artist;
import me.mikecasper.musicvoice.models.Track;
import me.mikecasper.musicvoice.util.RecyclerViewItemClickListener;

import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> {

    private RecyclerViewItemClickListener mListener;
    private List<TrackResponseItem> mTracks;

    public TrackAdapter(List<TrackResponseItem> tracks, RecyclerViewItemClickListener listener) {
        mListener = listener;
        mTracks = tracks;
    }

    public void updateTracks(List<TrackResponseItem> tracks) {
        mTracks = tracks;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_track, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Track track = mTracks.get(position).getTrack();
        holder.mTrack = track;
        List<Artist> artists = track.getArtists();
        StringBuilder artistNamesBuilder = new StringBuilder();
        Album album = track.getAlbum();
        Context context = holder.mArtistAndTrackNameTextView.getContext();

        for (Artist artist : artists) {
            artistNamesBuilder.append(artist.getName());
            artistNamesBuilder.append(", ");
        }

        String artistNames = artistNamesBuilder.substring(0, artistNamesBuilder.length() - 2);

        String artistAndAlbumName = context.getString(R.string.artists_and_album, artistNames, album.getName());

        holder.mTrackNameTextView.setText(track.getName());
        holder.mArtistAndTrackNameTextView.setText(artistAndAlbumName);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTracks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Track mTrack;
        public final View mView;
        public final TextView mTrackNameTextView;
        public final TextView mArtistAndTrackNameTextView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTrackNameTextView = (TextView) view.findViewById(R.id.trackName);
            mArtistAndTrackNameTextView = (TextView) view.findViewById(R.id.artistAndAlbum);
        }
    }
}