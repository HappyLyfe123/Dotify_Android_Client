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

import java.util.List;

import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistsAdapter extends RecyclerView.Adapter<PlaylistsAdapter.MyViewHolder> {

    private List<String> playlistList;
    private RecyclerViewClickListener mlistener;
    private boolean deleteIconIsVisible = false;
    private OnPlaylistDeletedListener onPlaylistDeletedListener;
    private Context context;
    private DotifyUser user;

    public interface OnPlaylistDeletedListener{
        void onPlaylistDeleted(int position);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView playlistName;
        private ImageView deleteIcon;
        private RecyclerViewClickListener mListener;

        public MyViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            playlistName = (TextView) view.findViewById(R.id.playlist_list_name_text_view);
            // Delete icon that handles the event that the user wants to delete a playlist
            deleteIcon = (ImageView) view.findViewById(R.id.playlist_item_delete_icon);
            deleteIcon.setOnClickListener(new View.OnClickListener() {
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

    public PlaylistsAdapter(Context context, DotifyUser user, List<String> playlistList,
                    RecyclerViewClickListener listener, OnPlaylistDeletedListener onPlaylistDeletedListener){
        this.playlistList = playlistList;
        mlistener = listener;
        this.context = context;
        this.user = user;
        this.onPlaylistDeletedListener = onPlaylistDeletedListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.playlist_list_layout, parent, false);

        return new MyViewHolder(itemView, mlistener);
    }

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

    @Override
    public int getItemCount() {
        return playlistList.size();
    }

    /**
     * Sets whether the delete icons for each item in the views should be visible or not
     *
     * @param isVisible Whether the items in each recycler view should be visible
     */
    public void setDeleteIconVisibility(boolean isVisible) {
        deleteIconIsVisible = isVisible;
    }

}
