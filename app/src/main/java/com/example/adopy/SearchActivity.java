package com.example.adopy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.example.adopy.UI_utilities.Adapters.PetsAdapter;
import com.example.adopy.Utilities.Interfaces_and_Emuns.CardListener;
import com.example.adopy.Utilities.Interfaces_and_Emuns.Gender;
import com.example.adopy.Utilities.Models.PetModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "my_SearchActivity";

    private RecyclerView recyclerView;
    private PetsAdapter petsAdapter;
    private List<PetModel> petModelArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        GridLayoutManager linearLayoutManager = new GridLayoutManager(this,2);
        petModelArrayList = new ArrayList<>();
        petsAdapter = new PetsAdapter(petModelArrayList, this);

        recyclerView = findViewById(R.id.recycler_home);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(petsAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        petsAdapter.setCardListener(new CardListener() {
            @Override
            public void onCardClicked(int position, View view) {
                PetModel pet = petModelArrayList.get(position);
                String str = "name: " + pet.getName() + "\nage: " + pet.getAge() + "\ntype: " + pet.getKind() + "\ngender: " + pet.getGender();
                Log.d(TAG, "onCardClicked: selected: \n" + str);
                Toast.makeText(SearchActivity.this, "selected: \n" + str, Toast.LENGTH_SHORT).show();
//                Gson gson = new Gson();
//                Intent intent = new Intent(SearchActivity.this, PetPageActivity.class);
//                intent.putExtra("pet", gson.toJson(pet));
//                startActivity(intent);

            }

            @Override
            public void onCardLongClicked(int position, View view) {

            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return 0;
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                PetModel petModel = petModelArrayList.get(i);
                //PopUpWindow(petModel,i);


            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);

        fillPetList();
    }

    private void fillPetList() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dog);
        petModelArrayList.add(new PetModel("dog", "nero", 4.2, Gender.Male, BitmapFactory.decodeResource(getResources(), R.drawable.dog)));
        petModelArrayList.add(new PetModel("cat", "laila", 2.7, Gender.Female, BitmapFactory.decodeResource(getResources(), R.drawable.cat)));
        petModelArrayList.add(new PetModel("cat", "nobles", 3.5, Gender.Male, BitmapFactory.decodeResource(getResources(), R.drawable.cat)));
        petModelArrayList.add(new PetModel("dog", "menesh", 0.9, Gender.Female, BitmapFactory.decodeResource(getResources(), R.drawable.dog)));
        petModelArrayList.add(new PetModel("iguana", "gourg", 0.5, Gender.Female, BitmapFactory.decodeResource(getResources(), R.drawable.iguana)));
        petModelArrayList.add(new PetModel("turtle", "slowy", 10.0, Gender.Male, BitmapFactory.decodeResource(getResources(), R.drawable.turtle)));
        petModelArrayList.add(new PetModel("chinchilla", "archy", 5.0, Gender.Male, BitmapFactory.decodeResource(getResources(), R.drawable.chinchilla)));
        petModelArrayList.add(new PetModel("rabbit", "carrot", 6.9, Gender.Female, BitmapFactory.decodeResource(getResources(), R.drawable.rabbit)));

        petsAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //petsAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }
}
