package com.udacity.julius.bakersapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.julius.bakersapp.adapter.RecipeAdapter;
import com.udacity.julius.bakersapp.model.Recipe;

public class IngredientsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    private Recipe mParam1;
    private View view;

    public IngredientsFragment() {
        // Required empty public constructor
    }

    public static IngredientsFragment newInstance(Recipe param1) {
        IngredientsFragment fragment = new IngredientsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_ingredients, container, false);

            RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.ingredients_recycler);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            RecipeAdapter mAdapter = new RecipeAdapter();
            mAdapter.refill(mParam1, 0);
            mRecyclerView.setAdapter(mAdapter);

            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(),
                    DividerItemDecoration.VERTICAL);
            mRecyclerView.addItemDecoration(itemDecoration);
        }

        return view;
    }
}
