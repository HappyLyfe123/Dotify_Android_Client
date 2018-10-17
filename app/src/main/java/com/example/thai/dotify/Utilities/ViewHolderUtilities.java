package com.example.thai.dotify.Utilities;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.thai.dotify.RecyclerViewClickListener;

public class ViewHolderUtilities extends RecyclerView.ViewHolder implements View.OnClickListener {

    private RecyclerViewClickListener clickListener;

    public enum ViewHolderType{

    }


    public ViewHolderUtilities(View itemView, RecyclerViewClickListener listener) {
        super(itemView);

    }

    @Override
    public void onClick(View view) {
        clickListener.onItemClick(view, getAdapterPosition());
    }

}
