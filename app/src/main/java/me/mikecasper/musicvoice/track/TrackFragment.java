package me.mikecasper.musicvoice.track;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import me.mikecasper.musicvoice.R;
import me.mikecasper.musicvoice.api.responses.TrackResponse;
import me.mikecasper.musicvoice.api.responses.TrackResponseItem;
import me.mikecasper.musicvoice.models.Playlist;
import me.mikecasper.musicvoice.models.SpotifyUser;
import me.mikecasper.musicvoice.models.Track;
import me.mikecasper.musicvoice.nowplaying.NowPlayingActivity;
import me.mikecasper.musicvoice.playlist.PlaylistFragment;
import me.mikecasper.musicvoice.playlist.events.GetPlaylistTracksEvent;
import me.mikecasper.musicvoice.services.eventmanager.EventManagerProvider;
import me.mikecasper.musicvoice.services.eventmanager.IEventManager;
import me.mikecasper.musicvoice.services.musicplayer.events.SetPlaylistEvent;
import me.mikecasper.musicvoice.util.Logger;
import me.mikecasper.musicvoice.util.RecyclerViewItemClickListener;
import me.mikecasper.musicvoice.views.DividerItemDecoration;
import me.mikecasper.musicvoice.views.Scrollbar;

import java.util.ArrayList;
import java.util.List;

public class TrackFragment extends Fragment implements RecyclerViewItemClickListener {

    private static final String TAG = "TrackFragment";
    private List<TrackResponseItem> mTracks;
    private IEventManager mEventManager;
    private Playlist mPlaylist;

    public TrackFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEventManager = EventManagerProvider.getInstance(getContext());
        mTracks = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_list, container, false);

        getTracks();

        // Set the adapter
        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.trackList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new TrackAdapter(mTracks, this));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));

        Scrollbar scrollbar = (Scrollbar) view.findViewById(R.id.trackScrollbar);
        scrollbar.setRecyclerView(recyclerView);

        return view;
    }

    @Override
    public void onPause() {
        mEventManager.unregister(this);

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        mEventManager.register(this);

        Bundle args = getArguments();

        if (args != null && args.containsKey(PlaylistFragment.SELECTED_PLAYLIST)) {
            getActivity().setTitle(mPlaylist.getName());
        }
    }

    private void getTracks() {
        Bundle args = getArguments();

        if (args != null && args.containsKey(PlaylistFragment.SELECTED_PLAYLIST)) {
            mPlaylist = args.getParcelable(PlaylistFragment.SELECTED_PLAYLIST);

            SpotifyUser owner = mPlaylist.getOwner();

            mEventManager.postEvent(new GetPlaylistTracksEvent(owner.getId(), mPlaylist.getId(), 0));
            mTracks.clear();
        }
    }

    @Subscribe
    public void onTracksObtained(TrackResponse response) {
        Logger.i(TAG, "Tracks obtained");

        View view = getView();
        if (view != null) {
            mTracks.addAll(response.getItems());
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.trackList);
            TrackAdapter adapter = (TrackAdapter) recyclerView.getAdapter();
            adapter.updateTracks(mTracks);

            View progressBar = view.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof TrackAdapter.ViewHolder) {
            TrackAdapter.ViewHolder selectedTrack = (TrackAdapter.ViewHolder) viewHolder;
            Track track = selectedTrack.mTrack;

            int position = viewHolder.getAdapterPosition();

            List<TrackResponseItem> copy = new ArrayList<>(mTracks.size());

            for (TrackResponseItem item : mTracks) {
                copy.add(new TrackResponseItem(item));
            }

            //mEventManager.postEvent(new PlaySongEvent(track.getUri()));
            mEventManager.postEvent(new SetPlaylistEvent(copy, position));

            Intent intent = new Intent(getContext(), NowPlayingActivity.class);
            intent.putExtra(NowPlayingActivity.TRACK, track);
            intent.putExtra(NowPlayingActivity.SHOULD_PLAY_TRACK, true);
            startActivity(intent);
        }
    }
}