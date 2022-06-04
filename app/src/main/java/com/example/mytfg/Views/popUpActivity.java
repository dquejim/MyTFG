package com.example.mytfg.Views;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mytfg.Control.HttpConnect;
import com.example.mytfg.Control.Utils;
import com.example.mytfg.Models.Product;
import com.example.mytfg.Models.User;
import com.example.mytfg.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//Actividad POP UP que se lanzará para mostrarnos los ingredientes y cosas varias del menu.

//Para el POP UP, se ha cambiado su tema para eliminar el espacio sobrante de la actividad y dar esa sensación de ventana emergente
//Ver res/values/styles.xml
public class popUpActivity extends AppCompatActivity {

    //Declaración de variables
    TextView txtProductDescription;
    ImageButton heartButton;

    Product product;
    SharedPreferences sharedPreferences;
    String userName;
    User myUser;
    Utils utils = new Utils();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_pop_up);

        //Obtenemos el usuario con el que estamos conectados
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        userName = (utils.getPreferences(sharedPreferences));
        //Obtenemos el objeto Producto que hemos pasado a la actividad en el Intent
        product = (Product) getIntent().getSerializableExtra("Product");

        initComponents();

    }

    //Iniciamos componentes uniendolos con la vista,además de variables/procesos
    public void initComponents() {
        txtProductDescription = findViewById(R.id.txtProductDescription);
        heartButton = findViewById(R.id.heartButton);

        //Obtenemos las medidas de la ventana
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        //Obtenemos el ancho y alto en base al objeto DisplayMetrics
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        //Indicamos las medidas de nuestra ventana POP UP
        getWindow().setLayout((int) (width * 0.75), (int) (height * 0.15));

        //Si nuestro objeto no tiene descripcion, pondremos su nombre en su lugar
        if(product.getDescription().equals("-")){
            txtProductDescription.setText(product.getProduct());
        }else{
            txtProductDescription.setText(product.getDescription());
        }

        //Si disponemos de conexion a Internet y no estamos como invitado, lanzamos una tarea asincrona
        if (utils.checkInternetConnection(getBaseContext()) && !userName.equals("Invitado")) {
            new popUpActivity.getUserTask().execute("GET","/selectUser.php?user=\""+userName+"\"");
        }
    }

    //Tarea asincrona con la que obtendremos el usuario y haremos varias acciones
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
                        String fav_food = jsonObject.getString("fav_food");

                        //Cargamos los datos del local a nuestro objeto
                        myUser = new User(user, password, number, adress, fav_food);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }

        //Indicamos al proceso lo que deberá hacer una vez haya terminado
        @Override
        protected void onPostExecute(String s) {
            //Si nuestro producto está marcado como favorito por el usuario, usaremos un icono diferente
            if(product.getProduct().equals(myUser.getFav_food())){
                heartButton.setImageResource(R.drawable.red_heart_ico);
            }else{
                heartButton.setImageResource(R.drawable.black_heart_ico);
            }

            //Listener y accion del boton de Me Gusta
            heartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setAsFavourite(myUser.getFav_food());
                }
            });
        }
    }

    //Tarea asincrona para actualizar un usuario, poniendole como favorio el procuto en cuestion
    private class updateUserTask extends AsyncTask<String, Void, String> {
        String result;

        //Indicamos la funcion de la tarea asincrona, que será hacer peticiones GET a la API
        @Override
        protected String doInBackground(String... strings) {
            result = HttpConnect.getRequest(strings[0]);
            return result;
        }
    }

    //Método de gestión del botón de Me Gusta
    private void setAsFavourite(String fav_food){
        //Si nuestro producto ya es e favorito
        if(fav_food.equals(product.getProduct())){
            //Eliminamos de favorito el producto del usuario en cuestión
            new popUpActivity.updateUserTask().execute("/updateUser.php?password=\""+myUser.getPassword()+
                    "\"&number=\""+myUser.getNumber()+"\"&adress=\""+myUser.getAdress()+"\"&name=\""+myUser.getName()+
                    "\"&fav_food=\"-\"" );

            //Indicamos al botón que ha de cambiar su icono
            heartButton.setImageResource(R.drawable.black_heart_ico);

            //Cambiamos al usuario local la comida favortia.(Si no es así, abría que llamar otra vez a la base de datos con
            // el usuario para que el botón funcione correctamente)
            myUser.setFav_food("-");

            //Si el usuario no tiene marcado como favorita esa comida
        }else{
            //Actualizamos y la ponemos como favorita
            new popUpActivity.updateUserTask().execute("/updateUser.php?password=\""+myUser.getPassword()+
                    "\"&number=\""+myUser.getNumber()+"\"&adress=\""+myUser.getAdress()+"\"&name=\""+myUser.getName()+
                    "\"&fav_food=\""+product.getProduct()+"\"" );

            //Indicamos al botón que ha de cambiar su icono
            heartButton.setImageResource(R.drawable.red_heart_ico);

            //Cambiamos al usuario local la comida favortia.(Si no es así, abría que llamar otra vez a la base de datos con
            // el usuario para que el botón funcione correctamente)
            myUser.setFav_food(product.getProduct());
        }
    }
}

