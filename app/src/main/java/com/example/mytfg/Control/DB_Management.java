package com.example.mytfg.Control;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    //Login - variables
    private String tableLogin = "login_table";
    private String tbLogin_userColumn = "user";
    private String tbLogin_passwordColumn = "password";
    private String tbLogin_numberColumn = "number";
    private String tbLogin_adressColumn = "adress";

    //Ofertas - variables
    private String tableOffer = "offer_table";
    private String tbOffer_idColumn= "id";
    private String tbOffer_nameColumn = "name";
    private String tbOffer_dayColumn = "week_day";
    private String tbOffer_priceColumn = "price";

    //Datos del local - variables
    private String tableLocalData= "localData_table";
    private String tbLocal_idColumn = "id";
    private String tbLocal_ubicationColumn = "ubication";
    private String tbLocal_adressColumn = "adress";

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
                tbLogin_adressColumn + " TEXT)";

        CREATE_TABLE_OFFER = "CREATE TABLE " + tableOffer + "(" +
                tbOffer_idColumn +" TEXT," +
                tbOffer_nameColumn +" TEXT," +
                tbOffer_dayColumn +" TEXT," +
                tbOffer_priceColumn + " TEXT)";

        CREATE_TABLE_DATA = "CREATE TABLE " + tableLocalData + "(" +
                tbLocal_idColumn +" TEXT," +
                tbLocal_ubicationColumn +" TEXT," +
                tbLocal_adressColumn +" TEXT)";


        sqLiteDatabase.execSQL(CREATE_TABLE_LOGIN);
        sqLiteDatabase.execSQL(CREATE_TABLE_OFFER);
        sqLiteDatabase.execSQL(CREATE_TABLE_DATA);


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

    //Método pars obtener todos los datos de un usuario
    public User getUser(String myUser){
        SQLiteDatabase db = this.getReadableDatabase();
        User results = null;
        String[] columns = new String[]{tbLogin_userColumn,tbLogin_passwordColumn,tbLogin_numberColumn,tbLogin_adressColumn};

        //Abrimos cursor con todos los resultados de la consulta
        Cursor c = db.query(tableLogin,columns,tbLogin_userColumn+"=?", new String[]{myUser},null,null,null);

        //Si hay datos en nuestro cursor, obtenemos todos los datos de la columna y tabla indicadas
        if(c.moveToFirst()){
            do{
                results = new User(c.getString(0),c.getString(1),c.getString(2),c.getString(3));
            }while(c.moveToNext());
        }

        db.close();
        //Devolvemos los resultados
        return results;
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

    public void alterUser(String user, String password, String number, String adress){
        String update = "UPDATE " + tableLogin + " SET " + tbLogin_passwordColumn + " = '" + password +
                "' , " + tbLogin_numberColumn + " = '" + number + "' , " + tbLogin_adressColumn + " = '" + adress +
                "' WHERE " + tbLogin_userColumn + " = '" + user + "' ;";

        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(update);
        db.close();
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
        insertOfferData("L102","Ración de patatas frita \nSandwich completo","1","6.5€");

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
    public long insertLocalData(String id, String ubication, String adress){

        SQLiteDatabase db = this.getReadableDatabase();
        long query_result = -1;

        ContentValues values = new ContentValues();

        //Valores a insertar en la tabla
        values.put(tbLocal_idColumn,id);
        values.put(tbLocal_ubicationColumn, ubication);
        values.put(tbLocal_adressColumn,adress);

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
        String[] columns = new String[]{tbLocal_idColumn,tbLocal_ubicationColumn,tbLocal_adressColumn};

        //Abrimos cursor con todos los resultados de la consulta
        Cursor c = db.query(tableLocalData,columns,tbLocal_idColumn+"=?", new String[]{id},null,null,null);

        //Si hay datos en nuestro cursor, obtenemos todos los datos de la columna y tabla indicadas
        if(c.moveToFirst()){
            do{
                localList = new Local(c.getString(0),c.getString(1),c.getString(2));
                results.add(localList);
            }while(c.moveToNext());
        }

        db.close();

        //Devolvemos los resultados
        return results;
    }
}
