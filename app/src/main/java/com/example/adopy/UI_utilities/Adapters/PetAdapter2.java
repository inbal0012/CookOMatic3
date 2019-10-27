package com.example.adopy.UI_utilities.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.adopy.R;
import com.example.adopy.Utilities.Models.PetModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class PetAdapter2 extends RecyclerView.Adapter<PetAdapter2.ViewHolder> {
    private Context context;
    private ArrayList<PetModel> petModels;

    public PetAdapter2(Context context, ArrayList<PetModel> petModels) {
        this.context = context;
        this.petModels = petModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_pet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PetModel petModel = petModels.get(position);

        holder.petName.setText(petModel.getName());
        holder.petImage.setImageBitmap(petModel.getBitmap());
        holder.petAge.setText(petModel.getAge().toString());
    }

    @Override
    public int getItemCount() {
        return petModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView petName;
        private CircleImageView petImage;
        private TextView petAge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            petName = itemView.findViewById(R.id.pet_name);
            petImage = itemView.findViewById(R.id.pet_image);
            petAge = itemView.findViewById(R.id.publish_date);


        }
    }
}
