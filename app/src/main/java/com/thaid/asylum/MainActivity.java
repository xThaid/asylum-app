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
import android.widget.ListView;

import com.thaid.asylum.Blinds.BlindsAdapter;
import com.thaid.asylum.Blinds.BlindsFragment;
import com.thaid.asylum.api.APIClient;
import com.thaid.asylum.api.APIError;
import com.thaid.asylum.api.ResponseListener;
import com.thaid.asylum.api.requests.GetUserInfoRequest;

public class MainActivity extends AppCompatActivity {

    private Fragment energyFragment;
    private Fragment blindsFragment;
    private Fragment meteoFragment;
    private FragmentManager fragmentManager;
    private Fragment activeFragment;
    private View container;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_energy:
                    fragmentManager.beginTransaction().hide(activeFragment).show(energyFragment).commit();
                    activeFragment = energyFragment;
                    return true;

                case R.id.navigation_shutter:
                    fragmentManager.beginTransaction().hide(activeFragment).show(blindsFragment).commit();
                    activeFragment = blindsFragment;
                    return true;

                case R.id.navigation_meteo:
                    fragmentManager.beginTransaction().hide(activeFragment).show(meteoFragment).commit();
                    activeFragment = meteoFragment;
                    return true;

                case R.id.navigation_camera:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        setSupportActionBar(toolbar);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        container = findViewById(R.id.main_container_coordinator);

        energyFragment = new EnergyFragment();
        blindsFragment = new BlindsFragment();
        meteoFragment = new MeteoFragment();
        activeFragment = energyFragment;

        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().add(R.id.main_container, meteoFragment, "3").hide(meteoFragment).commit();
        fragmentManager.beginTransaction().add(R.id.main_container, blindsFragment, "2").hide(blindsFragment).commit();
        fragmentManager.beginTransaction().add(R.id.main_container,energyFragment, "1").commit();

        APIClient.Initialize(this);
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
            APIClient apiClient = APIClient.getInstance();
            apiClient.sendRequest(new GetUserInfoRequest(), new ResponseListener<GetUserInfoRequest.GetUserInfoModel>() {
                @Override
                public void onSuccess(GetUserInfoRequest.GetUserInfoModel data) {
                    Snackbar snackbar = Snackbar
                            .make(container, "Zalogowano jako " + data.getName(), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }

                @Override
                public void onError(APIError error) {
                    Snackbar snackbar = Snackbar
                            .make(container, getString(error.getTranslationId()), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            });

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
