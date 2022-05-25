package com.example.mytfg.Views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mytfg.Control.DB_Management;
import com.example.mytfg.Control.Utils;
import com.example.mytfg.R;

import io.github.muddz.styleabletoast.StyleableToast;

public class Login_Activity extends AppCompatActivity {

    //Declaración de variables
    TextView bRegister;
    Button bLogin;
    EditText textName;
    EditText textPassword;
    DB_Management db_management = new DB_Management(this);
    Utils utils = new Utils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        //Escondemos el actionBar de la actividad
        getSupportActionBar().hide();

        //"Enlazamos" los componentes graficos con las variables creadas anteriormente
        bRegister = (TextView) findViewById(R.id.bRegister);
        bLogin = (Button) findViewById(R.id.bLogin);
        textName = (EditText) findViewById(R.id.textName);
        textPassword = (EditText) findViewById(R.id.textPassword);

        SharedPreferences sharedPreferences = getSharedPreferences("user",MODE_PRIVATE);

        //Comprobamos si el usuario Invitado001 ya existe, si no existe, lo creamos, ya que nos conectaremos a él cuando no dispongamos de conexion
        if(db_management.checkUser("Invitado001", "", 2) == null){
            //Insertamos en la base de datos el usuario al que nos conectaremos cuando no haya conexión
            db_management.insertUser("Invitado001", "user", "333", "Default");
        }

        //Accion al pulsar el botón de registrarse de la pantalla de login
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(utils.comprobarInternet(getBaseContext())){
                    //Si disponemos de internet, nos enviará a la pantalla de registro de usuario
                    Intent intent = new Intent(Login_Activity.this, NewAccount_Activity.class);
                    startActivity(intent);
                    //Si no, nos pregunta si queremos entrar como invitado
                }else{
                    Intent intent = new Intent(Login_Activity.this, Home_Activity.class);
                    createAlertDialog("Parece que está sin conexión, ¿desea acceder como invitado?",intent);
                }
            }
        });

        //Accion del boton de login
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Declaramos un intent hacia la home_activity
                Intent intent = new Intent(Login_Activity.this, Home_Activity.class);

                //Si disponemos de internet obtenemos el usuario y contraseña introducidos
                if(utils.comprobarInternet(getBaseContext())){
                    if(!textName.getText().toString().isEmpty() && !textPassword.getText().toString().isEmpty()) {
                        String user = textName.getText().toString();
                        String password = textPassword.getText().toString();

                        //Comprobamos si la contraseña pertenece al usuario con la opcion 1 del método checkUser
                        if (db_management.checkUser(user, password, 1) != null) {

                            //Lanzamos un toast de login correcto
                            createToast("Login correcto!",R.drawable.tick,Color.GREEN);

                            //Iniciamos el intent pasandole el nombre de usuario
                            utils.setPreferences(user,sharedPreferences);
                            startActivity(intent);

                            //Si no coindide la contraseña con el usuario, nos lanza un toast de error
                        } else {
                            createToast("Usuario o contraseña incorrectos.",R.drawable.cross,Color.RED);
                        }
                    }else{
                        createToast("Usuario o contraseña incorrectos.",R.drawable.cross,Color.RED);
                    }

                    //Si no hay conexion a Internet, nos pregunta si queremos conectarnos como usuario
                }else{
                    createAlertDialog("Parece que está sin conexión, ¿desea acceder como invitado?",intent);
                }
            }
        });

    }

    //Método para crear un alertdialog
    public void createAlertDialog(String title,Intent intent){
        SharedPreferences sharedPreferences = getSharedPreferences("user",MODE_PRIVATE);
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        final CharSequence[] opciones = {"Si","No"};
        alertDialog.setTitle(title);
        alertDialog.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(opciones[i].equals("Si")){
                    utils.setPreferences("Invitado001",sharedPreferences);
                    startActivity(intent);
                }
            }
        });

        alertDialog.show();

    }

    //Método para crear Toast personalizados
    public void createToast(String title, int icon,int backgroundcolor){
        new StyleableToast.Builder(Login_Activity.this).text(title) //Texto del Toast y vista del mismo
                .backgroundColor(backgroundcolor).textColor(Color.BLACK) //Fondo y color de texto
                .iconStart(icon).show(); //Indicamos el icono del toast y lo mostramos
    }
}