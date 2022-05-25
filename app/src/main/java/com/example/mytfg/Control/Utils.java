package com.example.mytfg.Control;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {

    //Método que comprueba si el usuario dispone de conexión a Internet
    public boolean comprobarInternet(Context context){
        boolean connected = false;

        //Creamos un objeto connectivityManager, lo usamos para obtener información de las conexiones activas
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo red = connectivityManager.getActiveNetworkInfo();

        //Si hay redes activas y hay conexion a Internet,devuelve true, sino, devuelve false
        if(red != null && red.isConnected()) {
            connected = true;
        }

        return connected;
    }

    public void setPreferences(String content,SharedPreferences sharedPreferences){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user",content);
        editor.commit();
    }

    public String getPreferences(SharedPreferences sharedPreferences){
        String user = sharedPreferences.getString("user","Invitado001");
        return user;
    }
}
