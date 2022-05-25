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