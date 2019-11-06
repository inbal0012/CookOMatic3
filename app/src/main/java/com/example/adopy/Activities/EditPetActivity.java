package com.example.adopy.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.adopy.R;
import com.isapanah.awesomespinner.AwesomeSpinner;

public class EditPetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pet);

        AwesomeSpinner kindSpinner = findViewById(R.id.kindSpinner);
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this, R.array.type_array, android.R.layout.simple_spinner_item);
        kindSpinner.setAdapter(typeAdapter, 0);
        kindSpinner.setOnSpinnerItemClickListener(new AwesomeSpinner.onSpinnerItemClickListener<String>() {
            @Override
            public void onItemSelected(int position, String itemAtPosition) {
                //TODO YOUR ACTIONS
                Toast.makeText(EditPetActivity.this, itemAtPosition+" selected", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
