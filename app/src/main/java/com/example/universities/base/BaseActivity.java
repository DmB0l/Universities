package com.example.universities.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.universities.autorization.Authorization;
import com.example.universities.main.MainActivity;
import com.example.universities.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.jetbrains.annotations.NotNull;

/**
 * Parent class for all Activities
 */
public abstract class BaseActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener{

    protected BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());

        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                startActivity(new Intent(this, MainActivity.class));
            } else if (itemId == R.id.person) {
                startActivity(new Intent(this, Authorization.class));
            }
            finish();
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateNavigationBarState();
    }

    private void updateNavigationBarState(){
        int actionId = getNavigationMenuItemId();
        selectBottomNavigationBarItem(actionId);
    }

    void selectBottomNavigationBarItem(int itemId) {
        MenuItem item = navigationView.getMenu().findItem(itemId);
        item.setChecked(true);
    }

    protected String changeStringsWithCommaToSemicolon(String string) {
        while (string.contains(",")) {
            string = string.substring(0, string.indexOf(",")).concat("; ").concat(string.substring(string.indexOf(",") + 1));
        }
        return string;
    }

    protected String deleteErrorStringsFromString(String str) {
        str = str.substring(1, str.length() - 1);
        String[] errorStrings = new String[]{".", "#", "$", "[", "]"};
        for (int i = 0; i < errorStrings.length; i++) {
            if (str.contains(errorStrings[i])) {
                str = str.substring(0, str.indexOf(errorStrings[i])).concat(" ").concat(str.substring(str.indexOf(errorStrings[i]) + 1));
                i--;
            }
        }
        return str;
    }

    protected String deleteErrorStringsFromString2(String str) {
        String[] errorStrings = new String[]{".", "#", "$", "[", "]"};
        for (int i = 0; i < errorStrings.length; i++) {
            if (str.contains(errorStrings[i])) {
                str = str.substring(0, str.indexOf(errorStrings[i])).concat(" ").concat(str.substring(str.indexOf(errorStrings[i]) + 1));
                i--;
            }
        }
        return str;
    }

    protected abstract int getContentViewId();

    protected abstract int getNavigationMenuItemId();

}

