package com.thaid.asylum;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.thaid.asylum.api.APIClient;
import com.thaid.asylum.api.APIError;
import com.thaid.asylum.api.ResponseListener;
import com.thaid.asylum.api.requests.GetUserInfoRequest;

public class MainActivity extends AppCompatActivity {

    Fragment fragment1;
    Fragment fragment2;
    Fragment fragment3;
    FragmentManager fm;
    Fragment active;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                    return true;

                case R.id.navigation_dashboard:
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active = fragment2;
                    return true;

                case R.id.navigation_notifications:
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;
                    return true;
            }
            return false;
        }
    };

    View vieww;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        vieww = findViewById(R.id.main_container_coordinator);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        fragment1 = new BlindsFragment();
        fragment2 = new GateFragment();
        fragment3 = new PowerFragment();
        fm = getSupportFragmentManager();
        active = fragment1;

        fm.beginTransaction().add(R.id.main_container, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.main_container, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.main_container,fragment1, "1").commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.main_menu_action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if(id == R.id.main_menu_action_refresh) {
            final Context currContext = this;

            APIClient apiClient = APIClient.getInstance(currContext);
            apiClient.sendRequest(new GetUserInfoRequest(), new ResponseListener<GetUserInfoRequest.GetUserInfoModel>() {
                @Override
                public void onSuccess(GetUserInfoRequest.GetUserInfoModel data) {
                    Snackbar snackbar = Snackbar
                            .make(vieww, "Zalogowano jako " + data.getName(), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }

                @Override
                public void onError(APIError error) {
                    Snackbar snackbar = Snackbar
                            .make(vieww, getString(error.getTranslationId()), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            });

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
