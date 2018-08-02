package com.fancystachestudios.bakingapp.bakingapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fancystachestudios.bakingapp.bakingapp.R;
import com.fancystachestudios.bakingapp.bakingapp.customClasses.Ingredient;
import com.fancystachestudios.bakingapp.bakingapp.customClasses.Recipe;
import com.fancystachestudios.bakingapp.bakingapp.customClasses.Step;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    private Context context;
    private Recipe recipe;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<Object> mDataset;

    public IngredientAdapter(Context context, Recipe recipe) {
        this.context = context;
        this.recipe = recipe;
        this.ingredients = recipe.getIngredients();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.textview_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient currIngredient = ingredients.get(position);
        double quantity = currIngredient.getQuantity();
        String measure = currIngredient.getMeasure();
        String ingredient = currIngredient.getIngredient();
        if(quantity == (int) quantity){
            if(quantity == 1){
                holder.stepName.setText(context.getString(R.string.ingredient_one_or_less, (int) quantity, measure, ingredient));
            }else{
                holder.stepName.setText(context.getString(R.string.ingredient_other, (int) quantity, measure, ingredient));
            }
        }else{
            if(quantity < 1){
                holder.stepName.setText(context.getString(R.string.ingredient_one_or_less, quantity, measure, ingredient));
            }else{
                holder.stepName.setText(context.getString(R.string.ingredient_other, quantity, measure, ingredient));
            }
        }
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder{

        @BindView(R.id.textview_item_text)
        TextView stepName;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setDataset(ArrayList<Ingredient> newDataset){
        ingredients = newDataset;
        notifyDataSetChanged();
    }

}
