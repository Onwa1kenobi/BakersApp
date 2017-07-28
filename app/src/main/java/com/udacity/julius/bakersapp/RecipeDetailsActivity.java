package com.udacity.julius.bakersapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.udacity.julius.bakersapp.model.Recipe;

public class RecipeDetailsActivity extends AppCompatActivity
        implements MasterRecipesDetailsFragment.OnRecipeClickListener,
        StepFragment.OnFragmentInteractionListener {

    public static Recipe mRecipe;

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        Toolbar menuToolbar = (Toolbar) findViewById(R.id.recipe_details_toolbar);
        setSupportActionBar(menuToolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (getIntent().hasExtra(Intent.EXTRA_TEXT)) {
            mRecipe = getIntent().getParcelableExtra(Intent.EXTRA_TEXT);
            menuToolbar.setTitle(mRecipe.getName());
        }

        if (findViewById(R.id.tablet_linear_layout) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.master_list_fragment, new MasterRecipesDetailsFragment().newInstance(mRecipe))
                        .commit();

                fragmentManager.beginTransaction()
                        .replace(R.id.master_list_container, new IngredientsFragment().newInstance(mRecipe))
                        .commit();
            }
        } else {
            mTwoPane = false;

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.master_list_container, new MasterRecipesDetailsFragment().newInstance(mRecipe))
                    .commit();
        }


    }

    @Override
    public void onIngredientsSelected(View view, Recipe recipe) {

        if (mTwoPane) {
            if (view.getId() == R.id.ingredients_card) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.master_list_container, new IngredientsFragment().newInstance(mRecipe))
                        .commit();
            }
        } else {
            if (view.getId() == R.id.ingredients_card) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.master_list_container, new IngredientsFragment().newInstance(mRecipe))
                        .addToBackStack(IngredientsFragment.class.getSimpleName())
                        .commit();
            }
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
            case R.id.action_settings:
                Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(startSettingsActivity);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStepSelected(int position) {
        if (mTwoPane) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.master_list_container, new StepFragment().newInstance(mRecipe.getSteps().get(position)))
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.master_list_container, new StepFragment().newInstance(mRecipe.getSteps().get(position)))
                    .addToBackStack(StepFragment.class.getSimpleName())
                    .commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


}
