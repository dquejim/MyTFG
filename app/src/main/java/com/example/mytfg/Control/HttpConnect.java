package com.example.mytfg.Control;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//Conexion con nuestra API Rest
public class HttpConnect {
    //Indicamos la url base,que nunca cambia en nuestras peticiones
    private static String URL_BASE = "http://iesayala.ddns.net/dquesada";

    //Metodo para pedir datos a la api, donde le añadimos el resto de la url como parametro
    public static String getRequest(String strUrl){
        //Iniciamos una conexion
        HttpURLConnection http = null;
        String content = null;

        try {
            //Creamos una url concatenando la base con la obtenida por parametros
            URL url = new URL(URL_BASE + strUrl );

            //Realizamos la conexion e indicamos que obtendremos un json
            http = (HttpURLConnection)url.openConnection();
            http.setRequestProperty("Content-Type", "application/json");
            http.setRequestProperty("Accept", "application/json");

            //Si el codigo de respuesta es 200, significa que nuestra peticion ha sido exitosa
            if( http.getResponseCode() == HttpURLConnection.HTTP_OK ) {

                StringBuilder sb = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader( http.getInputStream()));
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                content = sb.toString();
                reader.close();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        //Nos desconectamos de la API
        finally {
            if( http != null ) http.disconnect();
        }
        //Devolvemos el contenido de la consulta
        return content;
    }
}
