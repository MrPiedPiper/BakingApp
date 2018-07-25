package com.fancystachestudios.bakingapp.bakingapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.fancystachestudios.bakingapp.bakingapp.adapters.StepAdapter;
import com.fancystachestudios.bakingapp.bakingapp.customClasses.Recipe;
import com.fancystachestudios.bakingapp.bakingapp.customClasses.Step;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeActivity extends AppCompatActivity implements MasterRecipeFragment.onChooseStep, StepFragment.OnFragmentInteractionListener{

    @BindView(R.id.step_fragment_container)
    FrameLayout stepContainer;

    StepFragment stepFragment;
    MasterRecipeFragment masterRecipeFragment;
    IngredientsFragment ingredientsFragment;

    Recipe recipe;
    int stepIndex;

    boolean isSplitScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        ButterKnife.bind(this);

        if(stepContainer != null){
            isSplitScreen = true;
        }

        if(savedInstanceState != null){
            Recipe savedRecipe = savedInstanceState.getParcelable(getString(R.string.step_recipe_key));
            int savedIndex = savedInstanceState.getInt(getString(R.string.step_index_key));
            if(isSplitScreen) {
                recipe = savedRecipe;
                stepIndex = savedIndex;
            }
        }else{
            Intent startingIntent = getIntent();
            recipe = startingIntent.getParcelableExtra(getString(R.string.recipe_pass_key));
        }

        if(recipe == null || recipe.getSteps().size() == 0) {
            Toast.makeText(this, "Something went wrong! (RecipeActivity)", Toast.LENGTH_LONG).show();
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        masterRecipeFragment = new MasterRecipeFragment();
        masterRecipeFragment.setRecipe(recipe);
        fragmentManager.beginTransaction()
                .add(R.id.master_fragment_container, masterRecipeFragment)
                .commit();

        if(isSplitScreen){
            stepFragment = new StepFragment();
            stepFragment.setRecipe(recipe);

            ingredientsFragment = new IngredientsFragment();
            ingredientsFragment.setRecipe(recipe);

            if(stepIndex == 0){
                fragmentManager.beginTransaction()
                        .add(stepContainer.getId(), ingredientsFragment)
                        .commit();
            }else{
                stepFragment.setStepIndex(stepIndex-1);
                fragmentManager.beginTransaction()
                        .add(stepContainer.getId(), stepFragment)
                        .commit();
            }
        }
    }


    @Override
    public void stepChosen(int stepIndex) {
        this.stepIndex = stepIndex;
        if(isSplitScreen){
            if (stepIndex == 0){
                if(findViewById(R.id.step_details) != null){
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(stepContainer.getId(), ingredientsFragment)
                            .commit();
                }
            }else{
                if(findViewById(R.id.step_details) == null){
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(stepContainer.getId(), stepFragment)
                            .commit();
                }
                stepFragment.setStepIndex(stepIndex - 1);
            }
        }else{
            Intent intent;
            if (stepIndex == 0) {
                intent = new Intent(this, IngredientActivity.class);
            } else {
                intent = new Intent(this, StepActivity.class);
            }
            intent.putExtra(getString(R.string.recipe_pass_key), recipe);
            intent.putExtra(getString(R.string.step_number_key), stepIndex - 1);
            startActivity(intent);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.step_recipe_key), recipe);
        outState.putInt(getString(R.string.step_index_key), stepIndex);

        Recipe savedRecipe = outState.getParcelable(getString(R.string.step_recipe_key));
        Log.d("naputest", "saving "+savedRecipe.getName());
    }
}
