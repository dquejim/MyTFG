package com.example.mytfg.Control;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mytfg.Models.Product;
import com.example.mytfg.Models.Local;
import com.example.mytfg.Models.Offer;
import com.example.mytfg.Models.User;

import java.util.ArrayList;
import java.util.List;


public class DB_Management extends SQLiteOpenHelper{
    private static final String DB_NAME = "db_tfg";
    private static final int CURRENT_VERSION = 1;
    private String CREATE_TABLE_LOGIN = "";
    private String CREATE_TABLE_OFFER = "";
    private String CREATE_TABLE_DATA = "";
    private String CREATE_TABLE_MENU = "";
    private String CREATE_TABLE_DELIVERY = "";

    //Tabla de usuarios - variables
    private String tableLogin = "login_table";
    private String tbLogin_userColumn = "user";
    private String tbLogin_passwordColumn = "password";
    private String tbLogin_numberColumn = "number";
    private String tbLogin_adressColumn = "adress";
    private String tbLogin_favouriteColumn = "fav_food";

    //Tabla de ofertas - variables
    private String tableOffer = "offer_table";
    private String tbOffer_idColumn= "id";
    private String tbOffer_nameColumn = "name";
    private String tbOffer_dayColumn = "week_day";
    private String tbOffer_priceColumn = "price";

    //Tabla de datos del local - variables
    private String tableLocalData= "localData_table";
    private String tbLocal_idColumn = "id";
    private String tbLocal_ubicationColumn = "ubication";
    private String tbLocal_facebookColumn = "facebook";
    private String tbLocal_instagramColumn = "instagram";
    private String tbLocal_adressColumn = "adress";
    private String tbLocal_numberColumn = "number";

    //Tabla de menu - variables
    private String tableMenu= "menu_table";
    private String tbMenu_numberColumn = "number";
    private String tbMenu_categoryColumn = "category";
    private String tbMenu_productColumn = "product";
    private String tbMenu_priceColumn = "price";
    private String tbMenu_descriptionColumn = "description";

    private Context cContext;

