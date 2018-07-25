package com.fancystachestudios.bakingapp.bakingapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.fancystachestudios.bakingapp.bakingapp.adapters.StepAdapter;
import com.fancystachestudios.bakingapp.bakingapp.customClasses.Recipe;
import com.fancystachestudios.bakingapp.bakingapp.customClasses.Step;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeActivity extends AppCompatActivity implements MasterRecipeFragment.OnFragmentInteractionListener{

    Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        ButterKnife.bind(this);

        Intent startingIntent = getIntent();
        recipe = startingIntent.getParcelableExtra(getString(R.string.recipe_pass_key));

        if(recipe == null || recipe.getSteps().size() == 0) {
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show();
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        MasterRecipeFragment masterRecipeFragment = new MasterRecipeFragment();
        masterRecipeFragment.setRecipe(recipe);
        fragmentManager.beginTransaction()
                .add(R.id.master_fragment_container, masterRecipeFragment)
                .commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
