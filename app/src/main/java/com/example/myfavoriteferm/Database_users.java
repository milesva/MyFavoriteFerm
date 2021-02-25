package com.example.myfavoriteferm;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

//СТРУКТУРА БД
/* 2 ТАБЛИЦЫ: ИНФА О ЧЕЛОВЕКЕ, ИНФА  О ПОЛЕ
СВЯЗЬ 1 КО МНОГИМ, ТО ЕСТЬ В ТАБЛИЦЕ С ИНФОЙ О ПОЛЯМИ ВНЕДРЕН АЙДИШНИК ЮЗЕРА
 */

public class Database_users extends SQLiteOpenHelper  {
    public static final int DB_VERSION =9; //:)
    public static final String NAME_DB = "DBferm";

    //СТОЛБЦЫ ТАБЛИЦЫ ПОЛЬЗОВАТЕЛЯ
    public static final String TABLE_USERS= "USERS";
    public static final String KEY_ID_USERS = "_id";
    public static final String KEY_EMAIL_USERS = "EMAIL";
    public static final String KEY_PASSWORD_USERS = "PASSWORD";
    public static final String KEY_NAME_USERS = "NAME";

    //СТОЛБЦЫ ТАБЛИЦЫ ПОЛЯ
    public static final String TABLE_FIELDS = "FIELDS";
    public static final String KEY_ID_FIELDS = "_id";
    public static final String KEY_IDUSERS = "ID_USERS";
    public static final String KEY_NAME_FIELDS = "NAME_FIELD";
    public static final String KEY_SIZE_FIELDS = "SIZE_FIELD";
    public static final String KEY_SORT_FIELDS = "SORT_FIELD";
    public static final String KEY_POINTS_FIELDS = "POINTS";
    public static final String KEY_CULTIVATION_FIELDS = "CULTIVATION_FIELD";


    public Database_users(Context context)
    {
        super(context,NAME_DB,null,DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
       /* db.execSQL("CREATE TABLE "+TABLE_USERS
                + "("+KEY_ID_USERS+" INTEGER PRIMARY KEY AUTOINCREMENT, " +KEY_EMAIL_USERS+ "TEXT, " +KEY_PASSWORD_USERS+ "TEXT,  "+KEY_NAME_USERS+"TEXT)");*/
        db.execSQL("CREATE TABLE USERS (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "EMAIL TEXT, "
                + "PASSWORD TEXT, "
                + "NAME TEXT);");
        /*db.execSQL("CREATE TABLE "+TABLE_FIELDS
                + "("+KEY_ID_FIELDS+" INTEGER PRIMARY KEY AUTOINCREMENT, " +KEY_IDUSERS+ "INTEGER, "+KEY_NAME_FIELDS+"TEXT, "+KEY_SIZE_FIELDS+ "TEXT, "+KEY_SORT_FIELDS+ "TEXT, "+KEY_CULTIVATION_FIELDS+ "TEXT, "+KEY_POINTS_FIELDS+ "TEXT"+")");
 */
        db.execSQL("CREATE TABLE FIELDS (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "ID_USERS TEXT, "
                + "NAME_FIELD TEXT, "
                + "SIZE_FIELD TEXT, "
                + "SORT_FIELD TEXT, "
                + "CULTIVATION_FIELD TEXT, "
                + "POINTS TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       db.execSQL("DROP TABLE IF EXISTS "+TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_FIELDS);
        onCreate(db);
    }

}
