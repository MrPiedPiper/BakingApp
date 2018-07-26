package com.fancystachestudios.bakingapp.bakingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.fancystachestudios.bakingapp.bakingapp.adapters.IngredientAdapter;
import com.fancystachestudios.bakingapp.bakingapp.customClasses.Recipe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientActivity extends AppCompatActivity {

    @BindView(R.id.ingredient_recyclerview)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient);
        ButterKnife.bind(this);

        Intent startingIntent = getIntent();
        Recipe recipe = startingIntent.getParcelableExtra(getString(R.string.recipe_pass_key));
        IngredientAdapter adapter = new IngredientAdapter(this, recipe);
        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }
}
