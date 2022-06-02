package com.example.mytfg.Control;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.example.mytfg.Models.Offer;

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

public class Utils {

    ArrayList<Offer> offerList = new ArrayList<>();

    //Método que comprueba si el usuario dispone de conexión a Internet
    public boolean comprobarInternet(Context context) {
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

    public void setPreferences(String content, SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user", content);
        editor.commit();
    }

    public String getPreferences(SharedPreferences sharedPreferences) {
        String user = sharedPreferences.getString("user", "Invitado");
        return user;
    }

    public String dayChanger(String old_day){
        String new_day = "0";

        if(old_day.equals("1")){
            new_day = "7";
        }else{
            new_day = String.valueOf(Integer.parseInt(old_day) - 1);
        }

        return new_day;
    }



}
