package com.example.mytfg.Views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mytfg.Control.DB_Management;
import com.example.mytfg.Control.HttpConnect;
import com.example.mytfg.Control.Utils;
import com.example.mytfg.Models.Local;
import com.example.mytfg.Models.Offer;
import com.example.mytfg.R;
import com.example.mytfg.Models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Home_Activity extends AppCompatActivity{

    DB_Management db_management = new DB_Management(this);
    List<Offer> offerList = new ArrayList<>();
    SharedPreferences sharedPreferences;
    Calendar c = Calendar.getInstance();
    Utils utils = new Utils();

    User user;
    Offer firstOffer;
    Offer secondOffer;
    Uri _link;
    Local local = null;

    ImageButton exitButton;
    ImageButton fakeMapsView;
    ImageView optionalImageView;

    TextView userNameView;
    TextView firstOfferDesc;
    TextView secondOfferDesc;
    TextView firstOfferPrice;
    TextView secondOfferPrice;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        getSupportActionBar().hide();

        sharedPreferences  = getSharedPreferences("user", MODE_PRIVATE);
        user = db_management.getUser(utils.getPreferences(sharedPreferences));

        new getOfferTask().execute("GET","/selectOffer.php");
        new getLocalTask().execute("GET","/selectLocal.php");

        initComponents();

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home_Activity.this,Login_Activity.class);
                startActivity(intent);
            }
        });

        fakeMapsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(utils.comprobarInternet(getBaseContext())) {
                    _link = Uri.parse(local.getUbicationLink());
                    Intent i = new Intent(Intent.ACTION_VIEW, _link);
                    startActivity(i);
                }
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                loadMenu(item);
                return true;
            }
        });
    }

    private void initComponents(){
        firstOfferPrice = findViewById(R.id.firstOfferPrice);
        firstOfferDesc = findViewById(R.id.firstOfferDesc);

        secondOfferPrice = findViewById(R.id.secondOfferPrice);
        secondOfferDesc = findViewById(R.id.secondOfferDesc);

        bottomNavigationView = findViewById(R.id.menu);
        bottomNavigationView.setSelectedItemId(R.id.home_option);

        userNameView = findViewById(R.id.userNameView);
        userNameView.setText("Bienvenido " + user.getName()+ "!");
        exitButton = findViewById(R.id.exitButton);
        optionalImageView = findViewById(R.id.optionalImageView);
        fakeMapsView = findViewById(R.id.fakeMapView);
    }

    //Metodo para crear la tarea asincrona
    private class getOfferTask extends AsyncTask<String, Void, String> {
        String result;

        //Indicamos la funcion de la tarea asincrona, que será hacer peticiones GET a la API
        @Override
        protected String doInBackground(String... strings) {
            result = HttpConnect.getRequest(strings[1]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                if (s != null) {
                    JSONArray jsonArr = new JSONArray(s);

                    for (int i = 0; i < jsonArr.length(); i++)
                    {
                        JSONObject jsonObject = jsonArr.getJSONObject(i);

                        //Obtenemos los datos de interes de nuestro objeto JSON
                        String id = jsonObject.getString("id");
                        String name = jsonObject.getString("name");
                        String week_day = jsonObject.getString("week_day");
                        String price = jsonObject.getString("price");

                        //Creamos un objeto oferta y lo añadimos a la lista
                        Offer offer = new Offer(id, name, week_day, price);
                        offerList.add(offer);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            loadOffer();
        }
    }

    //Metodo para crear la tarea asincrona
    private class getLocalTask extends AsyncTask<String, Void, String> {
        String result;

        //Indicamos la funcion de la tarea asincrona, que será hacer peticiones GET a la API
        @Override
        protected String doInBackground(String... strings) {
            result = HttpConnect.getRequest(strings[1]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                if (s != null) {
                    JSONArray jsonArr = new JSONArray(s);

                    for (int i = 0; i < jsonArr.length(); i++)
                    {
                        JSONObject jsonObject = jsonArr.getJSONObject(i);

                        //Obtenemos los datos de interes de nuestro objeto JSON
                        String id = jsonObject.getString("id");
                        String ubication = jsonObject.getString("ubication");
                        String adress = jsonObject.getString("adress");

                        //Cargamos los datos del local a nuestro objeto
                        local = new Local(id, ubication, adress);
                        System.out.println(local.getUbicationLink());
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadMenu(MenuItem item){
        Intent intent = null;

        switch (item.getItemId()){
            case R.id.user_option:
                intent = new Intent(Home_Activity.this, PersonalizeUser_Activity.class);
                break;

            case R.id.home_option:
                intent = new Intent(Home_Activity.this, Home_Activity.class);
                break;

            case R.id.food_option:
                intent = new Intent(Home_Activity.this, Food_Activity.class);
                break;
        }

        startActivity(intent);
        overridePendingTransition(0,0);
    }

    private void loadOffer(){
        String dayOfTheWeek = String.valueOf(c.get(Calendar.DAY_OF_WEEK));

        if(utils.comprobarInternet(this)){
            for(int i = 0;i <offerList.size();i++){
                if(offerList.get(i).getDay().equals(dayOfTheWeek)){
                    firstOffer = offerList.get(i);
                    secondOffer = offerList.get(i+1);
                }
            }

        }else{
            firstOffer = db_management.getOffers(dayOfTheWeek).get(0);
            secondOffer = db_management.getOffers(dayOfTheWeek).get(1);
        }

        firstOfferDesc.setText(firstOffer.getName());
        firstOfferPrice.setText(firstOffer.getPrice());
        secondOfferDesc.setText(secondOffer.getName());
        secondOfferPrice.setText(secondOffer.getPrice());
    }
}

