package com.fancystachestudios.bakingapp.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fancystachestudios.bakingapp.bakingapp.adapters.StepAdapter;
import com.fancystachestudios.bakingapp.bakingapp.customClasses.Recipe;
import com.fancystachestudios.bakingapp.bakingapp.customClasses.Step;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MasterRecipeFragment extends Fragment implements StepAdapter.StepClickListener{

    Context context;

    @BindView(R.id.step_layout)
    RecyclerView stepRecyclerView;

    static Recipe recipe;
    ArrayList<Step> steps;
    StepAdapter.StepClickListener listener;

    private onChooseStep mListener;

    public MasterRecipeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //getActivity().setTitle(recipe.getName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_master_recipe, container, false);
        ButterKnife.bind(this, rootView);

        if(recipe == null || recipe.getSteps().size() == 0) {
            Toast.makeText(context, "Something went wrong! (MasterRecipeFragment)", Toast.LENGTH_LONG).show();
            return null;
        }

        listener = this;
        StepAdapter stepAdapter = new StepAdapter(context, recipe, listener);
        stepRecyclerView.setAdapter(stepAdapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        stepRecyclerView.setLayoutManager(layoutManager);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof onChooseStep) {
            mListener = (onChooseStep) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onChooseStep");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface onChooseStep {
        void stepChosen(int stepIndex);
    }

    @Override
    public void onStepClick(int clickedItemIndex) {
        if (mListener != null) {
            mListener.stepChosen(clickedItemIndex);
        }
    }

    public void setRecipe(Recipe recipe){
        this.recipe = recipe;
    }
}
