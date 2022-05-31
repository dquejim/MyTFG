package com.example.mytfg.Views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mytfg.Control.DB_Management;
import com.example.mytfg.Control.HttpConnect;
import com.example.mytfg.Control.Utils;
import com.example.mytfg.Models.User;
import com.example.mytfg.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.github.muddz.styleabletoast.StyleableToast;

public class NewAccount_Activity extends AppCompatActivity {

    //Declaracion de variables
    Button bRegister;
    EditText textNameR;
    EditText textPasswordR;
    EditText textNumberR;
    EditText textAdressR;

    User myUser;

    DB_Management db_management = new DB_Management(this);
    Utils utils = new Utils();
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newaccount_activity);
        //Escondemos el actionBar
        getSupportActionBar().hide();

        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);

        initComponents();

        //Añadimos una accion al pulsar el boton para registrarse
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadLoginButton();
            }
        });
    }

    private void initComponents(){
        //"Enlazamos" los componentes graficos con las variables creadas anteriormente
        bRegister = (Button) findViewById(R.id.button);
        textNameR = (EditText) findViewById(R.id.textNameR);
        textPasswordR = (EditText) findViewById(R.id.textPasswordR);
        textNumberR = (EditText) findViewById(R.id.textNumberR);
        textAdressR = (EditText) findViewById(R.id.textAdressR);
    }

    private void loadLoginButton(){
        //Declaramos un intent desde esta actividad hasta la principal
        Intent intent = new Intent(NewAccount_Activity.this, Home_Activity.class);

        //Si tenemos conexion a internet obtenemos los datos que el usuario ha introducido en las cajas de texto
        if(utils.comprobarInternet(getBaseContext())){
            if(!textNameR.getText().toString().isEmpty() && !textPasswordR.getText().toString().isEmpty()  && !textNumberR.getText().toString().isEmpty()  && !textAdressR.getText().toString().isEmpty() ) {
                String user = textNameR.getText().toString();
                new NewAccount_Activity.getUserTask().execute("GET","/selectUser.php?user=\""+user+"\"");
            }else{
                createToast("Debes rellenar todos los campos.",R.drawable.cross,Color.RED);
            }

            //Si no dispone de conexion a internet, nos lanza un alertDialog que nos pregunta si queremos iniciar sesion como invitado
        }else{
            createAlertDialog("Parece que está sin conexión, ¿desea acceder como invitado?",intent);
        }

    }

    //Método para crear un alertDialog en esta actividad
    public void createAlertDialog(String title,Intent intent){
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        final CharSequence[] opciones = {"Aceptar","Cancelar"};
        alertDialog.setTitle(title);
        alertDialog.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Si pulsamos el boton de aceptar
                if(opciones[i].equals("Aceptar")){
                    utils.setPreferences("Invitado",sharedPreferences);
                    startActivity(intent);
                }
            }
        });
        alertDialog.show();
    }
    //Método para crear Toast personalizados
    public void createToast(String title, int icon,int backgroundcolor){
        new StyleableToast.Builder(NewAccount_Activity.this).text(title) //Texto del Toast y vista del mismo
                .backgroundColor(backgroundcolor).textColor(Color.BLACK) //Fondo y color de texto
                .iconStart(icon).show(); //Indicamos el icono del toast y lo mostramos
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
            Intent intent = new Intent(NewAccount_Activity.this, Home_Activity.class);

            if(s.equals("[]")){
                new NewAccount_Activity.insertUserTask().execute("GET","/insertUser.php?name="+textNameR.getText()+"&password="+textPasswordR.getText()+"&number="+textNumberR.getText()+"&adress="+textAdressR.getText()+"");

                //Iniciamos el intent pasandole el nombre de usuario
                utils.setPreferences(textNameR.getText().toString(), sharedPreferences);
                startActivity(intent);
            }else{
                createToast("El usuario ya existe!", R.drawable.cross, Color.RED);
            }
        }
    }

    //Metodo para crear la tarea asincrona
    private class insertUserTask extends AsyncTask<String, Void, String> {
        String result;

        //Indicamos la funcion de la tarea asincrona, que será hacer peticiones GET a la API
        @Override
        protected String doInBackground(String... strings) {
            result = HttpConnect.getRequest(strings[1]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            createToast("Usuario creado correctamente!",R.drawable.tick,Color.GREEN);
        }
    }
}