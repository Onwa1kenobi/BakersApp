package com.udacity.julius.bakersapp.service;

import com.udacity.julius.bakersapp.model.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by ameh on 28/07/2017.
 */

public interface APIInterface {
    @GET("/topher/2017/May/59121517_baking/baking.json")
    Call<List<Recipe>> getBakingRecipes();
}
