package com.example.mytfg.Views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytfg.Control.DB_Management;
import com.example.mytfg.Control.HttpConnect;
import com.example.mytfg.Control.RecyclerAdapter;
import com.example.mytfg.Control.Utils;
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

    CardView sandwich_option,pizza_option,camp_option,burguer_option,potato_option,bread_option;
    
    RecyclerView recyclerView;
    RecyclerAdapter recAdapter;

    LinearLayoutManager layoutManager;
    ConstraintLayout presentationLayout;

    ArrayList<Food> foodList = new ArrayList<>();
    Utils utils = new Utils();
    DB_Management db_management = new DB_Management(this);

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
                loadMenuOptions("Sandwiches");
            }
        });

        pizza_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMenuOptions("Pizzas");
            }
        });

        camp_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMenuOptions("Camperos");
            }
        });
        
        burguer_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMenuOptions("Hamburguesas");
            }
        });

        potato_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMenuOptions("Patatas");
            }
        });

        bread_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMenuOptions("Bocadillos");
            }
        });
    }

    private void initComponents(){
        bottomNavigationView = findViewById(R.id.menu);
        bottomNavigationView.setSelectedItemId(R.id.food_option);

        sandwich_option = findViewById(R.id.sandwichButton);
        pizza_option = findViewById(R.id.pizzaButton);
        camp_option = findViewById(R.id.campButton);
        burguer_option = findViewById(R.id.burguerButton);
        potato_option = findViewById(R.id.potatoeButton);
        bread_option = findViewById(R.id.breadButton);

        recyclerView = (RecyclerView) findViewById(R.id.myRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        presentationLayout = findViewById(R.id.presentationLayout);

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
    private class getOnlineMenu extends AsyncTask<String, Void, String> {
        String result;

        //Indicamos la funcion de la tarea asincrona, que será hacer peticiones GET a la API
        @Override
        protected String doInBackground(String... strings) {
            result = HttpConnect.getRequest(strings[1]);

            try {
                if (result != null) {
                    JSONArray jsonArr = new JSONArray(result);

                    for (int i = 0; i < jsonArr.length(); i++){

                        JSONObject jsonObject = jsonArr.getJSONObject(i);

                        //Obtenemos los datos de interes de nuestro objeto JSON
                        String id = jsonObject.getString("number");
                        String name = jsonObject.getString("category");
                        String week_day = jsonObject.getString("product");
                        String price = jsonObject.getString("price");

                        //Creamos un objeto comida y lo añadimos a la lista
                        foodList.add(new Food(id, name, week_day, price));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            recAdapter.notifyDataSetChanged();
        }
    }

    //Metodo para crear la tarea asincrona
    private class getOffLineMenu extends AsyncTask<String, Void, String> {
        String result = "";

        //Indicamos la funcion de la tarea asincrona, que será hacer peticiones GET a la API
        @Override
        protected String doInBackground(String... strings) {
            foodList = (ArrayList<Food>) db_management.getProducts(strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            recAdapter = new RecyclerAdapter(foodList);
            recyclerView.setAdapter(recAdapter);
            recyclerView.setLayoutManager(layoutManager);
            recAdapter.notifyDataSetChanged();
        }
    }

    private void loadMenuOptions(String menu_option){
        foodList.clear();
        presentationLayout.setVisibility(View.GONE);

        if(utils.comprobarInternet(getBaseContext())) {
            new getOnlineMenu().execute("GET", "/selectFood.php?category=\"" + menu_option + "\"");
        }else{
            new getOffLineMenu().execute(menu_option);
        }
    }

}
