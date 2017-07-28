package com.udacity.julius.bakersapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by ameh on 28/07/2017.
 */

public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private JSONArray mIngredients;
    private String mRecipeName;


    ListRemoteViewsFactory(Context context, Intent intent) {

        mContext = context;

        if (intent.hasExtra("RecipeList")) {

            String s = intent.getStringExtra("RecipeList");

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            String preferenceString = sharedPreferences.getString(mContext.getResources().getString(R.string.pref_recipe_key), mContext.getResources().getString(R.string.pref_recipe_nutella_pie_value));

            int position;

            if (preferenceString.equals(mContext.getResources().getString(R.string.pref_recipe_nutella_pie_value))) {
                position = 0;
            } else if (preferenceString.equals(mContext.getResources().getString(R.string.pref_recipe_brownies_value))) {
                position = 1;
            } else if (preferenceString.equals(mContext.getResources().getString(R.string.pref_recipe_yellow_cake_value))) {
                position = 2;
            } else {
                position = 3;
            }

            try {
                JSONArray jsonArray = new JSONArray(s);

                JSONObject ingredientObject = jsonArray.getJSONObject(position);
                mRecipeName = ingredientObject.getString("name");
                mIngredients = ingredientObject.getJSONArray("ingredients");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (mIngredients == null) return 0;
        return mIngredients.length();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.recipe_widget);

        String ingredient = null;

        try {
            JSONObject object = mIngredients.getJSONObject(position);

            ingredient = object.getString("ingredient");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        views.setTextViewText(R.id.widget_recipe_title, ingredient);

        Intent intent = new Intent();
        intent.putExtra("position", position);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}


