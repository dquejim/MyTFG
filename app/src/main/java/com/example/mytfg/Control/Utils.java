package com.example.mytfg.Control;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.example.mytfg.Models.Offer;
import com.example.mytfg.R;
import com.example.mytfg.Views.Food_Activity;
import com.example.mytfg.Views.Home_Activity;
import com.example.mytfg.Views.PersonalizeUser_Activity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import org.json.*;

import io.github.muddz.styleabletoast.StyleableToast;

public class Utils {

    //Método que comprueba si el usuario dispone de conexión a Internet
    public boolean checkInternetConnection(Context context) {
        boolean connected = false;

        //Creamos un objeto connectivityManager, lo usamos para obtener información de las conexiones activas
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo red = connectivityManager.getActiveNetworkInfo();

        //Si hay redes activas y hay conexion a Internet,devuelve true, sino, devuelve false
        if (red != null && red.isConnected()) {
            connected = true;
        }

        return connected;
    }

    //Método con el que podemos guadar el nombre de usuario en las preferencias
    public void setPreferences(String content, SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user", content);
        editor.commit();
    }

    //Método con el que podemos obtener el nombre de usuario de las preferencias
    public String getPreferences(SharedPreferences sharedPreferences) {
        String user = sharedPreferences.getString("user", "Invitado");
        return user;
    }

    //Método para gestionar días de la semana
    public String dayChanger(String old_day){
        String new_day = "0";

        if(old_day.equals("1")){
            new_day = "7";
        }else{
            new_day = String.valueOf(Integer.parseInt(old_day) - 1);
        }

        return new_day;
    }

    //Método para lanzar un toast
    public void createToast(String title, int icon,int backgroundcolor,Context context){
        new StyleableToast.Builder(context).text(title) //Texto del Toast y vista del mismo
                .backgroundColor(backgroundcolor).textColor(Color.BLACK) //Fondo y color de texto
                .iconStart(icon).show(); //Indicamos el icono del toast y lo mostramos
    }
}
