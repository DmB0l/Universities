package com.example.universities.uniList;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.universities.R;
import com.example.universities.uniInfo.UniversityInfo;
import com.example.universities.base.BaseActivity;
import com.example.universities.db.DatabaseHelperUniversities;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Display all universities from database
 */
public class UniversitiesList extends BaseActivity {
    private DatabaseHelperUniversities mDBHelper;
    private SQLiteDatabase mDb;
    private ListView listView;
    private SearchView searchView;
    private ArrayList<University> universitiesList;
    private TextView noneFavourites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        Bundle arguments = getIntent().getExtras();
        new StartPage().execute(arguments);

        UniversityAdapter universityAdapter = new UniversityAdapter(this, R.layout.list_row, universitiesList);
        listView.setAdapter(universityAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(UniversitiesList.this, UniversityInfo.class);
                University uni = (University) adapterView.getItemAtPosition(position);

                Cursor cursor1 = mDb.rawQuery("select _id from universities where name = ?", new String[]{uni.getName()});
                cursor1.moveToFirst();
                position = cursor1.getInt(0) - 1;
                cursor1.close();

                intent.putExtra("position", position);
                Log.i("app", "position: " + position);
                startActivity(intent);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                universityAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void init() {
        listView = findViewById(R.id.listViewUni);
        searchView = findViewById(R.id.searchView);
        noneFavourites = findViewById(R.id.noneFavourites);
        universitiesList = new ArrayList<>();

        mDBHelper = new DatabaseHelperUniversities(this);
        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }
        mDb = mDBHelper.getReadableDatabase();
    }

    private class StartPage extends AsyncTask<Bundle, Void, String> {

        protected String doInBackground(Bundle... bundles) {
            Bundle arguments = bundles[0];
            if (arguments == null || arguments.isEmpty())
                usualPage();
            else if (arguments.size() > 2)
                filterPage(arguments);
            else {
                if (arguments.get("favourites") == null)
                    interviewPage(arguments);
                else
                    favouritesPage(arguments);
            }
            return null;
        }
    }

    private void usualPage() {
        Cursor cursor = mDb.query("universities", new String[]{"name", "image", "phones"},
                null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            universitiesList.add(new University(cursor.getString(1), cursor.getString(0), cursor.getString(2)));
            cursor.moveToNext();
        }
        cursor.close();
    }

    private void filterPage(Bundle arguments) {
        Cursor cursor = mDb.query("universities", new String[]{"name", "image", "phones",
                        "professions", "namesbak", "namespec", "namemag", "hostel"},
                null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            String string = cursor.getString(3);

            string = changeStringsWithCommaToSemicolon(string);

            String[] strings = string.split(";");

            for (String str : strings) {
                str = str.toLowerCase().trim();
                if (arguments.getString("profession").equals(str)) {

                    if (arguments.getBoolean("hostel")) {
                        if (cursor.getString(7).equals("нет")) {
                            break;
                        }
                    }

                    if (arguments.getBoolean("directionBak")) {
                        if (cursor.getString(4).length() <= 2) {
                            break;
                        }
                    }

                    if (arguments.getBoolean("directionSpec")) {
                        if (cursor.getString(5).length() <= 2) {
                            break;
                        }
                    }

                    if (arguments.getBoolean("directionMag")) {
                        if (cursor.getString(6).length() <= 2) {
                            break;
                        }
                    }

                    universitiesList.add(new University(cursor.getString(1), cursor.getString(0), cursor.getString(2)));
                    break;
                }
            }
            cursor.moveToNext();
        }
        cursor.close();
    }

    private void interviewPage(Bundle arguments) {
        Cursor cursor = mDb.query("universities", new String[]{"name", "image", "phones",
                        "professions"},
                null, null, null, null, null);
        cursor.moveToFirst();

        String[] professions1 = arguments.getStringArray("professions1");
        String[] professions2 = arguments.getStringArray("professions2");


        while (!cursor.isAfterLast()) {
            String string = cursor.getString(3);

            string = changeStringsWithCommaToSemicolon(string);

            String[] strings = string.split(";");

            m:
            for (String str : strings) {
                str = str.toLowerCase().trim();
                for (String prof : professions1) {
                    prof = prof.toLowerCase().trim();
                    if (prof.equals(str)) {
                        universitiesList.add(new University(cursor.getString(1), cursor.getString(0), cursor.getString(2)));
                        break m;
                    }
                }
                if (professions2 != null && professions2.length != 0) {
                    for (String prof : professions2) {
                        prof = prof.toLowerCase().trim();
                        if (prof.equals(str)) {
                            universitiesList.add(new University(cursor.getString(1), cursor.getString(0), cursor.getString(2)));
                            break m;
                        }
                    }
                }
            }

            cursor.moveToNext();
        }
        cursor.close();
    }

    private void favouritesPage(Bundle arguments) {
        String[] favourites = arguments.getStringArray("favourites");

        if (favourites.length == 0) {
            searchView.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            noneFavourites.setVisibility(View.VISIBLE);
        } else {
            Cursor cursor = mDb.query("universities", new String[]{"name", "image", "phones"},
                    null, null, null, null, null);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                String name = cursor.getString(0);

                for(int i = 0; i < favourites.length; i++) {
                    if(favourites[i].equals(deleteErrorStringsFromString(name))) {
                        universitiesList.add(new University(cursor.getString(1), cursor.getString(0), cursor.getString(2)));
                        break;
                    }
                }

                cursor.moveToNext();
            }
            cursor.close();
        }
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_universities_list;
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

}