package com.udacity.julius.bakersapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.udacity.julius.bakersapp.IdlingResource.SimpleIdlingResource;
import com.udacity.julius.bakersapp.adapter.HomeAdapter;
import com.udacity.julius.bakersapp.model.Recipe;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements HomeAdapter.ListItemClickListener,
        RecipeDownloader.DelayerCallback {

    private static final int SPAN_COUNT = 2;
    protected LayoutManagerType mCurrentLayoutManagerType;
    protected RecyclerView mRecyclerView;
    protected HomeAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ArrayList<Recipe> mRecipes = new ArrayList<>();
    private ProgressBar mProgressBar;
    private TextView mErrorMessageText;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    // The Idling Resource which will be null in production.
    @Nullable
    private SimpleIdlingResource mIdlingResource;

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mRecyclerView = (RecyclerView) findViewById(R.id.recipes_recycler);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mErrorMessageText = (TextView) findViewById(R.id.error_text);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        // if in landscape mode, change layout manager to grid layout
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRecyclerViewLayoutManager(LayoutManagerType.LINEAR_LAYOUT_MANAGER);
        } else {
            setRecyclerViewLayoutManager(LayoutManagerType.GRID_LAYOUT_MANAGER);
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RecipeDownloader.downloadRecipe(HomeActivity.this, mIdlingResource);
            }
        });
        Toolbar homeToolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(homeToolbar);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("RECIPE_CONTENTS")) {
                ArrayList<Recipe> recipes = savedInstanceState
                        .getParcelableArrayList("RECIPE_CONTENTS");
                mAdapter = new HomeAdapter(this, recipes, this);
                mRecyclerView.setAdapter(mAdapter);
            }
        } else {

            mAdapter = new HomeAdapter(this, mRecipes, this);
            mRecyclerView.setAdapter(mAdapter);
            mProgressBar.setVisibility(View.VISIBLE);

            mRecyclerView.setVisibility(View.INVISIBLE);
            mErrorMessageText.setVisibility(View.GONE);

            // Get the IdlingResource instance
            getIdlingResource();

            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        RecipeDownloader.downloadRecipe(this, mIdlingResource);
    }

    @Override
    public void onDone(ArrayList<Recipe> recipes) {
        mRecipes = recipes;
        mSwipeRefreshLayout.setRefreshing(false);
        showDataContent();
        mAdapter.refill(recipes);
    }

    @Override
    public void onFailed(Throwable t) {
        Log.e("RecipeDownloader", t.toString());
        mSwipeRefreshLayout.setRefreshing(false);
        showErrorMessage();
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorMessageText.setVisibility(View.VISIBLE);
    }

    private void showDataContent() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorMessageText.setVisibility(View.GONE);
    }

    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(this, SPAN_COUNT);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(this);
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(this);
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onListItemClick(int selectedItemIndex) {
        if (mRecipes.size() > 0) {
            startActivity(new Intent(this, RecipeDetailsActivity.class).putExtra(Intent.EXTRA_TEXT,
                    mRecipes.get(selectedItemIndex)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                mSwipeRefreshLayout.setRefreshing(true);
                RecipeDownloader.downloadRecipe(this, mIdlingResource);
                return true;
            case R.id.action_settings:
                Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(startSettingsActivity);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Recipe> recipesContents = mRecipes;
        outState.putParcelableArrayList("RECIPE_CONTENTS", recipesContents);
    }

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }
}
