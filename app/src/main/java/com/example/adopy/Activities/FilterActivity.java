package com.example.adopy.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.adopy.R;
import com.example.adopy.UI_utilities.MultipleSelectionSpinner;
import com.example.adopy.Utilities.Models.SearchPreferences;
import com.jaygoo.widget.RangeSeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class FilterActivity extends AppCompatActivity {

    private static final String TAG = "my_FilterActivity";

    //MultipleSelection spinner object
    MultipleSelectionSpinner typeSpinner;

    //List which hold multiple selection spinner values
    List<String> typeSpinnerList = new ArrayList<>();

    RangeSeekBar ageSeekbar;
    RangeSeekBar distanceSeekbar;
    MultiStateToggleButton sex_mstb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //typeSpinner START
        //casting of spinner
        typeSpinner = findViewById(R.id.typeSpinner);

        //adding items to typeSpinnerList TODO other
        typeSpinnerList = Arrays.asList( getResources().getStringArray(R.array.type_array) );

        //set items to spinner from typeSpinnerList
        typeSpinner.setItems(typeSpinnerList);
        //typeSpinner END

        ageSeekbar = findViewById(R.id.ageSeekBar);
        ageSeekbar.setIndicatorTextDecimalFormat("0");
        ageSeekbar.setProgress(0, 25);

        distanceSeekbar = findViewById(R.id.distanceSeekBar);
        distanceSeekbar.setIndicatorTextDecimalFormat("0");
        distanceSeekbar.setProgress(100);

        sex_mstb = findViewById(R.id.sex_mstb);
        sex_mstb.setValue(1);

        Button saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePreferences();
            }
        });

        Button cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "canceled");
                FilterActivity.this.finish();
            }
        });
    }

    private void savePreferences() {
        SearchPreferences sp = new SearchPreferences();
        sp.setTypes(typeSpinner.getSelectedStrings());
        sp.setAgeMin(Math.round(ageSeekbar.getLeftSeekBar().getProgress()));
        sp.setAgeMax(Math.round(ageSeekbar.getRightSeekBar().getProgress()));
        String[] sexEng = new String[]{"Male", "Doesn't matter", "Female"};
        sp.setSex(sexEng[sex_mstb.getValue()]);
        sp.setDistance(Math.round(distanceSeekbar.getLeftSeekBar().getProgress()));

        String ans = "types: " + sp.getTypes() + "\nage: " + sp.getAgeMin() + " - " + sp.getAgeMax() + "\nSex: " + sp.getSex() + "\nDistance: " + sp.getDistance();
        Log.d(TAG, "savePreferences: \n" + ans);
        Toast.makeText(this, "" + ans, Toast.LENGTH_SHORT).show();

        Intent intent = getIntent();
        intent.putExtra("key", sp);
        setResult(RESULT_OK, intent);

        FilterActivity.this.finish();
    }
}
