package com.example.thai.dotify.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thai.dotify.R;
import com.example.thai.dotify.RecyclerViewClickListener;
import com.example.thai.dotify.Server.DotifySong;
import com.google.gson.JsonArray;

import java.util.ArrayList;

public class SearchAlbumAdapter extends RecyclerView.Adapter<SearchAlbumAdapter.ItemsViewHolder>{

    private RecyclerViewClickListener onItemClickedListener;
    private ArrayList<String> albumNameList;
    private ArrayList<String> artistNameList;
    private ArrayList<JsonArray> albumSongsList;
    private DotifySong currSong;

    public SearchAlbumAdapter(RecyclerViewClickListener listener){
        albumNameList = new ArrayList<>();
        albumSongsList = new ArrayList<>();
        artistNameList = new ArrayList<>();
        onItemClickedListener = listener;
    }

    @NonNull
    @Override
    public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_recycler_item_layout, parent, false);
        return new SearchAlbumAdapter.ItemsViewHolder(itemView, onItemClickedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsViewHolder holder, int position) {
        holder.setViewText(albumNameList.get(position));
    }

    /**
     * Store album name and songs for that album
     * @param albumName name of the album
     * @param songsList all the information about the album
     */
    public void insertAlbum(String albumName, JsonArray songsList){
        albumNameList.add(albumName.substring(1, albumName.length() - 1));
        albumSongsList.add(songsList);
    }

    /**
     * Store album name, artist name and songs for that album
     */
    public void insertAlbum(String albumName, String artistName, JsonArray songList){
        albumNameList.add(albumName);
        artistNameList.add(artistName);
        albumSongsList.add(songList);
    }

    /**
     * Get the name of the album
     * @param position the location of where the name located
     * @return name of the album
     */
    public String getAlbumName(int position){
        return albumNameList.get(position);
    }

    /**
     * Get the name of the artist of the album
     * @param position the location where the name is located
     * @return artist name
     */
    public String getArtistName(int position){
        return artistNameList.get(position);
    }

    public DotifySong getSelectedSong(){
        return currSong;
    }

    /**
     * Get all of the song in the album
     * @param position the position of where the songs is located
     * @return all of the songs associated with the album
     */
    public JsonArray getSongList(int position){
        return albumSongsList.get(position);
    }

    /**
     * Get the amount of album in the list
     * @return
     */
    @Override
    public int getItemCount() {
        return albumNameList.size();
    }

    /**
     * Clear out all of the previous result
     */
    public void newResult(){
        albumSongsList.clear();
        albumNameList.clear();
        artistNameList.clear();
    }

    public class ItemsViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        private TextView albumNameTextView;
        private ImageView nextPageImage;
        private RecyclerViewClickListener itemClickedListener;

        public ItemsViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            this.itemClickedListener = listener;

            // Initialize the text from the search result item
            albumNameTextView = (TextView) view.findViewById(R.id.search_result_item_recycler_view);
            nextPageImage = (ImageView) view.findViewById(R.id.search_add_to_play_list_image_view);
            nextPageImage.setImageResource(R.drawable.next_page_icon);

            //Set listen for the text view
            albumNameTextView.setOnClickListener(this);
            nextPageImage.setOnClickListener(this);

        }

        /**
         * Sets the text for the current item in the recycler view
         * @param albumName
         */
        public void setViewText(String albumName) {
            albumNameTextView.setText(albumName);
        }

        /**
         * Get the item that the user clicked on
         * @param view
         */
        @Override
        public void onClick(View view) {
            itemClickedListener.onItemClick(view, getAdapterPosition());
        }
    }

}
