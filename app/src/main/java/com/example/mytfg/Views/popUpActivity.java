package com.example.mytfg.Views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mytfg.Control.HttpConnect;
import com.example.mytfg.Control.Utils;
import com.example.mytfg.Models.Product;
import com.example.mytfg.Models.User;
import com.example.mytfg.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.github.muddz.styleabletoast.StyleableToast;

public class popUpActivity extends AppCompatActivity {

    TextView txtProductDescription;
    ImageButton heartButton;

    Product product;
    SharedPreferences sharedPreferences;
    String userName;
    User myUser;
    Utils utils = new Utils();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_pop_up);

        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        userName = (utils.getPreferences(sharedPreferences));
        product = (Product) getIntent().getSerializableExtra("Product");

        initComponents();

    }

    public void initComponents() {
        txtProductDescription = findViewById(R.id.txtProductDescription);
        heartButton = findViewById(R.id.heartButton);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.75), (int) (height * 0.15));

        if(product.getDescription().equals("-")){
            txtProductDescription.setText(product.getProduct());
        }else{
            txtProductDescription.setText(product.getDescription());
        }

        if (utils.comprobarInternet(getBaseContext()) && !userName.equals("Invitado")) {
            new popUpActivity.getUserTask().execute("GET","/selectUser.php?user=\""+userName+"\"");
        }
    }

    //Metodo para crear la tarea asincrona
    private class getUserTask extends AsyncTask<String, Void, String> {
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
                        myUser = new User(user, password, number, adress, fav_food);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if(product.getProduct().equals(myUser.getFav_food())){
                heartButton.setImageResource(R.drawable.red_heart_ico);
            }else{
                heartButton.setImageResource(R.drawable.black_heart_ico);
            }

            heartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setAsFavourite(myUser.getFav_food());
                }
            });

        }
    }

    //Metodo para crear la tarea asincrona
    private class updateUserTask extends AsyncTask<String, Void, String> {
        String result;

        //Indicamos la funcion de la tarea asincrona, que será hacer peticiones GET a la API
        @Override
        protected String doInBackground(String... strings) {
            result = HttpConnect.getRequest(strings[0]);
            return result;
        }
    }

    private void setAsFavourite(String fav_food){
        if(fav_food.equals(product.getProduct())){
            new popUpActivity.updateUserTask().execute("/updateUser.php?password=\""+myUser.getPassword()+
                    "\"&number=\""+myUser.getNumber()+"\"&adress=\""+myUser.getAdress()+"\"&name=\""+myUser.getName()+
                    "\"&fav_food=\"-\"" );
            heartButton.setImageResource(R.drawable.black_heart_ico);
            myUser.setFav_food("-");
        }else{
            new popUpActivity.updateUserTask().execute("/updateUser.php?password=\""+myUser.getPassword()+
                    "\"&number=\""+myUser.getNumber()+"\"&adress=\""+myUser.getAdress()+"\"&name=\""+myUser.getName()+
                    "\"&fav_food=\""+product.getProduct()+"\"" );
            heartButton.setImageResource(R.drawable.red_heart_ico);
            myUser.setFav_food(product.getProduct());
        }
    }
}

