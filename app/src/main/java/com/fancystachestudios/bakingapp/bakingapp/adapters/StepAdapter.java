package com.fancystachestudios.bakingapp.bakingapp.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fancystachestudios.bakingapp.bakingapp.R;
import com.fancystachestudios.bakingapp.bakingapp.customClasses.Recipe;
import com.fancystachestudios.bakingapp.bakingapp.customClasses.Step;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.ViewHolder> {

    private Context context;
    private Recipe recipe;
    private ArrayList<Step> steps;
    private ArrayList<Object> mDataset;
    final private StepClickListener listener;

    public StepAdapter(Context context, Recipe recipe, StepClickListener listener) {
        this.context = context;
        this.recipe = recipe;
        this.steps = recipe.getSteps();
        this.listener = listener;
    }

    public interface StepClickListener{
        void onStepClick(int clickedItemIndex);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.step_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if(position == 0){
            holder.stepName.setText("Ingredients");
        }else{
        Step currStep = steps.get(position - 1);
        holder.stepName.setText(context.getString(R.string.step_name, position, currStep.getShortDescription()));
    }
    }

    @Override
    public int getItemCount() {
        return steps.size() + 1;
    }

    public class ViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.step_item_name)
        TextView stepName;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            listener.onStepClick(clickedPosition);
        }
    }

    public void setDataset(ArrayList<Step> newDataset){
        steps = newDataset;
        notifyDataSetChanged();
    }

}
