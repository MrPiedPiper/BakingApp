package com.fancystachestudios.bakingapp.bakingapp;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.fancystachestudios.bakingapp.bakingapp.customClasses.Recipe;

public class RecipeActivity extends AppCompatActivity implements MasterRecipeFragment.onChooseStep, StepFragment.stepFragmentInterface {

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

        stepContainer = findViewById(R.id.step_fragment_container);

        if(stepContainer != null){
            isSplitScreen = true;
        }

        if(savedInstanceState != null){
            Recipe savedRecipe = savedInstanceState.getParcelable(getString(R.string.step_recipe_key_fragment));
            int savedIndex = savedInstanceState.getInt(getString(R.string.step_index_key_fragment));
            if(!isSplitScreen) {
                recipe = savedRecipe;
                stepIndex = savedIndex;
            }
        }else{
            Intent startingIntent = getIntent();
            recipe = startingIntent.getParcelableExtra(getString(R.string.recipe_pass_key));
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("naputest", "saved recipe "+recipe.getName());
        outState.putParcelable(getString(R.string.step_recipe_key_fragment), recipe);
        outState.putInt(getString(R.string.step_index_key_fragment), stepIndex);

        Recipe savedRecipe = outState.getParcelable(getString(R.string.step_recipe_key_fragment));
    }

    @Override
    public void stepIndexChanged(int newIndex) {

    }

    @Override
    public void videoSeekChanged(long seekPos) {

    }
}
