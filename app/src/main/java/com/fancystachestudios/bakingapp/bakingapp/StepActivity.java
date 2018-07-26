package com.fancystachestudios.bakingapp.bakingapp;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fancystachestudios.bakingapp.bakingapp.customClasses.Recipe;

public class StepActivity extends AppCompatActivity implements StepFragment.stepFragmentInterface {

    private Recipe recipe;
    private int stepIndex;
    private long seekPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        if(savedInstanceState == null){
            Intent startedIntent = getIntent();
            recipe = startedIntent.getParcelableExtra(getString(R.string.recipe_pass_key));
            stepIndex = startedIntent.getIntExtra(getString(R.string.step_number_key), 0);
        }else{
            recipe = savedInstanceState.getParcelable(getString(R.string.step_recipe_key_activity));
            stepIndex = savedInstanceState.getInt(getString(R.string.step_index_key_activity));
            seekPos = savedInstanceState.getLong(getString(R.string.step_seek_key_activity));
        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        StepFragment stepFragment = new StepFragment();
        stepFragment.setRecipe(recipe);
        stepFragment.setStepIndex(stepIndex);
        stepFragment.setStartPos(seekPos);
        fragmentManager.beginTransaction()
                .add(R.id.step_fragment_container, stepFragment)
                .commit();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.step_recipe_key_activity), recipe);
        outState.putInt(getString(R.string.step_index_key_activity), stepIndex);
        outState.putLong(getString(R.string.step_seek_key_activity), seekPos);

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
