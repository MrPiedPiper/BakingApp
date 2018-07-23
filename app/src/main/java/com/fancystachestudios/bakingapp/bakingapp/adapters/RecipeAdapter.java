package com.fancystachestudios.bakingapp.bakingapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fancystachestudios.bakingapp.bakingapp.R;
import com.fancystachestudios.bakingapp.bakingapp.customClasses.Recipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Recipe> mDataset;
    final private RecipeClickListener listener;

    public RecipeAdapter(Context context, ArrayList<Recipe> mDataset, RecipeClickListener listener) {
        this.context = context;
        this.mDataset = mDataset;
        this.listener = listener;
    }

    public interface RecipeClickListener{
        void onRecipeClick(int clickedItemIndex);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe currRecipe = mDataset.get(position);

        if(!currRecipe.getImage().isEmpty()){
            Picasso.get().load(currRecipe.getImage()).into(holder.recipeImage);
        }
        Log.d("naputest", currRecipe.getName());
        holder.recipeName.setText(currRecipe.getName());

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.recipe_item_name)
        TextView recipeName;

        @BindView(R.id.recipe_item_image)
        ImageView recipeImage;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            listener.onRecipeClick(clickedPosition);
        }
    }

    public void setDataset(ArrayList<Recipe> newDataset){
        mDataset = newDataset;
        notifyDataSetChanged();
    }
}
