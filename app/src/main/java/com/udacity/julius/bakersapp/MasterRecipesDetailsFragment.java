package com.udacity.julius.bakersapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.julius.bakersapp.adapter.RecipeAdapter;
import com.udacity.julius.bakersapp.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class MasterRecipesDetailsFragment extends Fragment implements
        RecipeAdapter.ListItemClickListener {
    private static final String ARG_RECIPE = "arg_recipe";

    private View view;

    private OnRecipeClickListener mCallback;

    private Recipe mRecipe;

    public MasterRecipesDetailsFragment() {
        // Required empty public constructor
    }

    public static MasterRecipesDetailsFragment newInstance(Recipe recipe) {
        MasterRecipesDetailsFragment fragment = new MasterRecipesDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_RECIPE, recipe);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRecipe = getArguments().getParcelable(ARG_RECIPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_master_recipes, container, false);

            CardView ingredientCardView = (CardView) view.findViewById(R.id.ingredients_card);
            ingredientCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mCallback != null) {
                        mCallback.onIngredientsSelected(view, mRecipe);
                    }
                }
            });

            RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.steps_recycler);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            RecipeAdapter mAdapter = new RecipeAdapter(getActivity(), this);
            mAdapter.refill(mRecipe, 1);
            mRecyclerView.setAdapter(mAdapter);

            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(),
                    DividerItemDecoration.VERTICAL);
            mRecyclerView.addItemDecoration(itemDecoration);
        }

        return view;
    }

    private ArrayList<Object> getSampleArrayList(List<Recipe> recipe) {
        ArrayList<Object> items = new ArrayList<>();

        for (int i = 0; i < recipe.size(); i++) {
            items.add(i, recipe.get(i));
        }
        return items;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRecipeClickListener) {
            mCallback = (OnRecipeClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRecipeClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        mCallback.onStepSelected(clickedItemIndex);
    }

    interface OnRecipeClickListener {
        void onIngredientsSelected(View view, Recipe recipe);

        void onStepSelected(int position);
    }
}
