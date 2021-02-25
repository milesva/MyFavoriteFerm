package com.example.myfavoriteferm;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//ОКНО С АВТОРИЗАЦИЕЙ
public class Activity_auth extends AppCompatActivity {

    private Database_users db;
    Cursor userCursor;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
    }

    //СОБЫТИЕ НА КНОПКУ "ВОЙТИ"
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void SignIn(View v) {
        switch (v.getId()) {
            case R.id.button_signlk:
                db = new Database_users(this);
                String edit_username = ((EditText)(findViewById(R.id.email))).getText().toString();
                String edit_password =((EditText)(findViewById(R.id.psw))).getText().toString();
                //ПРОВЕРКА,ЧТОБЫ ВСЕ ПОЛЯ БЫЛИ ЗАПОЛЕНЫ
                if (edit_username.length() <= 1 || edit_password.length() <= 1) {
                    Toast.makeText(v.getContext(), "Все поля обязательные к заполнению", Toast.LENGTH_SHORT).show();
                    return;
                }
                database = db.getReadableDatabase();
                //ПРОВЕРЯЮ, ЕСТЬ ЛИ ТАКОЙ ЛОГИН И ПАРОЛЬ В БД
                try {
                    userCursor = database.rawQuery("SELECT "+Database_users.KEY_PASSWORD_USERS+" FROM " + Database_users.TABLE_USERS + " WHERE " + Database_users.KEY_EMAIL_USERS + " = '" + edit_username+"' AND " + Database_users.KEY_PASSWORD_USERS + " = '" + HASH_SHA256(edit_password)+"';", null);
                } catch (Exception e) {
                    //ЗАПИСИ НЕТ В БД
                    Toast.makeText(this, "Данные введены неверно", Toast.LENGTH_SHORT).show();
                    EditText email = (EditText)findViewById(R.id.email);
                    email.setText("");
                    return;
                }
                //АУТЕНТИФИКАЦИЯ ПРОШЛА УСПЕШНО
                Toast.makeText(this, "Вы вошли в личный кабинет", Toast.LENGTH_SHORT).show();
                //ПЕРЕХОД В АКТИВИТИ С РИСОВАНИЕМ НА КАРТЕ
                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtra("secretkey", edit_username);
                startActivity(intent);
                break;
        }
    }
    //ПАРОЛЬ ХРАНИТСЯ В БД ХЕШИРОВАННЫЙ, ПОЭТОМУ, ЧТОБЫ СРАВНИТЬ ВВЕДЕНЫЙ ПАРОЛЬ ПОЛЬЗОВАТЕЛЯ С ЗАПИСЬЮ В БД, ХЕШИРУЕМ ВВЕДЕННЫЙ ПАРОЛЬ
    //В ОБЩЕМ, ДЛЯ КОРРЕКТНОГО СРАВНЕНИЯ
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String HASH_SHA256(String psw) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(psw.getBytes(StandardCharsets.UTF_16));
        byte[] digest = md.digest();
        String hex = String.format("%064x", new BigInteger(1, digest));
        return hex;
    }

}