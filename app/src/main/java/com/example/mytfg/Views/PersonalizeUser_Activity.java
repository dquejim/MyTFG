package com.example.mytfg.Views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mytfg.Control.DB_Management;
import com.example.mytfg.Control.HttpConnect;
import com.example.mytfg.Control.Utils;
import com.example.mytfg.R;
import com.example.mytfg.Models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.github.muddz.styleabletoast.StyleableToast;

public class PersonalizeUser_Activity extends AppCompatActivity {

    EditText textName;
    EditText textPassword;
    EditText textNumber;
    EditText textAdress;

    Button buttonConfirm;
    BottomNavigationView bottomNavigationView;
    User myUser;

    Utils utils = new Utils();
    DB_Management db_management = new DB_Management(this);
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personalize_user_activity);
        getSupportActionBar().hide();

        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        myUser = db_management.getUser(utils.getPreferences(sharedPreferences));

        initComponents();

        String user = utils.getPreferences(sharedPreferences);
        new PersonalizeUser_Activity.getUserTask().execute("GET","/selectUser.php?user=\""+user+"\"");

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonalizeUser_Activity.this,Home_Activity.class);
                if(myUser.getName().equals("Invitado")){
                    createToast("No puedes modificar un usuario como invitado!",R.drawable.cross,Color.RED);
                    startActivity(intent);
                }else{
                    if(!textPassword.getText().equals(myUser.getPassword()) || !textNumber.getText().equals(myUser.getNumber()) || !textAdress.getText().equals(myUser.getAdress())){
                        db_management.alterUser(textName.getText().toString(), textPassword.getText().toString(), textNumber.getText().toString(), textAdress.getText().toString());
                        createToast("Usuario modificado correctamente!",R.drawable.tick,Color.GREEN);
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

    public void createToast(String title, int icon,int backgroundcolor){
        new StyleableToast.Builder(PersonalizeUser_Activity.this).text(title) //Texto del Toast y vista del mismo
                .backgroundColor(backgroundcolor).textColor(Color.BLACK) //Fondo y color de texto
                .iconStart(icon).show(); //Indicamos el icono del toast y lo mostramos
    }

    private void initComponents(){
        textName = findViewById(R.id.textNameR);
        textName.setEnabled(false);
        textPassword = findViewById(R.id.textPasswordR);
        textNumber = findViewById(R.id.textNumberR);
        textAdress = findViewById(R.id.textAdressR);
        buttonConfirm = findViewById(R.id.button);

        bottomNavigationView = findViewById(R.id.menu);
        bottomNavigationView.setSelectedItemId(R.id.user_option);

        if(utils.getPreferences(sharedPreferences).equals("Invitado")){
            textPassword.setEnabled(false);
            textNumber.setEnabled(false);
            textAdress.setEnabled(false);
        }
    }

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
        overridePendingTransition(0,0);
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

                        //Cargamos los datos del local a nuestro objeto
                        myUser = new User(user, password, number, adress);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            //Declaramos un intent hacia la home_activity
            Intent intent = new Intent(PersonalizeUser_Activity.this, Home_Activity.class);
            if(utils.comprobarInternet(getBaseContext())){
                textName.setText(myUser.getName());
                textAdress.setText(myUser.getAdress());
                textPassword.setText(myUser.getPassword());
                textNumber.setText(myUser.getNumber());
            }
        }
    }
}
