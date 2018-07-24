package com.fancystachestudios.bakingapp.bakingapp;

import android.content.Intent;
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

public class RecipeActivity extends AppCompatActivity implements StepAdapter.StepClickListener{

    @BindView(R.id.step_layout)
    RecyclerView stepRecyclerView;

    Recipe recipe;
    ArrayList<Step> steps;
    StepAdapter.StepClickListener listener;

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

        setTitle(recipe.getName());

        listener = this;
        StepAdapter stepAdapter = new StepAdapter(this, recipe, listener);
        stepRecyclerView.setAdapter(stepAdapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        stepRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onStepClick(int clickedItemIndex) {
        Intent intent;
        intent = new Intent(this, IngredientActivity.class);
        /*if(clickedItemIndex == 0){
            intent = new Intent(this, IngredientActivity.class);
        }else{
            //intent = new Intent(this, StepActivity.class);
        }*/
        intent.putExtra(getString(R.string.recipe_pass_key), recipe);
        startActivity(intent);
    }
}
