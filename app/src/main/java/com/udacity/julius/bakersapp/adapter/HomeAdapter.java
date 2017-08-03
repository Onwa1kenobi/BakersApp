package com.udacity.julius.bakersapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.udacity.julius.bakersapp.R;
import com.udacity.julius.bakersapp.model.Recipe;

import java.util.ArrayList;

/**
 * Created by ameh on 28/07/2017.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private static final String TAG = "HomeAdapter";

    final private ListItemClickListener mOnClickListener;
    private Context mContext;

    private ArrayList<Recipe> mRecipe;

    public HomeAdapter(Context context, ArrayList<Recipe> dataSet, ListItemClickListener listener) {
        mContext = context;
        mRecipe = dataSet;
        mOnClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_layout_recipe, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTextView().setText(mRecipe.get(position).getName());
        Glide.with(mContext).load(mRecipe.get(position).getImage()).centerCrop()
                .placeholder(R.mipmap.ic_launcher_round).into(viewHolder.getImageView());

    }

    @Override
    public int getItemCount() {
        return mRecipe.size();
    }

    public void refill(ArrayList<Recipe> recipes) {
        if (mRecipe != null) {
            mRecipe.clear();
        }
        mRecipe.addAll(recipes);
        notifyDataSetChanged();
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private ImageView imageView;

        ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int clickedPosition = getAdapterPosition();
                    mOnClickListener.onListItemClick(clickedPosition);
                }
            });
            textView = (TextView) v.findViewById(R.id.recipe_name);
            imageView = (ImageView) v.findViewById(R.id.recipe_image);
        }

        TextView getTextView() {
            return textView;
        }

        ImageView getImageView() {
            return imageView;
        }

    }
}
