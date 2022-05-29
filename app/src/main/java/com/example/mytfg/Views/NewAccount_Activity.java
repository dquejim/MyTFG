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

import androidx.appcompat.app.AppCompatActivity;

import com.example.mytfg.Control.DB_Management;
import com.example.mytfg.Control.Utils;
import com.example.mytfg.R;

import io.github.muddz.styleabletoast.StyleableToast;

public class NewAccount_Activity extends AppCompatActivity {

    //Declaracion de variables
    Button bRegister;
    EditText textNameR;
    EditText textPasswordR;
    EditText textNumberR;
    EditText textAdressR;

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
                String password = textPasswordR.getText().toString();
                String number = textNumberR.getText().toString();
                String adress = textAdressR.getText().toString();

                //Llamamos al metodo checkUser con opcion 2, que nos dirá si el usuario ya existe
                String userCheck = db_management.checkUser(user, password, 2);

                //Si el usuario no existe, lo añade a la BBDD , lanza un toast e inicia el intent
                if(userCheck == null){
                    if(db_management.insertUser(user,password,number,adress) != -1){
                        createToast("Usuario creado correctamente",R.drawable.tick,Color.GREEN);
                        utils.setPreferences(user,sharedPreferences);
                        startActivity(intent);
                    }
                }else{
                    createToast("El usuario ya existe.",R.drawable.cross,Color.RED);
                }
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
}