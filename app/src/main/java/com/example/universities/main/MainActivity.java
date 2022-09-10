package com.example.universities.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.universities.uniFilter.FilterUniversities;
import com.example.universities.profession.ProfessionalOrientation;
import com.example.universities.R;
import com.example.universities.uniList.UniversitiesList;
import com.example.universities.base.BaseActivity;

/**
 * This class is Home page on app
 */
public class MainActivity extends BaseActivity {
    private Button uniListButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uniListButton = findViewById(R.id.uni_list_button);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.home;
    }

    public void uniListButtonActionListener(View v) {
        Intent intent = new Intent(this, UniversitiesList.class);
        startActivity(intent);
    }

    public void filterButtonActionListener(View v) {
        Intent intent = new Intent(this, FilterUniversities.class);
        startActivity(intent);
    }

    public void professionButtonActionListener(View v) {
        Intent intent = new Intent(this, ProfessionalOrientation.class);
        startActivity(intent);
    }
}