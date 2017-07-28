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
import com.udacity.julius.bakersapp.model.Ingredient;
import com.udacity.julius.bakersapp.model.Recipe;
import com.udacity.julius.bakersapp.model.Step;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ameh on 28/07/2017.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "RecipeAdapter";
    private final int INGREDIENT = 0, STEP = 1;
    Context mContext;
    private List<Object> items = new ArrayList<>();
    private ListItemClickListener mOnClickListener;

    public RecipeAdapter() {

    }

    public RecipeAdapter(Context context, ListItemClickListener listener) {
        mContext = context;
        mOnClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Ingredient) {
            return INGREDIENT;
        } else if (items.get(position) instanceof Step) {
            return STEP;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case INGREDIENT:
                View v1 = inflater.inflate(R.layout.item_layout_ingredient, viewGroup, false);
                viewHolder = new ViewHolderIngredient(v1);
                break;
            case STEP:
                View v2 = inflater.inflate(R.layout.item_layout_step, viewGroup, false);
                viewHolder = new ViewHolderStep(v2);
                break;
            default:
                View v = inflater.inflate(android.R.layout.simple_list_item_1, viewGroup, false);
//                viewHolder = new RecyclerViewSimpleTextViewHolder(v);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case INGREDIENT:
                ViewHolderIngredient vh1 = (ViewHolderIngredient) viewHolder;
                configureViewHolderIngredient(vh1, position);
                break;
            case STEP:
                ViewHolderStep vh2 = (ViewHolderStep) viewHolder;
                configureViewHolderStep(vh2, position);
                break;
            default:
//                RecyclerViewSimpleTextViewHolder vh = (RecyclerViewSimpleTextViewHolder) viewHolder;
//                configureDefaultViewHolder(vh, position);
                break;
        }
    }

    private void configureViewHolderIngredient(ViewHolderIngredient vh1, int position) {
        Ingredient ingredient = (Ingredient) items.get(position);
        if (ingredient != null) {
            vh1.getIngredientName().setText(ingredient.getIngredient());
            vh1.getIngredientQuantity().setText("Quantity: " + String.valueOf(((Ingredient) items.get(position)).getQuantity()));
            vh1.getIngredientMeasure().setText("Measure: " + ingredient.getMeasure());
        }
    }

    private void configureViewHolderStep(ViewHolderStep vh2, int position) {
        Glide.with(mContext).load(((Step) items.get(position)).getThumbnailURL()).centerCrop()
                .placeholder(R.mipmap.ic_launcher_round).into(vh2.getImageView());
        vh2.getTextView().setText("Step " + String.valueOf(((Step) items.get(position)).getId()) + ": " + ((Step) items.get(position)).getShortDescription());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void refill(Recipe recipes, int type) {
        if (items != null) {
            items.clear();
        }
        if (type == INGREDIENT) {
            items.addAll(recipes.getIngredients());
            notifyDataSetChanged();
        } else if (type == STEP) {
            items.addAll(recipes.getSteps());
            notifyDataSetChanged();
        } else {
//            Toast.makeText(, "No data", Toast.LENGTH_SHORT).show();
        }
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public class ViewHolderIngredient extends RecyclerView.ViewHolder {
        private final TextView ingredientName, ingredientQuantity, ingredientMeasure;

        public ViewHolderIngredient(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            ingredientName = (TextView) v.findViewById(R.id.ingredient_name);
            ingredientQuantity = (TextView) v.findViewById(R.id.ingredient_quantity);
            ingredientMeasure = (TextView) v.findViewById(R.id.ingredient_measure);
        }

        public TextView getIngredientName() {
            return ingredientName;
        }

        public TextView getIngredientQuantity() {
            return ingredientQuantity;
        }

        public TextView getIngredientMeasure() {
            return ingredientMeasure;
        }

    }

    public class ViewHolderStep extends RecyclerView.ViewHolder {
        private final TextView textView;
        private ImageView imageView;

        public ViewHolderStep(View v) {
            super(v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int clickedPosition = getAdapterPosition();
                    mOnClickListener.onListItemClick(clickedPosition);
                }
            });
            textView = (TextView) v.findViewById(R.id.step_number_text);
            imageView = (ImageView) v.findViewById(R.id.step_image);
        }

        public TextView getTextView() {
            return textView;
        }

        public ImageView getImageView() {
            return imageView;
        }
    }
}
