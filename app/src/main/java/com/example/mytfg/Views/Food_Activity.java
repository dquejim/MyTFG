package com.example.mytfg.Views;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytfg.Control.HttpConnect;
import com.example.mytfg.Control.RecyclerAdapter;
import com.example.mytfg.Models.Food;
import com.example.mytfg.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Food_Activity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    
    CardView foodCardView;
    CardView sandwich_option;
    CardView pizza_option;
    CardView camp_option;
    CardView burguer_option;
    CardView potato_option;
    
    RecyclerView recyclerView;
    RecyclerAdapter recAdapter;
    ArrayList<Food> foodList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_activity);
        getSupportActionBar().hide();

        initComponents();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                loadMenu(item);
                return true;
            }
        });

        sandwich_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Food_Activity.getFoodTask().execute("GET","/selectFood.php?category=\"Sandwiches\"");
                foodCardView.setVisibility(View.VISIBLE);
            }
        });

        pizza_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Food_Activity.getFoodTask().execute("GET","/selectFood.php?category=\"Pizzas\"");
                foodCardView.setVisibility(View.VISIBLE);
            }
        });

        camp_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Food_Activity.getFoodTask().execute("GET","/selectFood.php?category=\"Camperos\"");
                foodCardView.setVisibility(View.VISIBLE);
            }
        });
        
        burguer_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Food_Activity.getFoodTask().execute("GET","/selectFood.php?category=\"Hamburguesas\"");
                foodCardView.setVisibility(View.VISIBLE);
            }
        });

        potato_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Food_Activity.getFoodTask().execute("GET","/selectFood.php?category=\"Patatas\"");
                foodCardView.setVisibility(View.VISIBLE);
            }
        });
        
        


    }

    private void initComponents(){
        bottomNavigationView = findViewById(R.id.menu);
        bottomNavigationView.setSelectedItemId(R.id.food_option);
        foodCardView = findViewById(R.id.foodCardView);

        sandwich_option = findViewById(R.id.card_View1);
        pizza_option = findViewById(R.id.card_View2);
        camp_option = findViewById(R.id.card_View3);
        burguer_option = findViewById(R.id.card_View4);
        potato_option = findViewById(R.id.crad_View5);

        recyclerView = (RecyclerView) findViewById(R.id.myRecycerView);
        foodCardView.setVisibility(View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        //Le indicamos el adaptador a usar, así como su layout
        recAdapter = new RecyclerAdapter(foodList);
        recyclerView.setAdapter(recAdapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void loadMenu(MenuItem item){
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
    }

    //Metodo para crear la tarea asincrona
    private class getFoodTask extends AsyncTask<String, Void, String> {
        String result;

        //Indicamos la funcion de la tarea asincrona, que será hacer peticiones GET a la API
        @Override
        protected String doInBackground(String... strings) {
            result = HttpConnect.getRequest(strings[1]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            ArrayList<Food> myFoodList = new ArrayList<>();
            try {
                if (s != null) {
                    JSONArray jsonArr = new JSONArray(s);

                    for (int i = 0; i < jsonArr.length(); i++){

                        JSONObject jsonObject = jsonArr.getJSONObject(i);

                        //Obtenemos los datos de interes de nuestro objeto JSON
                        String id = jsonObject.getString("number");
                        String name = jsonObject.getString("category");
                        String week_day = jsonObject.getString("product");
                        String price = jsonObject.getString("price");

                        //Creamos un objeto comida y lo añadimos a la lista
                        foodList.add(new Food(id, name, week_day, price));
                        recAdapter.notifyDataSetChanged();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
