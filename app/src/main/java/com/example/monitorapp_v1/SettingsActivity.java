package com.example.monitorapp_v1;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import Controller.SettingsController;

import static com.example.monitorapp_v1.MainActivity.restartService;
import static com.example.monitorapp_v1.MainActivity.setRefreshState;

public class SettingsActivity extends AppCompatActivity {

    private SettingsController backgroundSettingsController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Action bar logo display settings
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        final Switch switch1NotifChoice = findViewById(R.id.switch1NotifChoice);
        final Switch switch2NotifChoice = findViewById(R.id.switch2NotifChoice);

        final Switch switch0ServicePeriod = findViewById(R.id.switch0ServicePeriod);
        final Switch switch1ServicePeriod = findViewById(R.id.switch1ServicePeriod);
        final Switch switch2ServicePeriod = findViewById(R.id.switch2ServicePeriod);
        final Switch switch3ServicePeriod = findViewById(R.id.switch3ServicePeriod);
        final Switch switch4ServicePeriod = findViewById(R.id.switch4ServicePeriod);
        final Switch switch5ServicePeriod = findViewById(R.id.switch5ServicePeriod);

        backgroundSettingsController = new SettingsController(getApplicationContext());
        int settingsInterval=backgroundSettingsController.getSettingsInterval();
        int settingsType=backgroundSettingsController.getSettingsType();

        setRefreshState(true);
        restartService(true);

        if(settingsType==1) switch1NotifChoice.setChecked(true);
        else switch2NotifChoice.setChecked(true);

        switch (settingsInterval) {
            case 60:
                switch1ServicePeriod.setChecked(true);
                break;
            case 3:
                switch2ServicePeriod.setChecked(true);
                break;
            case 6:
                switch3ServicePeriod.setChecked(true);
                break;
            case 12:
                switch4ServicePeriod.setChecked(true);
                break;
            case 24:
                switch5ServicePeriod.setChecked(true);
                break;
            default:
                switch0ServicePeriod.setChecked(true);
        }

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        switch1NotifChoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switch2NotifChoice.setChecked(false);
                    backgroundSettingsController.callUpdateType(1);
                    Toast.makeText(getApplicationContext(),"Salvat",Toast.LENGTH_SHORT).show();
                }else {
                    switch2NotifChoice.setChecked(true);
                    backgroundSettingsController.callUpdateType(2);
                }
            }
        });
        switch2NotifChoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switch1NotifChoice.setChecked(false);
                    backgroundSettingsController.callUpdateType(2);
                    Toast.makeText(getApplicationContext(),"Salvat",Toast.LENGTH_SHORT).show();
                }else {
                    switch1NotifChoice.setChecked(true);
                    backgroundSettingsController.callUpdateType(1);
                }
            }
        });
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        switch0ServicePeriod.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switch1ServicePeriod.setChecked(false);
                    switch2ServicePeriod.setChecked(false);
                    switch3ServicePeriod.setChecked(false);
                    switch4ServicePeriod.setChecked(false);
                    switch5ServicePeriod.setChecked(false);
                    backgroundSettingsController.callUpdateInterval(30);
                    Toast.makeText(getApplicationContext(),"Salvat",Toast.LENGTH_SHORT).show();
                }
            }
        });
        switch1ServicePeriod.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switch0ServicePeriod.setChecked(false);
                    switch2ServicePeriod.setChecked(false);
                    switch3ServicePeriod.setChecked(false);
                    switch4ServicePeriod.setChecked(false);
                    switch5ServicePeriod.setChecked(false);
                    backgroundSettingsController.callUpdateInterval(60);
                    Toast.makeText(getApplicationContext(),"Salvat",Toast.LENGTH_SHORT).show();
                }
            }
        });
        switch2ServicePeriod.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switch0ServicePeriod.setChecked(false);
                    switch1ServicePeriod.setChecked(false);
                    switch3ServicePeriod.setChecked(false);
                    switch4ServicePeriod.setChecked(false);
                    switch5ServicePeriod.setChecked(false);
                    backgroundSettingsController.callUpdateInterval(3);
                    Toast.makeText(getApplicationContext(),"Salvat",Toast.LENGTH_SHORT).show();
                }
            }
        });
        switch3ServicePeriod.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switch0ServicePeriod.setChecked(false);
                    switch1ServicePeriod.setChecked(false);
                    switch2ServicePeriod.setChecked(false);
                    switch4ServicePeriod.setChecked(false);
                    switch5ServicePeriod.setChecked(false);
                    backgroundSettingsController.callUpdateInterval(6);
                    Toast.makeText(getApplicationContext(),"Salvat",Toast.LENGTH_SHORT).show();
                }
            }
        });
        switch4ServicePeriod.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switch0ServicePeriod.setChecked(false);
                    switch1ServicePeriod.setChecked(false);
                    switch2ServicePeriod.setChecked(false);
                    switch3ServicePeriod.setChecked(false);
                    switch5ServicePeriod.setChecked(false);
                    backgroundSettingsController.callUpdateInterval(12);
                    Toast.makeText(getApplicationContext(),"Salvat",Toast.LENGTH_SHORT).show();
                }
            }
        });
        switch5ServicePeriod.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switch0ServicePeriod.setChecked(false);
                    switch1ServicePeriod.setChecked(false);
                    switch2ServicePeriod.setChecked(false);
                    switch3ServicePeriod.setChecked(false);
                    switch4ServicePeriod.setChecked(false);
                    backgroundSettingsController.callUpdateInterval(24);
                    Toast.makeText(getApplicationContext(),"Salvat",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
