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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Home_Activity extends AppCompatActivity{

    DB_Management db_management = new DB_Management(this);
    ArrayList<Offer> offerList = new ArrayList<>();
    SharedPreferences sharedPreferences;
    Calendar c = Calendar.getInstance();
    Utils utils = new Utils();

    Offer firstOffer = null;
    Offer secondOffer = null;
    Local local = null;
    String now;
    String userName;

    ImageButton exitButton,facebook_button,instagram_button,maps_button;

    TextView userNameView,firstOfferDesc,secondOfferDesc,firstOfferPrice,secondOfferPrice,txtLocalNumber;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        getSupportActionBar().hide();

        now = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        now = utils.dayChanger(now);

        sharedPreferences  = getSharedPreferences("user", MODE_PRIVATE);
        userName = utils.getPreferences(sharedPreferences);

        initComponents();

        loadData();

        View.OnClickListener listener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                switch (v.getId())
                {
                    case R.id.exitButton:
                        Intent intent = new Intent(Home_Activity.this,Login_Activity.class);
                        startActivity(intent);
                        break;

                    case R.id.instagram_button:
                        navigateTo(local.getInstagram_link());
                        break;

                    case R.id.facebook_button:
                        navigateTo(local.getFacebook_link());
                        break;

                    case R.id.maps_button:
                        navigateTo(local.getUbicationLink());
                        break;

                    case R.id.txtLocalPhone:
                        navigateTo(local.getNumber());
                        break;

                    default:
                        break;
                }

            }
        };

        exitButton.setOnClickListener(listener);
        instagram_button.setOnClickListener(listener);
        facebook_button.setOnClickListener(listener);
        maps_button.setOnClickListener(listener);
        txtLocalNumber.setOnClickListener(listener);

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
        txtLocalNumber = findViewById(R.id.txtLocalPhone);

        exitButton = findViewById(R.id.exitButton);
        facebook_button = findViewById(R.id.facebook_button);
        maps_button = findViewById(R.id.maps_button);
        instagram_button = findViewById(R.id.instagram_button);

        userNameView.setText("Bienvenido " +userName + "!");
    }

    //Metodo para crear la tarea asincrona
    private class getOfferTask extends AsyncTask<String, Void, String> {
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

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            loadOffer(offerList);
        }
    }

    //Metodo para crear la tarea asincrona
    private class getLocalTask extends AsyncTask<String, Void, String> {
        String result;

        //Indicamos la funcion de la tarea asincrona, que será hacer peticiones GET a la API
        @Override
        protected String doInBackground(String... strings) {
            result = HttpConnect.getRequest(strings[1]);

            try {
                if (result != null) {
                    JSONArray jsonArr = new JSONArray(result);

                    for (int i = 0; i < jsonArr.length(); i++)
                    {
                        JSONObject jsonObject = jsonArr.getJSONObject(i);

                        //Obtenemos los datos de interes de nuestro objeto JSON
                        String id = jsonObject.getString("Id");
                        String ubication = jsonObject.getString("Ubication");
                        String facebook_link = jsonObject.getString("Fb_link");
                        String instagram_link = jsonObject.getString("Ig_link");
                        String adress = jsonObject.getString("Adress");
                        String number = jsonObject.getString("Number");

                        //Cargamos los datos del local a nuestro objeto
                        local = new Local(id, ubication,facebook_link,instagram_link, adress,number);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            txtLocalNumber.setText(local.getNumber());
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

    private void loadOffer(ArrayList<Offer> myList){
        offerList = myList;

        firstOffer = offerList.get(0);
        secondOffer = offerList.get(1);

        firstOfferDesc.setText(firstOffer.getName());
        firstOfferPrice.setText(firstOffer.getPrice());
        secondOfferDesc.setText(secondOffer.getName());
        secondOfferPrice.setText(secondOffer.getPrice());
    }

    private void loadData(){
        if(utils.comprobarInternet(this)){
            new getOfferTask().execute("GET","/selectOffer.php?week_day=\""+now+"\"");
            new getLocalTask().execute("GET","/selectLocal.php");


        }else{
            offerList.add(db_management.getOffers(now).get(0));
            offerList.add(db_management.getOffers(now).get(1));
            local = db_management.getLocalData("01").get(0);

            firstOffer = offerList.get(0);
            secondOffer = offerList.get(1);

            firstOfferDesc.setText(firstOffer.getName());
            firstOfferPrice.setText(firstOffer.getPrice());
            secondOfferDesc.setText(secondOffer.getName());
            secondOfferPrice.setText(secondOffer.getPrice());

            txtLocalNumber.setText(local.getNumber());
        }
    }

    private void navigateTo(String link){
        if(utils.comprobarInternet(getBaseContext()) && !link.equals("958 16 75 39")){
            Uri uri = Uri.parse(link);
            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            startActivity(intent);
        }

        if(link.equals("958 16 75 39")){
            String dial = "tel:" + link;
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
        }
    }
}


