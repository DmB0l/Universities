package com.example.universities.uniFilter;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.universities.R;
import com.example.universities.base.BaseActivity;
import com.example.universities.db.DatabaseHelperUniversities;
import com.example.universities.uniList.UniversitiesList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class create filters for search universities as your preferences
 */
public class FilterUniversities extends BaseActivity {
    private AutoCompleteTextView autoCompleteTextView;
    private CheckBox checkBoxBakalavr;
    private CheckBox checkBoxSpicialitet;
    private CheckBox checkBoxMagistr;
    private CheckBox checkBoxHostel;
    private EditText editTextMinRating;
    private Button buttonFindUni;

    private List<String> professions;

    private DatabaseHelperUniversities mDBHelper;
    private SQLiteDatabase mDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initialAutoCompleteTextView();

        checkBoxListener checkBoxListener = new checkBoxListener();
        checkBoxBakalavr.setOnCheckedChangeListener(checkBoxListener);
        checkBoxSpicialitet.setOnCheckedChangeListener(checkBoxListener);
        checkBoxMagistr.setOnCheckedChangeListener(checkBoxListener);

    }

    private void init() {
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        checkBoxBakalavr = findViewById(R.id.checkBoxBakalavr);
        checkBoxSpicialitet = findViewById(R.id.checkBoxSpicialitet);
        checkBoxMagistr = findViewById(R.id.checkBoxMagistr);
        checkBoxHostel = findViewById(R.id.checkBoxHostel);
        editTextMinRating = findViewById(R.id.editTextMinRating);
        buttonFindUni = findViewById(R.id.buttonFindUni);

        mDBHelper = new DatabaseHelperUniversities(this);
        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }
        mDb = mDBHelper.getReadableDatabase();
    }

    private void initialAutoCompleteTextView() {
        Cursor cursor = mDb.query("universities", new String[]{"professions"},
                null, null, null, null, null);
        cursor.moveToFirst();

        professions = new ArrayList<>();

        while (!cursor.isAfterLast()) {
            String string = cursor.getString(0);

            string = changeStringsWithCommaToSemicolon(string);

            String[] strings = string.split(";");

            for (String str : strings) {
                str = str.toLowerCase().trim();
                if (!professions.contains(str))
                    professions.add(str);
            }
            cursor.moveToNext();
        }
        cursor.close();

        autoCompleteTextView.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, professions));
    }

    public void findButtonClickListener(View view) {
        String profession = autoCompleteTextView.getText().toString();

        if(profession.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Выберите профессию", Toast.LENGTH_SHORT).show();
        }
        else if (!professions.contains(profession)) {
            Toast.makeText(getApplicationContext(), "Выберите профессию из списка", Toast.LENGTH_SHORT).show();
        }
        else{
            Intent intent = new Intent(getApplicationContext(), UniversitiesList.class);

            intent.putExtra("profession", profession);
            intent.putExtra("directionBak", checkBoxBakalavr.isChecked());
            intent.putExtra("directionSpec", checkBoxSpicialitet.isChecked());
            intent.putExtra("directionMag", checkBoxMagistr.isChecked());
            intent.putExtra("hostel", checkBoxHostel.isChecked());
            intent.putExtra("rate", editTextMinRating.getText().toString());
            startActivity(intent);
        }
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_filter_universities;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.home;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDb.close();
        mDBHelper.close();
    }

    private class checkBoxListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                if (buttonView.getId() == checkBoxBakalavr.getId()) {
                    checkBoxSpicialitet.setChecked(false);
                    checkBoxMagistr.setChecked(false);
                }
                if (buttonView.getId() == checkBoxMagistr.getId()) {
                    checkBoxSpicialitet.setChecked(false);
                    checkBoxBakalavr.setChecked(false);
                }
                if (buttonView.getId() == checkBoxSpicialitet.getId()) {
                    checkBoxBakalavr.setChecked(false);
                    checkBoxMagistr.setChecked(false);
                }
            }
        }
    }
}