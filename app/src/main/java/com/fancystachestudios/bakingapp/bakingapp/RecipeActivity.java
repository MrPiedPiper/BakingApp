package com.fancystachestudios.bakingapp.bakingapp;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.fancystachestudios.bakingapp.bakingapp.customClasses.Recipe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeActivity extends AppCompatActivity implements MasterRecipeFragment.onChooseStep, StepFragment.stepFragmentInterface {

    @Nullable
    @BindView(R.id.step_fragment_container)
    FrameLayout stepContainer;

    @BindView(R.id.master_fragment_container)
    FrameLayout masterFragmentContainer;

    ScrollView scrollView;

    StepFragment stepFragment;
    MasterRecipeFragment masterRecipeFragment;
    IngredientsFragment ingredientsFragment;

    Recipe recipe;
    int stepIndex;
    private long seekPos = 0;

    boolean isSplitScreen = false;
    boolean isLookingAtStep = false;
    boolean isLandscape = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        ButterKnife.bind(this);

        if(stepContainer != null){
            isSplitScreen = true;
        }

        if(getResources().getConfiguration().orientation == getResources().getConfiguration().ORIENTATION_LANDSCAPE) {
            isLandscape = true;
        }

        if(savedInstanceState != null){
            Recipe savedRecipe = savedInstanceState.getParcelable(getString(R.string.step_recipe_key_fragment));
            int savedIndex = savedInstanceState.getInt(getString(R.string.step_index_key_fragment));
            boolean savedIsLookingAtStep = savedInstanceState.getBoolean(getString(R.string.step_is_on_step_boolean_key_fragment));
            long savedSeekPos = savedInstanceState.getLong(getString(R.string.step_seek_key_activity));
            recipe = savedRecipe;
            stepIndex = savedIndex;
            isLookingAtStep = savedIsLookingAtStep;
            seekPos = savedSeekPos;
        }else{
            Intent startingIntent = getIntent();
            recipe = startingIntent.getParcelableExtra(getString(R.string.recipe_pass_key));
        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        masterRecipeFragment = new MasterRecipeFragment();
        masterRecipeFragment.setRecipe(recipe);

        stepFragment = new StepFragment();
        stepFragment.setRecipe(recipe);

        ingredientsFragment = new IngredientsFragment();
        ingredientsFragment.setRecipe(recipe);
//TODO Now that the step-activity  is implemented into the fragment, add the IngredientActivity too.
        if(isSplitScreen){
            isLookingAtStep = true;
            fragmentManager.beginTransaction()
                    .add(masterFragmentContainer.getId(), masterRecipeFragment)
                    .commit();
            if(stepIndex == 0){
                fragmentManager.beginTransaction()
                        .add(stepContainer.getId(), ingredientsFragment)
                        .commit();
            }else{
                stepFragment.setStepIndex(stepIndex-1);
                fragmentManager.beginTransaction()
                        .replace(stepContainer.getId(), stepFragment)
                        .commit();
            }
        }else{
            if(!isLookingAtStep) {
                fragmentManager.beginTransaction()
                        .add(masterFragmentContainer.getId(), masterRecipeFragment)
                        .commit();
            } else {
                if(stepIndex == 0){
                    fragmentManager.beginTransaction()
                            .add(masterFragmentContainer.getId(), ingredientsFragment)
                            .commit();
                    isLookingAtStep = true;
                }else{
                    stepFragment.setStepIndex(stepIndex);
                    fragmentManager.beginTransaction()
                            .add(masterFragmentContainer.getId(), stepFragment)
                            .commit();
                    stepFragment.setStartPos(seekPos);
                    if(isLandscape){
                        getSupportActionBar().hide();
                        stepFragment.isFullScreen = true;
                    }
                }
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
        }else if(stepIndex == 0){
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(masterFragmentContainer.getId(), ingredientsFragment)
                    .commit();
            isLookingAtStep = true;
        }else{
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(masterFragmentContainer.getId(), stepFragment)
                    .commit();
            stepFragment.setStepIndex(stepIndex-1);
            isLookingAtStep = true;
            stepFragment.setStartPos(seekPos);
            fragmentManager.beginTransaction()
                    .replace(masterFragmentContainer.getId(), stepFragment)
                    .commit();
            if(isLandscape){
                getSupportActionBar().hide();
                stepFragment.isFullScreen = true;
            }
            /*Intent intent;
            if (stepIndex == 0) {
                intent = new Intent(this, IngredientActivity.class);
            } else {
                intent = new Intent(this, StepActivity.class);
            }
            intent.putExtra(getString(R.string.recipe_pass_key), recipe);
            intent.putExtra(getString(R.string.step_number_key), stepIndex - 1);
            startActivity(intent);*/
        }
    }

    @Override
    public void onBackPressed() {
        if(isLookingAtStep){
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(masterFragmentContainer.getId(), masterRecipeFragment)
                    .commit();
            isLookingAtStep = false;
        }else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.step_recipe_key_fragment), recipe);
        outState.putInt(getString(R.string.step_index_key_fragment), stepIndex);
        outState.putBoolean(getString(R.string.step_is_on_step_boolean_key_fragment), isLookingAtStep);
        outState.putLong(getString(R.string.step_seek_key_activity), seekPos);


        Recipe savedRecipe = outState.getParcelable(getString(R.string.step_recipe_key_fragment));
    }

    @Override
    public void stepIndexChanged(int newIndex) {
        stepIndex = newIndex;
    }

    @Override
    public void videoSeekChanged(long seekPos) {
        this.seekPos = seekPos;
    }
}
