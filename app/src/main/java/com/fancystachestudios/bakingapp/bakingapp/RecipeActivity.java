package com.fancystachestudios.bakingapp.bakingapp;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.fancystachestudios.bakingapp.bakingapp.customClasses.Recipe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeActivity extends AppCompatActivity implements MasterRecipeFragment.onChooseStep, StepFragment.stepFragmentInterface {

    static final String FRAGMENT_STEPS = "steps";
    static final String FRAGMENT_STEP = "step";
    static final String FRAGMENT_INGREDIENTS = "ingredients";

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
    boolean playOnResume = false;

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


        FragmentManager fragmentManager = getSupportFragmentManager();
        masterRecipeFragment = new MasterRecipeFragment();
        stepFragment = new StepFragment();
        ingredientsFragment = new IngredientsFragment();

        /*if(fragmentManager.findFragmentByTag(FRAGMENT_STEPS) == null){
            masterRecipeFragment = new MasterRecipeFragment();
        }else{
            masterRecipeFragment = (MasterRecipeFragment) fragmentManager.findFragmentByTag(FRAGMENT_STEPS);
        }
        if(fragmentManager.findFragmentByTag(FRAGMENT_STEP)== null){
            stepFragment = new StepFragment();
        }else{
            stepFragment = (StepFragment) fragmentManager.findFragmentByTag(FRAGMENT_STEP);
        }
        if(fragmentManager.findFragmentByTag(FRAGMENT_INGREDIENTS)== null){
            ingredientsFragment = new IngredientsFragment();
        }else{
            ingredientsFragment = (IngredientsFragment) fragmentManager.findFragmentByTag(FRAGMENT_INGREDIENTS);
        }*/

        //Log.d("naputest", ""+(savedInstanceState == null));
        if(savedInstanceState != null){
            Recipe savedRecipe = savedInstanceState.getParcelable(getString(R.string.step_recipe_key_fragment));
            int savedIndex = savedInstanceState.getInt(getString(R.string.step_index_key_fragment));
            boolean savedIsLookingAtStep = savedInstanceState.getBoolean(getString(R.string.step_is_on_step_boolean_key_fragment));
            long savedSeekPos = savedInstanceState.getLong(getString(R.string.step_seek_key_activity));
            //boolean savedPlayOnResume = savedInstanceState.getBoolean(getString(R.string.step_play_on_resume_fragment));
            recipe = savedRecipe;
            stepIndex = savedIndex;
            isLookingAtStep = savedIsLookingAtStep;
            seekPos = savedSeekPos;
            //playOnResume = savedPlayOnResume;

            if(isSplitScreen){
                fragmentManager.beginTransaction()
                        .replace(masterFragmentContainer.getId(), masterRecipeFragment, FRAGMENT_STEPS)
                        .commit();
                if(stepIndex == 0){
                    fragmentManager.beginTransaction()
                            .replace(stepContainer.getId(), ingredientsFragment, FRAGMENT_INGREDIENTS)
                            .commit();
                }else{
                    stepFragment.setStepIndex(stepIndex);
                    fragmentManager.beginTransaction()
                            .replace(stepContainer.getId(), stepFragment, FRAGMENT_STEP)
                            .commit();
                }
            }else{
                if(!isLookingAtStep) {
                    fragmentManager.beginTransaction()
                            .replace(masterFragmentContainer.getId(), masterRecipeFragment, FRAGMENT_STEPS)
                            .commit();
                } else {
                    if(stepIndex == 0){
                        fragmentManager.beginTransaction()
                                .replace(masterFragmentContainer.getId(), ingredientsFragment, FRAGMENT_INGREDIENTS)
                                .commit();
                        isLookingAtStep = true;
                    }else{
                        stepFragment.setStepIndex(stepIndex);
                        fragmentManager.beginTransaction()
                                .replace(masterFragmentContainer.getId(), stepFragment, FRAGMENT_STEP)
                                .commit();
                        stepFragment.setStartPos(seekPos);
                        if(isLandscape){
                            getSupportActionBar().hide();
                            stepFragment.isFullScreen = true;
                            View decorView = getWindow().getDecorView();
                            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                            decorView.setSystemUiVisibility(uiOptions);
                        }
                    }
                }
            }

        }else{
            Intent startingIntent = getIntent();
            recipe = startingIntent.getParcelableExtra(getString(R.string.recipe_pass_key));

            masterRecipeFragment.setRecipe(recipe);
            stepFragment.setRecipe(recipe);
            stepFragment.setStartPos(seekPos);
            ingredientsFragment.setRecipe(recipe);

            if(isSplitScreen){
                fragmentManager.beginTransaction()
                        .replace(masterFragmentContainer.getId(), masterRecipeFragment, FRAGMENT_STEPS)
                        .commit();
                if(stepIndex == 0){
                    fragmentManager.beginTransaction()
                            .replace(stepContainer.getId(), ingredientsFragment, FRAGMENT_INGREDIENTS)
                            .commit();
                }else{
                    stepFragment.setStepIndex(stepIndex);
                    fragmentManager.beginTransaction()
                            .replace(stepContainer.getId(), stepFragment, FRAGMENT_STEP)
                            .commit();
                }
            }else{
                if(!isLookingAtStep) {
                    fragmentManager.beginTransaction()
                            .replace(masterFragmentContainer.getId(), masterRecipeFragment, FRAGMENT_STEPS)
                            .commit();
                } else {
                    if(stepIndex == 0){
                        fragmentManager.beginTransaction()
                                .replace(masterFragmentContainer.getId(), ingredientsFragment, FRAGMENT_INGREDIENTS)
                                .commit();
                        isLookingAtStep = true;
                    }else{
                        stepFragment.setStepIndex(stepIndex);
                        fragmentManager.beginTransaction()
                                .replace(masterFragmentContainer.getId(), stepFragment, FRAGMENT_STEP)
                                .commit();
                        stepFragment.setStartPos(seekPos);
                        if(isLandscape){
                            getSupportActionBar().hide();
                            stepFragment.isFullScreen = true;
                            View decorView = getWindow().getDecorView();
                            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                            decorView.setSystemUiVisibility(uiOptions);
                        }
                    }
                }
            }
        }

    }


    @Override
    public void stepChosen(int stepIndex) {
        for(int i = 0; i < getSupportFragmentManager().getFragments().size(); i++){
            Log.d("naputest", "Fragment "+(i+1)+" is "+getSupportFragmentManager().getFragments().get(i).getClass());
        }
        this.stepIndex = stepIndex;
        if(isSplitScreen){
            if (stepIndex == 0){
                if(findViewById(R.id.step_details) != null){
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(stepContainer.getId(), ingredientsFragment, FRAGMENT_INGREDIENTS)
                            .commit();
                }
            }else{
                if(findViewById(R.id.step_details) == null){
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(stepContainer.getId(), stepFragment, FRAGMENT_STEP)
                            .commit();
                }
                stepFragment.setStepIndex(stepIndex);
            }
        }else if(stepIndex == 0){
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(masterFragmentContainer.getId(), ingredientsFragment, FRAGMENT_INGREDIENTS)
                    .commit();
            isLookingAtStep = true;
        }else{
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(masterFragmentContainer.getId(), stepFragment, FRAGMENT_STEP)
                    .commit();
            stepFragment.setStepIndex(stepIndex);
            isLookingAtStep = true;
            stepFragment.setStartPos(seekPos);
            if(isLandscape){
                getSupportActionBar().hide();
                stepFragment.isFullScreen = true;
                View decorView = getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(isSplitScreen){
            super.onBackPressed();
        }else if(isLookingAtStep){
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(masterFragmentContainer.getId(), masterRecipeFragment)
                    .commit();
            isLookingAtStep = false;
            if(isLandscape){
                getSupportActionBar().show();
                stepFragment.isFullScreen = false;
                View decorView = getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
                decorView.setSystemUiVisibility(uiOptions);
            }
        }else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.step_recipe_key_fragment), recipe);
        outState.putInt(getString(R.string.step_index_key_fragment), stepIndex);
        outState.putLong(getString(R.string.step_seek_key_activity), seekPos);
        outState.putBoolean(getString(R.string.step_is_on_step_boolean_key_fragment), isLookingAtStep);
        //outState.putBoolean(getString(R.string.step_play_on_resume_fragment), playOnResume);
    }

    @Override
    public void stepIndexChanged(int newIndex) {
        if (stepIndex > 0){
        stepIndex = newIndex;
        }
    }

    @Override
    public void videoSeekChanged(long seekPos) {
        this.seekPos = seekPos;
    }

    @Override
    public void playOnResumeChanged(boolean playOnResume) {
        this.playOnResume = playOnResume;
    }
}
