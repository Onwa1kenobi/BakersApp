package com.udacity.julius.bakersapp;

import android.support.annotation.Nullable;
import android.util.Log;

import com.udacity.julius.bakersapp.IdlingResource.SimpleIdlingResource;
import com.udacity.julius.bakersapp.model.Recipe;
import com.udacity.julius.bakersapp.service.APIClient;
import com.udacity.julius.bakersapp.service.APIInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by ameh on 28/07/2017.
 */

/**
 * Takes a String and returns it after a while via a callback.
 * <p>
 * This executes a long-running operation on a different thread that results in problems with
 * Espresso if an {@link SimpleIdlingResource} is not implemented and registered.
 */
class RecipeDownloader {

    // Create an ArrayList of mTeas
    final static ArrayList<Recipe> mRecipes = new ArrayList<>();

    /**
     * This method is meant to simulate downloading a large image file which has a loading time
     * delay. This could be similar to downloading an image from the internet.
     * For simplicity, in this hypothetical situation, we've provided the image in
     * {@link drawable/order_activity_tea_image.jpg}.
     * We simulate a delay time of {@link ##DELAY_MILLIS} and once the time
     * is up we return the image back to the calling activity via a {@link DelayerCallback}.
     *
     * @param callback used to notify the caller asynchronously
     */
    static void downloadRecipe(final DelayerCallback callback,
                               @Nullable final SimpleIdlingResource idlingResource) {

        /**
         * The IdlingResource is null in production as set by the @Nullable annotation which means
         * the value is allowed to be null.
         *
         * If the idle state is true, Espresso can perform the next action.
         * If the idle state is false, Espresso will wait until it is true before
         * performing the next action.
         */
        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }

        APIInterface mAPIService =
                APIClient.getClient().create(APIInterface.class);

        Call<List<Recipe>> call = mAPIService.getBakingRecipes();
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                List<Recipe> recipes = response.body();
                Log.d("Recipe Downloader", "Number of movies received: " + recipes.size());
                if (callback != null) {
                    callback.onDone((ArrayList<Recipe>) recipes);
                    if (idlingResource != null) {
                        idlingResource.setIdleState(true);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                if (callback != null) {
                    callback.onFailed(t);
                }
            }
        });
    }

    interface DelayerCallback {
        void onDone(ArrayList<Recipe> recipes);

        void onFailed(Throwable t);
    }
}
