package com.movies.bruno.udacity.popularmovies;

import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import java.util.ArrayList;


public class SettingsActivity extends AppCompatActivity {

    Toolbar toolbar;
    ArrayList<RadioButton> listRadio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.settings_title);
        toolbar.setNavigationIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.abc_ic_ab_back_mtrl_am_alpha, null));
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        listRadio = new ArrayList<>();

        RadioButton radioMostPopular = (RadioButton) findViewById(R.id.radioMostPopular);
        RadioButton radioHighestRated = (RadioButton) findViewById(R.id.radioHighestRated);

        listRadio.add(radioMostPopular);
        listRadio.add(radioHighestRated);
    }

    protected void onResume(){
        super.onResume();

        for(RadioButton radio : listRadio){
            radio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                        processRadios(buttonView);
                }
            });

            radio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.radioMostPopular:
                            //Save this in the SharedPreferences
                            break;
                        case R.id.radioHighestRated:
                            //Save this in the SharedPreferences
                            break;
                    }
                }
            });
        }
    }

    private void processRadios(CompoundButton buttonView){
        for(RadioButton radio : listRadio){
            if(radio != buttonView)
                radio.setChecked(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
