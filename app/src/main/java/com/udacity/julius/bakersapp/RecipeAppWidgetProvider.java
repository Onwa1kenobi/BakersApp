package com.udacity.julius.bakersapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.preference.PreferenceManager;
import android.widget.RemoteViews;

import java.io.IOException;
import java.net.URL;

public class RecipeAppWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String jsonString) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_view);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String preferenceString = sharedPreferences.getString(
                context.getResources().getString(R.string.pref_recipe_key),
                context.getResources().getString(R.string.pref_recipe_nutella_pie_value));

        String name;

        if (preferenceString.equals(context.getResources().getString(R.string.pref_recipe_nutella_pie_value))) {
            name = "Nutella Pie";
        } else if (preferenceString.equals(context.getResources().getString(R.string.pref_recipe_brownies_value))) {
            name = "Brownies";
        } else if (preferenceString.equals(context.getResources().getString(R.string.pref_recipe_yellow_cake_value))) {
            name = "Yellow Cake";
        } else {
            name = "Cheese Cake";
        }

        views.setTextViewText(R.id.recipe_title, name + " Ingredients");

        Intent intent = new Intent(context, RecipeDetailsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget, pendingIntent);

        Intent intentService = new Intent(context, ListWidgetService.class);

        intentService.putExtra("RecipeList", jsonString);
        views.setRemoteAdapter(appWidgetId, R.id.widget_list_view, intentService);
        views.setPendingIntentTemplate(R.id.widget_list_view, pendingIntent);

        views.setEmptyView(R.id.widget_list_view, R.id.empty_view);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Update all active views
        for (final int appWidgetId : appWidgetIds) {

            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            boolean isNetworkAvailable = manager.getActiveNetworkInfo().isAvailable();
            if (!isNetworkAvailable) {

                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_error_layout);
                appWidgetManager.updateAppWidget(appWidgetId, views);

            } else {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        URL url = NetworkUtils.buildUrl();
                        String s = null;

                        try {
                            s = NetworkUtils.getResponseFromHttpUrl(url);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return s;
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        updateAppWidget(context, appWidgetManager, appWidgetId, s);
                    }
                }.execute();

            }
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

