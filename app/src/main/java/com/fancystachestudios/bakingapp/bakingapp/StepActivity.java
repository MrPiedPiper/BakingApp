package com.fancystachestudios.bakingapp.bakingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.fancystachestudios.bakingapp.bakingapp.customClasses.Recipe;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepActivity extends AppCompatActivity {

    @BindView(R.id.step_player_layout)
    AspectRatioFrameLayout playerLayout;
    @BindView(R.id.step_player)
    SimpleExoPlayerView player;
    @BindView(R.id.step_details)
    TextView stepDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        ButterKnife.bind(this);

        Intent startedIntent = getIntent();
        Recipe recipe = startedIntent.getParcelableExtra(getString(R.string.recipe_pass_key));
        int stepIndex = startedIntent.getIntExtra(getString(R.string.step_number_key), 0);

        playerLayout.setAspectRatio(((float)16/9));

        if(recipe == null){
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show();
            return;
        }
        stepDetails.setText(recipe.getSteps().get(stepIndex).getDescription());

    }
}