    //Constructor de la base de datos que la creará o se conectará
    public DB_Management(Context context){
        super(context,DB_NAME,null,CURRENT_VERSION);
        cContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Sentencia para crear nuestra primera tabla
        CREATE_TABLE_LOGIN = "CREATE TABLE " + tableLogin + "(" +
                tbLogin_userColumn +" TEXT," +
                tbLogin_passwordColumn +" TEXT," +
                tbLogin_numberColumn +" TEXT," +
                tbLogin_adressColumn + " TEXT," +
                tbLogin_favouriteColumn + " TEXT )";

        CREATE_TABLE_OFFER = "CREATE TABLE " + tableOffer + "(" +
                tbOffer_idColumn +" TEXT," +
                tbOffer_nameColumn +" TEXT," +
                tbOffer_dayColumn +" TEXT," +
                tbOffer_priceColumn + " TEXT)";

        CREATE_TABLE_DATA = "CREATE TABLE " + tableLocalData + "(" +
                tbLocal_idColumn +" TEXT," +
                tbLocal_ubicationColumn +" TEXT," +
                tbLocal_facebookColumn +" TEXT," +
                tbLocal_instagramColumn +" TEXT," +
                tbLocal_adressColumn +" TEXT," +
                tbLocal_numberColumn +" TEXT)";

        CREATE_TABLE_MENU = "CREATE TABLE " + tableMenu + "(" +
                tbMenu_numberColumn +" TEXT," +
                tbMenu_categoryColumn +" TEXT," +
                tbMenu_productColumn +" TEXT," +
                tbMenu_priceColumn + " TEXT," +
                tbMenu_descriptionColumn+" TEXT)";

        sqLiteDatabase.execSQL(CREATE_TABLE_LOGIN);
        sqLiteDatabase.execSQL(CREATE_TABLE_OFFER);
        sqLiteDatabase.execSQL(CREATE_TABLE_DATA);
        sqLiteDatabase.execSQL(CREATE_TABLE_MENU);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    //Método para insertar un usuario en la BBDD
    public long insertUser(String user, String password, String number, String adress){

        SQLiteDatabase db = this.getReadableDatabase();
        long query_result = -1;

        ContentValues values = new ContentValues();

        //Valores a insertar en la tabla
        values.put(tbLogin_userColumn,user);
        values.put(tbLogin_passwordColumn, password);
        values.put(tbLogin_numberColumn,number);
        values.put(tbLogin_adressColumn, adress);

        //Instruccion para insertar en la tabla, indicando los valores y el nombre de la misma
        query_result = db.insert(tableLogin,null,values);

        insertAllOffers();

        db.close();

        return query_result;

    }

    //Método para verificar si el usuario existe, o si el usuario y la contraseña introducida coinciden
    public String checkUser(String myUser, String myPassword, int option){

        String result = null;
        //Nos conectamos a la base de datos
        SQLiteDatabase db = this.getReadableDatabase();
        //Indicamos las columnas que queremos obtener en nuestra consulta
        String[] cols = new String[]{ tbLogin_userColumn,tbLogin_passwordColumn };
        //Creamos el cursor con los datos de la consulta, en este caso solo nos devolverá un registro
        //Esto se debe a que buscamos los datos del usuario por su nombre, y al ser clave primaria no habrá dos usuarios con el mismo nombre
        Cursor c = db.query(tableLogin,cols,tbLogin_userColumn+"=?", new String[]{myUser},null,null,null);

        //Si el cursor recoge datos...
        if(c.moveToFirst()) {
            String searchedUser = c.getString(0);
            String searchedPassword = c.getString(1);

            switch (option) {
                //Para el boton login
                case 1:
                    //Si el usuario introducido coinicide con la contraseña, result nos devolverá el usuario
                    if (searchedUser.equals(myUser) && searchedPassword.equals(myPassword)) {
                        result = searchedUser;
                    }
                    break;
                //Para el boton register
                case 2:
                    //Si el usuario ya existe, nos devolvera su nombre
                    if (searchedUser.equals(myUser)) {
                        result = searchedUser;
                    }
                    break;
            }
        }

        //Cerramos el cursor
        if(c != null) {
            c.close();
        }
        //Cerramos la base de datos
        db.close();

        return result;
    }

    public void deleteDB(){
        cContext.deleteDatabase(DB_NAME);
    }

    private long insertOfferData(String id,String name,String day,String price){
        SQLiteDatabase db = this.getReadableDatabase();
        long query_result = -1;

        ContentValues values = new ContentValues();

        //Valores a insertar en la tabla
        values.put(tbOffer_idColumn,id);
        values.put(tbOffer_nameColumn, name);
        values.put(tbOffer_dayColumn,day);
        values.put(tbOffer_priceColumn, price);

        //Instruccion para insertar en la tabla, indicando los valores y el nombre de la misma
        query_result = db.insert(tableOffer,null,values);

        db.close();

        return query_result;
    }

    //Método pars obtener todos los datos de una oferta
    public List<Offer> getOffers(String weekDay){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Offer> results= new ArrayList<>();
        Offer offers = null;
        String[] columns = new String[]{tbOffer_idColumn,tbOffer_nameColumn,tbOffer_dayColumn,tbOffer_priceColumn};

        //Abrimos cursor con todos los resultados de la consulta
        Cursor c = db.query(tableOffer,columns,tbOffer_dayColumn+"=?", new String[]{weekDay},null,null,null);

        //Si hay datos en nuestro cursor, obtenemos todos los datos de la columna y tabla indicadas
        if(c.moveToFirst()){
            do{
                offers = new Offer(c.getString(0),c.getString(1),c.getString(2),c.getString(3));
                results.add(offers);
            }while(c.moveToNext());
        }

        db.close();

        //Devolvemos los resultados
        return results;
    }

    private void insertAllOffers(){
        insertOfferData("L101","Ensalada césar \nPollo asado","1","12€");
        insertOfferData("L102","Ración de patatas fritas \nSandwich completo","1","6.5€");

        insertOfferData("M101","Hamburguesa completa \nPostre a elegir","2","12€");
        insertOfferData("M102","Ración de patatas frita \nPollo asado","2","10€");

        insertOfferData("X101","Pizza volcán \nRefresco 1L","3","11€");
        insertOfferData("X102","Patata asada tropical \nHelado a elegir","3","8€");

        insertOfferData("J101","Aros de cebolla 10 u. \nAlitas de pollo","4","7.5€");
        insertOfferData("J102","Tempura de verduras \nPops de pollo 10 u.","4","7.5€");

        insertOfferData("V101","Pizza trufada \nRefresco 1L","5","12€");
        insertOfferData("V102","Rolling flamenco \nPostre a elegir","5","6€");

        insertOfferData("S101","Ensalada mixta \nBocadillo de jamón asado","6","12€");
        insertOfferData("S102","Patata asada rusa \nLata de refresco","6","5€");

        insertOfferData("D101","Pizza andaluza \nLambrusco","7","18€");
        insertOfferData("D102","Nuggets de pollo 20 u. \nRación de patatas fritas","7","10€");
    }

    //Método para insertar un local en la BBDD
    public long insertLocalData(String id, String ubication,String facebook_link,String instagram_link, String adress,String number){

        SQLiteDatabase db = this.getReadableDatabase();
        long query_result = -1;

        ContentValues values = new ContentValues();

        //Valores a insertar en la tabla
        values.put(tbLocal_idColumn,id);
        values.put(tbLocal_ubicationColumn, ubication);
        values.put(tbLocal_facebookColumn,facebook_link);
        values.put(tbLocal_instagramColumn,instagram_link);
        values.put(tbLocal_adressColumn,adress);
        values.put(tbLocal_numberColumn,number);

        //Instruccion para insertar en la tabla, indicando los valores y el nombre de la misma
        query_result = db.insert(tableLocalData,null,values);

        db.close();

        return query_result;

    }

    //Método para obtener todos los datos de un local
    public List<Local> getLocalData(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Local> results= new ArrayList<>();
        Local localList = null;
        String[] columns = new String[]{tbLocal_idColumn,tbLocal_ubicationColumn,tbLocal_facebookColumn,tbLocal_instagramColumn,tbLocal_adressColumn,tbLocal_numberColumn};

        //Abrimos cursor con todos los resultados de la consulta
        Cursor c = db.query(tableLocalData,columns,tbLocal_idColumn+"=?", new String[]{id},null,null,null);

        //Si hay datos en nuestro cursor, obtenemos todos los datos de la columna y tabla indicadas
        if(c.moveToFirst()){
            do{
                localList = new Local(c.getString(0),c.getString(1),c.getString(2),c.getString(3),c.getString(4),c.getString(5));
                results.add(localList);
            }while(c.moveToNext());
        }

        db.close();

        //Devolvemos los resultados
        return results;
    }

    //--------------------------------------------------------------------
    //Método para insertar un usuario en la BBDD
    public long insertProduct(String number, String category, String product, String price,String description){
        SQLiteDatabase db = this.getReadableDatabase();
        long query_result = -1;

        ContentValues values = new ContentValues();

        //Valores a insertar en la tabla
        values.put(tbMenu_numberColumn,number);
        values.put(tbMenu_categoryColumn, category);
        values.put(tbMenu_productColumn,product);
        values.put(tbMenu_priceColumn, price);
        values.put(tbMenu_descriptionColumn, description);

        //Instruccion para insertar en la tabla, indicando los valores y el nombre de la misma
        query_result = db.insert(tableMenu,null,values);

        insertAllOffers();

        db.close();

        return query_result;
    }

    //Método pars obtener todos los datos de una oferta
    public List<Product> getProducts(String category){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Product> results= new ArrayList<>();
        Product product = null;
        String[] columns = new String[]{tbMenu_numberColumn,tbMenu_categoryColumn,tbMenu_productColumn,tbMenu_priceColumn,tbMenu_descriptionColumn};

        //Abrimos cursor con todos los resultados de la consulta
        Cursor c = db.query(tableMenu,columns,tbMenu_categoryColumn+"=?", new String[]{category},null,null,null);

        //Si hay datos en nuestro cursor, obtenemos todos los datos de la columna y tabla indicadas
        if(c.moveToFirst()){
            do{
                product = new Product(c.getString(0),c.getString(1),c.getString(2),c.getString(3),c.getString(4));
                results.add(product);
            }while(c.moveToNext());
        }

        db.close();

        //Devolvemos los resultados
        return results;
    }

    public void insertAllMenu(){
        insertProduct("1","Sandwiches","Sandwich Mixto","3.30€",
                "Doble de Queso y Jamón York");
        insertProduct("2","Sandwiches","Sandwich Completo","4.50€",
                "Queso,Jamón York,Bacon,Huevo,Tomate y Lechuga");
        insertProduct("3","Sandwiches","Sandwich Vegetal","4.80€",
                "Queso,Alcachofas,Esparragos,Pimientos Asados,Huevo cocido,Tomate,Lechuga,Atún o Anchoas");
        insertProduct("4","Sandwiches","Sandwich Más Que Pizzas de Lomo","5.60€",
                "Doble de Queso,Jamón York,Huevo,Bacon,Tomate,Lechuga y Lomo");
        insertProduct("5","Sandwiches","Sandwich Más Que Pizzas de Pechuga","5.60€",
                "Doble de Queso,Jamón York,Huevo,Bacon,Tomate,Lechuga y Pechuga");


        insertProduct("6","Pizzas","Margarita","9.20€","" +
                "Tomate y Queso");
        insertProduct("7","Pizzas","Prosciutto","9.90€",
                "Jamón York y Champiñones");
        insertProduct("8","Pizzas","Napolitana","10.80€",
                "Anchoas,Alcaparras,Aceitunas,Cebolla,Bacon y Surtido de Verduras");
        insertProduct("9","Pizzas","Cuatro Quesos","10.20€",
                "Surtido de Quesos");
        insertProduct("10","Pizzas","Caprichosa","10.80€",
                "Jamón York,Alcachofas,Cebolla,Bacon y Surtid de Verduras");
        insertProduct("11","Pizzas","Cuatro Estaciones","10.80€",
                "Jamón York,Aceitunas Negras,Champiñones,Alcachofas y Atún");
        insertProduct("12","Pizzas","Barbacoa","10.80€",
                "Carne Picada,Cebolla,Nata,Huevo y Parmesano");
        insertProduct("13","Pizzas","Carbonara","10.80€",
                "Bacon,Cebolla,Nata,Huevo y Parmesano");
        insertProduct("14","Pizzas","Serrana","10.80€",
                "Jamón Serrano,Esparragos,Surtido de Setas y Champiñones");
        insertProduct("15","Pizzas","Marinera","11.30€",
                "Atún,Gambas,Salmón,Surtido de Setas y Champiñones");
        insertProduct("16","Pizzas","Volcán","11.40€",
                "Queso,Tomate y 5 Ingredientes a elegir");
        insertProduct("17","Pizzas","Diábolo","10.20€",
                "Chorizo Picante y Salsa Picante");
        insertProduct("18","Pizzas","Verduras", "10.20€",
                "Alcachofas,Cebolla,Pimientos,Esparragos y Tomates Secos");
        insertProduct("19","Pizzas","Siciliana","10.20€",
                "Pepperoni,Doble Queso y Huevos de Codorniz");
        insertProduct("20","Pizzas","Tropical","10.20€",
                "Jamón York,Bacon,Piña,Maíz");
        insertProduct("21","Pizzas","Más que pizzas","12€",
                "Bacon,Doble Pollo,Nata,Cebolla,Maíz");
        insertProduct("22","Pizzas","Trufada","12€",
                "Mozzarella,Rulo de cabra,Crema Trufada,Surtido de Setas");
        insertProduct("23","Pizzas","Ibérica","16€",
                "Crema de Espárragos,Mozzarella,Anchoas,Jamón,Surtido de Setas y Champiñones");
        insertProduct("24","Pizzas","Funghi","16€",
                "Crema de Setas,Mozzarella,Rulo de Cabra,Cebolla Caramelizada");
        insertProduct("25","Pizzas","Andaluza","16€",
                "Crema de Calabaza,Tomates Secos,Mozzarella,Jamón Ibérico,Parmesano");

        insertProduct("26","Camperos","Campero de Pollo","7.90€",
                "Filete de Pollo,Jamón York,Queso,Lechuga,Tomate y Cebolla");
        insertProduct("27","Camperos","Campero Serrano","7.90€",
                "Filete de Pollo,Jamón Serrano,Queso,Pimientos y Tomate");
        insertProduct("28","Camperos","Campero de Atún","7.80€",
                "Queso,Atún,Cebolla,Pimiento,Lechuga,Tomate y Alcaparras");
        insertProduct("29","Camperos","Campero Completo\"","7.90€",
                "Filete de Lomo,Quesp,Bacon,Huevo,Lechuga,Tomate y Cebolla");
        insertProduct("30","Camperos","Campero Más Que Pizzas","8.30€",
                "Filete de Lomo,Quesp,Bacon,Huevo,Lechuga,Tomate y Esparragos");
        insertProduct("31","Camperos","Campero Suave","7.50€",
                "Jamón York,Queso,Bacon,Tomate y Lechuga");
        insertProduct("32","Camperos","Campero de Lomo con Roquefort\"","7.50€",
                "Filete de Lomo,Queso,Bacon,Tomate,Lechuga y Salsa Roquefort");


        insertProduct("33","Hamburguesas","Cerdo simple con queso","3.30€","-");
        insertProduct("34","Hamburguesas","Cerdo simple con queso Completa","4.50€","-");
        insertProduct("35","Hamburguesas","Ternera extra simple con queso","5.10€","-");
        insertProduct("36","Hamburguesas","Ternera extra simple con queso COMPLETA","5.20€","-");
        insertProduct("37","Hamburguesas","Meat de vacuno mayor 250Gr","9.10€",
                "Mollete,Rulo de Cabra,Cebolla Caramelizada,Tomate y Lechuga");
        insertProduct("38","Hamburguesas","Doble ternera extra y doble queso","5.60€","-");


        insertProduct("39","Patatas","Simple","3.70€",
                "Sal,Pimienta y Aceite");
        insertProduct("40","Patatas","Roquefort","5.50€",
                "Salsa Roquefort,Mozzarella,jamón York,Maíz y Aceitunas");
        insertProduct("41","Patatas","Tropical","6.40€",
                "Salsa Rosa,Tronquitos de Mar,Gambas,Atún,Piña y Maíz");
        insertProduct("42","Patatas","Carbonara","5.50€",
                "Nata,Cebolla Dulce,Huevo Duro,Bacon y Mozzarella");
        insertProduct("43","Patatas","Primavera","5.50€",
                "Aceite,Cebolla Dulce,Atún,Pimientos Morrón y Huevo Duro");
        insertProduct("44","Patatas","Cuatro Quesos","6.10€",
                "Salsa 4 Quesos,Cuatro Quesos Rallados y Gratinados");
        insertProduct("45","Patatas","Suave","5.30€",
                "Mantequilla,Mozzarella,Jamón York y Orégano");
        insertProduct("46","Patatas","Rusa","5.20€",
                "Mayonesa,Atún,Pimiento Morron,Maíz y Aceituna");
        insertProduct("47","Patatas","Brava","5.20€",
                "Ali-Oli,Salsa Brava,Jamón York,Maíz,Zanahoria,Remolacha,Zanahoria y Aceitunas");
        insertProduct("48","Patatas","Tomaku","5.20€",
                "Tomate Frito,Queso,Jamón York,Atún y Maíz");
        insertProduct("49","Patatas","Al gusto","6.70€",
                "Salsa a ELegir + 8 Ingredientes");
        insertProduct("50","Patatas","Verano","5.50€",
                "Aceite de Ajo,Zanahoria,Remolacha,Maíz,Cebolla,Pimiento,Pepino,Atún,Maíz,Huevo Duro y Aceitunas");
        insertProduct("51","Patatas","Philadelphia","6.40€",
                "Queso Philadelphia,Salmón,Huevo Duro y Mozzarella");
        insertProduct("52","Patatas","Más que pizzas","6.20€",
                "Mayonesa,Queso,Jamón York,Maíz,Aceitunas,Cuatro Quesos Gratinados y Orégano");

        insertProduct("53","Bocadillos","Jamón Serrano","4.80€","-");
        insertProduct("54","Bocadillos","Queso 1/2 curación","4.80€","-");
        insertProduct("55","Bocadillos","Jamón Ibérico de cebo","4.50€","-");
        insertProduct("56","Bocadillos","Jamón Serrano y queso añejo","7.50€","-");
        insertProduct("57","Bocadillos","Jamón Serrano y queso 1/2 curación","5.60€","-");
        insertProduct("58","Bocadillos","Jamón Ibérico de cebo y queso añejo","5.20€","-");
        insertProduct("59","Bocadillos","Lomo simple con queso","7.90€","-");
        insertProduct("60","Bocadillos","Lomo simple con queso COMPLETO","4.80€","-");
        insertProduct("61","Bocadillos","Pechuga simple con queso","5.60€","-");
        insertProduct("62","Bocadillos","Lomo simple con queso COMPLETO","4.80€","-");
        insertProduct("63","Bocadillos","Pechuga simple con queso COMPLETO","5.60€","-");
    }

}
