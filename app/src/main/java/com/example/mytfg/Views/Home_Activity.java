package com.example.mytfg.Views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.mytfg.Control.DB_Management;
import com.example.mytfg.Control.HttpConnect;
import com.example.mytfg.Control.Utils;
import com.example.mytfg.Models.Local;
import com.example.mytfg.Models.Offer;
import com.example.mytfg.Models.User;
import com.example.mytfg.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class Home_Activity extends AppCompatActivity{

    //Declaraión de variables
    DB_Management db_management = new DB_Management(this);
    ArrayList<Offer> offerList = new ArrayList<>();
    SharedPreferences sharedPreferences;
    Calendar c = Calendar.getInstance();
    Utils utils = new Utils();

    Offer firstOffer = null;
    Offer secondOffer = null;
    Local local = null;
    User myUser;
    String now;
    String userName;

    ImageButton exitButton,facebook_button,instagram_button,maps_button;

    TextView firstOfferDesc,secondOfferDesc,firstOfferPrice,secondOfferPrice,txtLocalNumber,txtFavouriteHome;

    CardView favouriteViewHome;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.home_activity);

        //Escondemos el ActionBar
        getSupportActionBar().hide();

        //Obtenemos el dia de la semana en el que estamos
        now = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        now = utils.dayChanger(now);

        //Obtenemos las preferencias, que nos dará el nombre de usuario con el que estams registrados
        sharedPreferences  = getSharedPreferences("user", MODE_PRIVATE);
        userName = utils.getPreferences(sharedPreferences);

        //Iniciamos componentes
        initComponents();

        //Cargamos los datos necesarios
        loadData();

        //Indicamos los listeners y sus eventos
        View.OnClickListener listener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                switch (v.getId())
                {
                    case R.id.exitButtonHome:
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

                    case R.id.txtLocalPhoneHome:
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

        txtLocalNumber = findViewById(R.id.txtLocalPhoneHome);
        txtFavouriteHome = findViewById(R.id.txtFavouriteHome);

        favouriteViewHome = findViewById(R.id.favouriteViewHome);

        exitButton = findViewById(R.id.exitButtonHome);
        facebook_button = findViewById(R.id.facebook_button);
        maps_button = findViewById(R.id.maps_button);
        instagram_button = findViewById(R.id.instagram_button);

        if(!utils.checkInternetConnection(getBaseContext()) || userName.equals("Invitado")){
            favouriteViewHome.setVisibility(View.GONE);
        }
    }

    //Tarea asincrona para obtener todas las ofertas
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
            super.onPostExecute(s);
            firstOffer = offerList.get(0);
            secondOffer = offerList.get(1);

            firstOfferDesc.setText(firstOffer.getName());
            firstOfferPrice.setText(firstOffer.getPrice());
            secondOfferDesc.setText(secondOffer.getName());
            secondOfferPrice.setText(secondOffer.getPrice());
        }
    }

    //Tarea asincrona para obtener los datos del local
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

    //Tarea asincrona para obtener un usuario y sus datos
    private class getUserTask extends AsyncTask<String, Void, String> {
        //Declaramos un intent hacia la home_activity
        String result;

        //Indicamos la funcion de la tarea asincrona, que será hacer peticiones GET a la API
        @Override
        protected String doInBackground(String... strings) {
            result = HttpConnect.getRequest(strings[1]);
            try {
                if (result != null) {
                    JSONArray jsonArr = new JSONArray(result);

                    for (int i = 0; i < jsonArr.length(); i++) {
                        JSONObject jsonObject = jsonArr.getJSONObject(i);

                        //Obtenemos los datos de interes de nuestro objeto JSON
                        String user = jsonObject.getString("user");
                        String password = jsonObject.getString("password");
                        String number = jsonObject.getString("number");
                        String adress = jsonObject.getString("adress");
                        String fav_food = jsonObject.getString("fav_food");

                        //Cargamos los datos del local a nuestro objeto
                        myUser = new User(user, password, number, adress,fav_food);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }

        //Indicamos la función del proceso una vez haya acabado
        @Override
        protected void onPostExecute(String s) {
            if(!userName.equals("Invitado")) {
                if (myUser.getFav_food().equals("-")) {
                    favouriteViewHome.setVisibility(View.GONE);
                } else {
                    txtFavouriteHome.setText(myUser.getFav_food());
                }
            }
        }
    }


    //Método para cargar y gestionar el menu para navegar entre actividades
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

        //Eliminacion de la animacion del intent
        overridePendingTransition(0,0);
    }

    //Método para cargar y gestionar los datos tanto de ofertas como del local
    private void loadData(){
        //Si disponemos de conexión a Internet lo carga de Internet
        if(utils.checkInternetConnection(this)){
            new getOfferTask().execute("GET","/selectOffer.php?week_day=\""+now+"\"");
            new getLocalTask().execute("GET","/selectLocal.php");
            new Home_Activity.getUserTask().execute("GET","/selectUser.php?user=\""+userName+"\"");

            //Si no disponemos, lo hace de la base de datos
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

    //Método con el que navegaremos a links como el de instagram, facebook ...
    private void navigateTo(String link){
        if(utils.checkInternetConnection(getBaseContext()) && !link.equals("958 16 75 39")){
            Uri uri = Uri.parse(link);
            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            startActivity(intent);
        }

        //Si el parámetro a navegar es el numero de teléfono, nos llevará a la libreta de contactos
        if(link.equals("958 16 75 39")){
            String dial = "tel:" + link;
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
        }
    }
}


