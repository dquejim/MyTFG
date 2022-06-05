package com.example.mytfg.Views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mytfg.Control.DB_Management;
import com.example.mytfg.Control.HttpConnect;
import com.example.mytfg.Control.Utils;
import com.example.mytfg.Models.User;
import com.example.mytfg.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Login_Activity extends AppCompatActivity {

    //Declaración de variables
    TextView bRegister;
    Button bLogin;
    EditText textName;
    EditText textPassword;
    DB_Management db_management = new DB_Management(this);
    SharedPreferences sharedPreferences;
    Utils utils = new Utils();
    User myUser;
    User searchedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.login_activity);
        //Escondemos el actionBar de la actividad
        getSupportActionBar().hide();

        sharedPreferences = getSharedPreferences("user",MODE_PRIVATE);

        initComponents();

        //Accion al pulsar el botón de registrarse de la pantalla de login
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadRegisterButton();
            }
        });
        
        //Accion del boton de login
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               loadLoginButton();
            }
        });
    }

    //Método para crear y mostrar un alertdialog con las opciones si o no
    public void createAlertDialog(String title,Intent intent){
        SharedPreferences sharedPreferences = getSharedPreferences("user",MODE_PRIVATE);
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        final CharSequence[] opciones = {"Si","No"};
        alertDialog.setTitle(title);
        alertDialog.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(opciones[i].equals("Si")){
                    utils.setPreferences("Invitado",sharedPreferences);
                    startActivity(intent);
                }
            }
        });

        alertDialog.show();

    }

    //Método para iniciar los componentes gráficos y otros procesos
    private void initComponents(){
        //"Enlazamos" los componentes graficos con las variables creadas anteriormente
        bRegister = (TextView) findViewById(R.id.bRegister);
        bLogin = (Button) findViewById(R.id.bLogin);
        textName = (EditText) findViewById(R.id.textName);
        textPassword = (EditText) findViewById(R.id.textPassword);

        //Comprobamos si el usuario Invitado001 ya existe, si no existe, lo creamos, ya que nos conectaremos a él cuando no dispongamos de conexion
        if(db_management.checkUser("Invitado", "", 2) == null){
            //Insertamos en la base de datos el usuario al que nos conectaremos cuando no haya conexión
            db_management.insertUser("Invitado", "1234213453", "Default", "Default");
            //Insertamos a la base de datos los datos del local
            db_management.insertLocalData("01","https://goo.gl/maps/5MdAZttVLEe5jzr5A","https://es-es.facebook.com/masquepizzasgranada/","https://www.instagram.com/masquepizzaszubia/?hl=es","Calle Almuñecar nº6","958 16 75 39");
            //Insertamos a la base de datos los datos del menú
            db_management.insertAllMenu();
        }
    }

    //Método para cargar el botón de login
    private void loadLoginButton(){
        //Si disponemos de internet obtenemos el usuario y contraseña introducidos
        if(utils.checkInternetConnection(getBaseContext())){
            if(!textName.getText().toString().isEmpty() && !textPassword.getText().toString().isEmpty()) {
                String user = textName.getText().toString();
                String password = textPassword.getText().toString();
                searchedUser = new User(user,password,"","","");

                //Iniciamos una tarea asincrona para obtener el usuario de esa base de datos
                new Login_Activity.getUserTask().execute("GET","/selectUser.php?user='"+user+"'");

                //Si no hay conexion a Internet, nos pregunta si queremos conectarnos como usuario
            }
        }else{
            Intent intent = new Intent(Login_Activity.this, Home_Activity.class);
            createAlertDialog("Parece que está sin conexión, ¿desea acceder como invitado?",intent);
        }
    }

    //Método para cargar el boton de registrarse
    private void loadRegisterButton(){
        if(utils.checkInternetConnection(getBaseContext())){
            //Si disponemos de internet, nos enviará a la pantalla de registro de usuario
            Intent intent = new Intent(Login_Activity.this, NewAccount_Activity.class);
            startActivity(intent);
            //Si no, nos pregunta si queremos entrar como invitado
        }else{
            Intent intent = new Intent(Login_Activity.this, Home_Activity.class);
            createAlertDialog("Parece que está sin conexión, ¿desea acceder como invitado? ",intent);
        }
    }

    //Tarea asincrona para obtener todos los datos de un usuario
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
                        myUser = new User(user, password, number, adress, "");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }

        //Indicamos las acciones a realizar en la vista tras completar el proceso
        @Override
        protected void onPostExecute(String s) {
            //Declaramos un intent hacia la home_activity
            Intent intent = new Intent(Login_Activity.this, Home_Activity.class);

            //Si el usuario tiene datos, es decir, existe
            if(!s.equals("[]")) {
                //Si es el usuario y contraseña que buscamos...
                if (searchedUser.getName().equals(myUser.getName()) && searchedUser.getPassword().equals(myUser.getPassword())) {
                    //Lanzamos un toast de login correcto
                    utils.createToast("Login correcto!", R.drawable.tick, Color.GREEN,Login_Activity.this);

                    //Iniciamos el intent pasandole el nombre de usuario
                    utils.setPreferences(myUser.getName(), sharedPreferences);
                    startActivity(intent);

                    //Si no lo es
                } else {
                    utils.createToast("Usuario o contraseña incorrectos.", R.drawable.cross, Color.RED,Login_Activity.this);
                }
                //Si el usuario no tiene datos
            }else{
                utils.createToast("Usuario o contraseña incorrectos.", R.drawable.cross, Color.RED,Login_Activity.this);
            }

            //Si s devuelve valor nulo, significa que no tenemos conexion a internet
            if(s == null){
                createAlertDialog("Parece que está sin conexión, ¿desea acceder como invitado?",intent);
            }
        }
    }

}