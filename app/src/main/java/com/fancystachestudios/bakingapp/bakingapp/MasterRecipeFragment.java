package com.fancystachestudios.bakingapp.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fancystachestudios.bakingapp.bakingapp.adapters.StepAdapter;
import com.fancystachestudios.bakingapp.bakingapp.customClasses.Recipe;
import com.fancystachestudios.bakingapp.bakingapp.customClasses.Step;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MasterRecipeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MasterRecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MasterRecipeFragment extends Fragment implements StepAdapter.StepClickListener{

    Context context;

    @BindView(R.id.step_layout)
    RecyclerView stepRecyclerView;

    Recipe recipe;
    ArrayList<Step> steps;
    StepAdapter.StepClickListener listener;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MasterRecipeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MasterRecipeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MasterRecipeFragment newInstance(String param1, String param2) {
        MasterRecipeFragment fragment = new MasterRecipeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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

        //recipe = startingIntent.getParcelableExtra(getString(R.string.recipe_pass_key));

        if(recipe == null || recipe.getSteps().size() == 0) {
            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
            return null;
        }

        listener = this;
        StepAdapter stepAdapter = new StepAdapter(context, recipe, listener);
        stepRecyclerView.setAdapter(stepAdapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        stepRecyclerView.setLayoutManager(layoutManager);

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onStepClick(int clickedItemIndex) {
        Intent intent;
        if(clickedItemIndex == 0){
            intent = new Intent(context, IngredientActivity.class);
        }else{
            intent = new Intent(context, StepActivity.class);
        }
        intent.putExtra(getString(R.string.recipe_pass_key), recipe);
        intent.putExtra(getString(R.string.step_number_key), clickedItemIndex);
        startActivity(intent);
    }

    public void setRecipe(Recipe recipe){
        this.recipe = recipe;
    }
}
