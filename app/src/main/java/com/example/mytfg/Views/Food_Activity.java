package com.example.mytfg.Views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytfg.Control.DB_Management;
import com.example.mytfg.Control.HttpConnect;
import com.example.mytfg.Control.RecyclerAdapter;
import com.example.mytfg.Control.Utils;
import com.example.mytfg.Models.Product;
import com.example.mytfg.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//Actividad para administrar la carta del restaurante, en la que podremos ver los productos que ofrece,
//sus ingredientes y elegir uno como nuestro favorito
public class Food_Activity extends AppCompatActivity {

    //Declaración de variables
    BottomNavigationView bottomNavigationView;

    CardView sandwich_option,pizza_option,camp_option,burguer_option, potatoe_option,bread_option;

    ImageView infoPopUp;
    
    RecyclerView recyclerView;
    RecyclerAdapter recAdapter;

    LinearLayoutManager layoutManager;
    ConstraintLayout presentationLayout;

    ArrayList<Product> productList = new ArrayList<>();
    DB_Management db_management;
    Utils utils = new Utils();
    SharedPreferences sharedPreferences;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.food_activity);
        //Escondemos el ActionBar de la App, ya que no lo usarmos
        getSupportActionBar().hide();

        //Creamos un nuevo objeto para conectarnos a la base de datos
        db_management = new  DB_Management(getBaseContext());

        //Hacemos uso del método de la clase Utils para obtener el usuario con el que estamos logueados
        sharedPreferences  = getSharedPreferences("user", MODE_PRIVATE);
        userName = utils.getPreferences(sharedPreferences);

        //Iniciamos los componentes gráficos y algunos procesos necesarios
        initComponents();

        //Listeners de los distintos clickables de la actividad
        View.OnClickListener listener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                switch (v.getId())
                {
                    case R.id.sandwichButton:
                        loadMenuOptions("Sandwiches",sandwich_option);
                        break;

                    case R.id.pizzaButton:
                        loadMenuOptions("Pizzas",pizza_option);
                        break;

                    case R.id.campButton:
                        loadMenuOptions("Camperos",camp_option);
                        break;

                    case R.id.burguerButton:
                        loadMenuOptions("Hamburguesas",burguer_option);
                        break;

                    case R.id.potatoeButton:
                        loadMenuOptions("Patatas", potatoe_option);
                        break;

                    case R.id.breadButton:
                        loadMenuOptions("Bocadillos",bread_option);
                        break;

                    default:
                        break;
                }
            }
        };

        recAdapter.setOnClickListener(listener);
        sandwich_option.setOnClickListener(listener);
        pizza_option.setOnClickListener(listener);
        camp_option.setOnClickListener(listener);
        bread_option.setOnClickListener(listener);
        potatoe_option.setOnClickListener(listener);
        burguer_option.setOnClickListener(listener);


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                loadMenu(item);
                return true;
            }
        });

        recAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDetails(view);
            }
        });
    }

    private void initComponents(){
        bottomNavigationView = findViewById(R.id.menu);
        bottomNavigationView.setSelectedItemId(R.id.food_option);

        sandwich_option = findViewById(R.id.sandwichButton);
        pizza_option = findViewById(R.id.pizzaButton);
        camp_option = findViewById(R.id.campButton);
        burguer_option = findViewById(R.id.burguerButton);
        potatoe_option = findViewById(R.id.potatoeButton);
        bread_option = findViewById(R.id.breadButton);

        infoPopUp = findViewById(R.id.infoPopUp);

        recyclerView = (RecyclerView) findViewById(R.id.myRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        presentationLayout = findViewById(R.id.presentationLayout);

        //Le indicamos a nuestro recyclerView el adaptador a usar, así como su layout
        recAdapter = new RecyclerAdapter(productList);
        recyclerView.setAdapter(recAdapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    //Método que carga el menú para navegar entre acticidades
    private void loadMenu(MenuItem item){
        Intent intent = null;

        switch (item.getItemId()){
            case R.id.user_option:
                intent = new Intent(Food_Activity.this, PersonalizeUser_Activity.class);
                break;

            case R.id.home_option:
                intent = new Intent(Food_Activity.this, Home_Activity.class);
                break;

            case R.id.food_option:
                intent = new Intent(Food_Activity.this, Food_Activity.class);
                break;
        }

        startActivity(intent);
        //Eliminamos la animación entre transiciones del intent
        overridePendingTransition(0,0);
    }

    //Taréa asincrona que obtiene el menu cuando estamos conectados a Internet
    private class getOnlineMenu extends AsyncTask<String, Void, String> {
        String result;

        //Indicamos la funcion de la tarea asincrona, que será hacer peticiones GET a la API
        @Override
        protected String doInBackground(String... strings) {
            result = HttpConnect.getRequest(strings[1]);

            try {
                if (result != null) {
                    JSONArray jsonArr = new JSONArray(result);

                    for (int i = 0; i < jsonArr.length(); i++){

                        JSONObject jsonObject = jsonArr.getJSONObject(i);

                        //Obtenemos los datos de interes de nuestro objeto JSON
                        String id = jsonObject.getString("number");
                        String name = jsonObject.getString("category");
                        String week_day = jsonObject.getString("product");
                        String price = jsonObject.getString("price");
                        String description = jsonObject.getString("description");

                        //Creamos un objeto producto y lo añadimos a la lista
                        productList.add(new Product(id, name, week_day, price,description));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }

        //Indicamos la funcion a realizar para cuando el proceso haya terminado
        @Override
        protected void onPostExecute(String s) {
            recAdapter.notifyDataSetChanged();
        }
    }

    //Taréa asincrona que obtiene el menu cuando no estamos conectados a Internet
    private class getOffLineMenu extends AsyncTask<String, Void, String> {
        String result = "";

        //Indicamos la funcion de la tarea asincrona, que será hacer peticiones GET a la API
        @Override
        protected String doInBackground(String... strings) {
            //LLamada a la base de datos
            productList = (ArrayList<Product>) db_management.getProducts(strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            //Volvemos a sobrecargar el adaptador, ya que al pasar de online a offline y viceversa podría dar problemas
            recAdapter = new RecyclerAdapter(productList);
            recyclerView.setAdapter(recAdapter);
            recyclerView.setLayoutManager(layoutManager);
            recAdapter.notifyDataSetChanged();

            recAdapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDetails(view);
                }
            });
        }
    }

    //Método que limpia la lista de productos y gestiona la carga del menu, dependiendo de si disponemos de conexión a Internet o no
    private void loadMenuOptions(String menu_option,CardView selectedView){
        productList.clear();
        presentationLayout.setVisibility(View.GONE);

        sandwich_option.setCardBackgroundColor(Color.parseColor("#FDECEF"));
        pizza_option.setCardBackgroundColor(Color.parseColor("#FDECEF"));
        burguer_option.setCardBackgroundColor(Color.parseColor("#FDECEF"));
        potatoe_option.setCardBackgroundColor(Color.parseColor("#FDECEF"));
        bread_option.setCardBackgroundColor(Color.parseColor("#FDECEF"));
        camp_option.setCardBackgroundColor(Color.parseColor("#FDECEF"));

        selectedView.setCardBackgroundColor(Color.parseColor("#D33E43"));

        if(utils.checkInternetConnection(getBaseContext())) {
            new getOnlineMenu().execute("GET", "/selectFood.php?category=\"" + menu_option + "\"");
        }else{
            new getOffLineMenu().execute(menu_option);
        }
    }

    //Método para iniciar un "POP UP" con la descripcion del producto y el boton de Me Gusta
    private void showDetails(View view){
       int position = recyclerView.getChildAdapterPosition(view);
       Intent intent = new Intent(Food_Activity.this,popUpActivity.class);
       intent.putExtra("Product", productList.get(position));
       startActivity(intent);
    }

}
