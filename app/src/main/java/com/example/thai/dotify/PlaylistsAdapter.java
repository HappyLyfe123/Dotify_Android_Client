package com.example.thai.dotify;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Server.DotifyHttpInterface;
import com.example.thai.dotify.Server.DotifySong;

import java.util.List;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * this object manipulates playlist data
 */
public class PlaylistsAdapter extends RecyclerView.Adapter<PlaylistsAdapter.MyViewHolder> {

    private List<String> playlistList;
    private RecyclerViewClickListener mlistener;
    private boolean deleteIconIsVisible = false;
    private OnPlaylistDeletedListener onPlaylistDeletedListener;
    private Context context;
    private DotifyUser user;

    /**
     * interface for deleting a playlist
     */
    public interface OnPlaylistDeletedListener{
        void onPlaylistDeleted(int position);
    }

    /**
     * the MyViewHolder object allows user to delete a playlist
     */
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView playlistName;
        private ImageView deleteIcon;
        private RecyclerViewClickListener mListener;

        /**
         * constructor that allows the user to delete a playlist
         * @param view - View object that will display playlist_list_layout layout file
         * @param listener
         */
        public MyViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            playlistName = (TextView) view.findViewById(R.id.playlist_list_name_text_view);
            // Delete icon that handles the event that the user wants to delete a playlist
            deleteIcon = (ImageView) view.findViewById(R.id.playlist_item_delete_icon);
            deleteIcon.setOnClickListener(new View.OnClickListener() {
                /**
                 * invoked when user selects delete
                 * @param view - View object representing file "playlist_list_layout"
                 */
                @Override
                public void onClick(View view) {
                    deleteIcon.setOnClickListener((deleteIconView) -> {
                        // Create a Dotify object to begin sending requests
                        final Dotify dotify = new Dotify("https://www.dotify.online/");
                        // Add an interceptor to log what is moving back and forth
                        dotify.addLoggingInterceptor(HttpLoggingInterceptor.Level.BODY);
                        // Create the interface to get the route methods
                        DotifyHttpInterface dotifyHttpInterface = dotify.getHttpInterface();
                        Call<ResponseBody> deletePlaylist = dotifyHttpInterface.deletePlaylist(
                                context.getString(R.string.appKey),
                                playlistName.getText().toString(),
                                user.getUsername()
                        );

                        /**
                         * send a reply to user after the playlist is deleted
                         */
                        deletePlaylist.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                Log.d("PlaylistsAdapter",
                                        "MyViewHolder -> onClick -> onResponse: Reponse Code = " + response.code());
                                if (response.code() == Dotify.OK){
                                    onPlaylistDeletedListener.onPlaylistDeleted(getAdapterPosition());
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });
                    });
                }
            });
            mListener = listener;
            view.setOnClickListener(this);
        }

        /**
         * invoked when user selects a playlist
         * @param v - View object for type PlaylistsAdapter
         */
        @Override
        public void onClick(View v) {
            mListener.onItemClick(v, getAdapterPosition());
        }

        /**
         * Sets the delete icon corresponding with the current recycler view item to be visible
         */
        public void setDeleteIconVisible() {
            deleteIcon.setVisibility(View.VISIBLE);
        }

        /**
         * Sets the delete icon corresponding with the current recycler view item to disappear
         */
        public void setDeleteIconGone() {
            deleteIcon.setVisibility(View.GONE);
        }
    }

    public PlaylistsAdapter(List<String> songList, RecyclerViewClickListener listener){
        this.playlistList = songList;
        mlistener = listener;
    }

    /**
     * constructor w/ given values
     * @param context
     * @param user - Dotify user logged into app
     * @param playlistList - list of all playlist names
     * @param listener
     * @param onPlaylistDeletedListener
     */
    public PlaylistsAdapter(Context context, DotifyUser user, List<String> playlistList,
                    RecyclerViewClickListener listener, OnPlaylistDeletedListener onPlaylistDeletedListener){
        this.playlistList = playlistList;
        mlistener = listener;
        this.context = context;
        this.user = user;
        this.onPlaylistDeletedListener = onPlaylistDeletedListener;
    }

    /**
     * create the view object containing data for file "playlist_list_layout"
     * @param parent
     * @param viewType
     * @return new MyViewHolder object
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.playlist_list_layout, parent, false);

        return new MyViewHolder(itemView, mlistener);
    }

    /**
     * bind data to the PlaylistsAdapter object
     * @param holder - MyViewHolder object NOT null
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String playlist = playlistList.get(position);
        holder.playlistName.setText(playlist);
        // Check whether the boolean for whether the delete icon should be visible is
        // set to true
        if (deleteIconIsVisible) {
            holder.setDeleteIconVisible();
        } else {
            holder.setDeleteIconGone();
        }
    }

    /**
     * get the size of the playlist list
     * @return
     */
    @Override
    public int getItemCount() {
        return playlistList.size();
    }

    /**
     * Sets whether the delete icons for each item in the views should be visible or not
     * @param isVisible Whether the items in each recycler view should be visible
     */
    public void setDeleteIconVisibility(boolean isVisible) {
        deleteIconIsVisible = isVisible;
    }

}
