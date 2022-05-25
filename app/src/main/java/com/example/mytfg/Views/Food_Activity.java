package com.example.mytfg.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mytfg.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Food_Activity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_activity);
        getSupportActionBar().hide();

        bottomNavigationView = findViewById(R.id.menu);
        bottomNavigationView.setSelectedItemId(R.id.food_option);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;

                switch (item.getItemId()){
                    case R.id.user_option:
                        intent = new Intent(Food_Activity.this, PersonalizeUser_Activity.class);
                        break;

                    case R.id.home_option:
                        intent = new Intent(Food_Activity.this, Home_Activity.class);
                        break;

                    case R.id.food_option:
                        intent = new Intent(Food_Activity.this, Food_Activity.class);
                        break;
                }

                startActivity(intent);
                overridePendingTransition(0,0);
                return true;
            }
        });
    }


}
