package com.example.myfavoriteferm;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//ОКНО С АВТОРИЗАЦИЕЙ
public class Activity_regist extends AppCompatActivity {

    private Database_users db;
    Cursor userCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        db = new Database_users(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void SignUp(View v) throws NoSuchAlgorithmException {
        SQLiteDatabase database=db.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        switch (v.getId()) {
            case R.id.but_regist:
                String edit_email = ((EditText) findViewById(R.id.email)).getText().toString();
                String edit_psw = ((EditText) findViewById(R.id.psw)).getText().toString();
                String edit_name = ((EditText) findViewById(R.id.name)).getText().toString();
                //ПРОВЕРКА, ЧТОБЫ ВСЕ ПОЛЯ БЫЛИ ЗАПОЛНЕНЫ
                if (edit_email == null || edit_psw == null || edit_name == null) {
                    Toast.makeText(this, "Все поля обязательные к заполнению", Toast.LENGTH_SHORT).show();
                    break;
                }
                //ПРОВЕРКА, ЧТО ТАКОГО ЛОГИНА НЕТ В БД
                try {
                    //ЕСЛИ ЕСТЬ, ТО ПЛОХО
                    userCursor = database.rawQuery("SELECT * FROM USERS " + " WHERE " + Database_users.KEY_EMAIL_USERS + " =?", new String[]{edit_email});
                    if (userCursor.getCount() >= 1) {
                        Toast.makeText(getApplicationContext(), "Пользователь с таким логином уже существует", Toast.LENGTH_SHORT).show();
                        EditText email = (EditText) findViewById(R.id.email);
                        email.setText("");
                        break;
                    }
                } catch (Exception e) {
                }
                //ТУДУ: функция на наличие пробелов, странных знаком, наличие соб@чки
                contentValue.put(Database_users.KEY_EMAIL_USERS, edit_email);
                contentValue.put(Database_users.KEY_PASSWORD_USERS, HASH_SHA256(edit_psw));
                contentValue.put(Database_users.KEY_NAME_USERS, edit_name);
                database.insert(Database_users.TABLE_USERS, null, contentValue);
                //ЗАПИСЬ В БД
                Toast.makeText(getApplicationContext(), edit_email + " - " + HASH_SHA256(edit_psw) + " - " + edit_name, Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Новый аккаунт зарегистрирован успешно", Toast.LENGTH_SHORT).show();
                //userCursor = database.rawQuery("SELECT * FROM USERS "+ " WHERE " + Database_users.KEY_EMAIL_USERS + " =?", new String[] {edit_email});
                db.close();
                database.close();
                //Toast.makeText(getApplicationContext(), "Новый аккаунт зарегистрирован успешно", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, Activity_auth.class);
                startActivity(intent);
        }
    }
    //ХЕШИРУЕМ ПАРОЛЬ ПЕРЕД ТЕМ, КАК ЗАПИСАТЬ В БД
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String HASH_SHA256(String psw) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        md.update(psw.getBytes(StandardCharsets.UTF_16));
        byte[] digest = md.digest();

        String hex = String.format("%064x", new BigInteger(1, digest));
        return hex;
    }
}