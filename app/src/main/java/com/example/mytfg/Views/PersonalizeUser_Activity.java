package com.example.mytfg.Views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mytfg.Control.HttpConnect;
import com.example.mytfg.Control.Utils;
import com.example.mytfg.R;
import com.example.mytfg.Models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PersonalizeUser_Activity extends AppCompatActivity {

    //Declaración de variables
    EditText textName,textPassword,textNumber,textAdress;
    TextView offLineText;

    Button buttonConfirm;
    BottomNavigationView bottomNavigationView;
    User myUser;

    String userName;

    Utils utils = new Utils();
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.personalize_user_activity);
        //Escondemos el Action Bar
        getSupportActionBar().hide();

        //Obtenemos el usuario con el que estamos conectados en este momento
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        userName = (utils.getPreferences(sharedPreferences));

        initComponents();

        //Indicamos los listener y acciones necesarios
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creamos un intemt hacia la Home_Activity
                Intent intent = new Intent(PersonalizeUser_Activity.this,Home_Activity.class);

                //Si no tenemos internet o  estamos conectados como invitados
                if(!utils.checkInternetConnection(getBaseContext()) || userName.equals("Invitado")){
                    utils.createToast("No puedes modificar un usuario sin conexion!",R.drawable.cross,Color.RED,PersonalizeUser_Activity.this);
                    startActivity(intent);
                }else{
                    if(!textPassword.getText().equals(myUser.getPassword()) || !textNumber.getText().equals(myUser.getNumber()) || !textAdress.getText().equals(myUser.getAdress())){
                        //Lanzamos la tarea asincrona que actualizará nuestro usuario en la base de datos
                        new PersonalizeUser_Activity.updateUserTask().execute("GET","/updateUser.php?password=\""+textPassword.getText()+"\"&number=\""+textNumber.getText()+"\"&adress=\""+textAdress.getText()+"\"&name=\""+myUser.getName()+"\"&fav_food=\"-\"" );
                    }
                    startActivity(intent);
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

    //Método para iniciar componentes gráficos y otros procesos
    private void initComponents(){
        textName = findViewById(R.id.textNameR);
        textName.setEnabled(false);
        textPassword = findViewById(R.id.textPasswordR);
        textNumber = findViewById(R.id.textNumberR);
        textAdress = findViewById(R.id.textAdressR);
        offLineText = findViewById(R.id.offLineText);

        buttonConfirm = findViewById(R.id.button);

        bottomNavigationView = findViewById(R.id.menu);
        bottomNavigationView.setSelectedItemId(R.id.user_option);

        //Si no tenemos conexión a Internet, desactivamos los botones
        if(!utils.checkInternetConnection(getBaseContext())){
            textPassword.setEnabled(false);
            textNumber.setEnabled(false);
            textAdress.setEnabled(false);
            offLineText.setText("No dispones de conexión en estos momentos");
        }

        //Si tenemos conexion y no estamos como invitados, lanzamos una tarea asincrona para obtener el usuario con el que estamos conectados
        if (utils.checkInternetConnection(getBaseContext()) && !userName.equals("Invitado")) {
            new PersonalizeUser_Activity.getUserTask().execute("GET","/selectUser.php?user=\""+userName+"\"");
        }
    }

    //Método para cargar el menú de navegación y sus componentes
    private void loadMenu(MenuItem item){
        Intent intent = null;

        switch (item.getItemId()){
            case R.id.user_option:
                intent = new Intent(PersonalizeUser_Activity.this, PersonalizeUser_Activity.class);
                break;

            case R.id.home_option:
                intent = new Intent(PersonalizeUser_Activity.this, Home_Activity.class);
                break;

            case R.id.food_option:
                intent = new Intent(PersonalizeUser_Activity.this, Food_Activity.class);
                break;
        }

        startActivity(intent);
        //Eliminación de la animación entre intents
        overridePendingTransition(0,0);
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
            //Declaramos un intent hacia la home_activity
            Intent intent = new Intent(PersonalizeUser_Activity.this, Home_Activity.class);
            //Si tenemos conexion a internet,actualizamos nuestros datos
            if(utils.checkInternetConnection(getBaseContext())){
                textName.setText(myUser.getName());
                textAdress.setText(myUser.getAdress());
                textPassword.setText(myUser.getPassword());
                textNumber.setText(myUser.getNumber());
            }
        }
    }

    //Tarea asincrona para actualizar un usuario
    private class updateUserTask extends AsyncTask<String, Void, String> {
        String result;

        //Indicamos la funcion de la tarea asincrona, que será hacer peticiones GET a la API
        @Override
        protected String doInBackground(String... strings) {
            result = HttpConnect.getRequest(strings[1]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            utils.createToast("Usuario modificado correctamente!",R.drawable.tick,Color.GREEN,PersonalizeUser_Activity.this);
            }
        }

    }
