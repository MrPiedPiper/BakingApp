package com.fancystachestudios.bakingapp.bakingapp;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import com.fancystachestudios.bakingapp.bakingapp.customClasses.Recipe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepActivity extends AppCompatActivity implements StepFragment.stepFragmentInterface {

    ScrollView scrollView;

    private Recipe recipe;
    private int stepIndex;
    private long seekPos = 0;
    boolean isLandscape = false;

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

        if(getResources().getConfiguration().orientation == getResources().getConfiguration().ORIENTATION_LANDSCAPE) {
            isLandscape = true;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        StepFragment stepFragment = new StepFragment();
        stepFragment.setRecipe(recipe);
        stepFragment.setStepIndex(stepIndex);
        stepFragment.setStartPos(seekPos);
        fragmentManager.beginTransaction()
                .add(R.id.step_fragment_container, stepFragment)
                .commit();

        if(isLandscape) {
            //SetOnTouchListener idea by lily from https://stackoverflow.com/questions/5763304/disable-scrollview-programmatically

//TODO the problem, You need to swap back to the recipeActivity when you rotate from the StepActivity. Or maybe, just use the step fragment in the recipeactivity!

            getSupportActionBar().hide();
            scrollView = findViewById(R.id.step_scrollview);
            scrollView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });
        }
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

    @Override
    public void playOnResumeChanged(boolean playOnResume) {


    }
}
