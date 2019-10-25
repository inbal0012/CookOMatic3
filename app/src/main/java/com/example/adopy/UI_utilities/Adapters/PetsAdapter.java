package com.example.adopy.UI_utilities.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.adopy.R;
import com.example.adopy.Utilities.Interfaces_and_Emuns.CardListener;
import com.example.adopy.Utilities.Models.PetModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class PetsAdapter extends RecyclerView.Adapter<PetsAdapter.ViewHolder> {


    List<PetModel> petModelList;
    private CardListener cardListener;
    private Context context;

    public void setCardListener(CardListener cardListener) {
        this.cardListener = cardListener;
    }

    public PetsAdapter(List<PetModel> petModelList, Context context) {
        this.petModelList = petModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_pet,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        PetModel petModel = petModelList.get(i);
        if(!petModel.getBitmap().toString().isEmpty()){
            Glide.with(context).load(petModel.getBitmap()).placeholder(R.drawable.foot).into(viewHolder.petImage);
        }

        viewHolder.petName.setText(petModel.getName());
        //viewHolder.publish_Date.setText(petModel.getDate());
        viewHolder.pet_price.setText(petModel.getPrice());

    }

    @Override
    public int getItemCount() {
        return petModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView petName;
        private CircleImageView petImage;
        private TextView publish_Date;
        private TextView pet_price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            petImage = itemView.findViewById(R.id.pet_image);
            petName = itemView.findViewById(R.id.pet_name);
            publish_Date = itemView.findViewById(R.id.publish_date);
            pet_price = itemView.findViewById(R.id.pet_price);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    cardListener.onCardClicked(getAdapterPosition(),v);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    cardListener.onCardLongClicked(getAdapterPosition(),v);
                    return true;
                }
            });

        }
    }
}
