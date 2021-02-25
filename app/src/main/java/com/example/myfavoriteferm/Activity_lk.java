package com.example.myfavoriteferm;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

//ЛИЧНЫЙ КАБИНЕТ
public class Activity_lk extends AppCompatActivity {
    private String login;
    private Database_users db;
    private TextView name, logintext;
    private Cursor userCursor;
    private ListView fieldsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lk);

        //ПОЛУЧАЕМ ЛОГИН ОТ ПРОШЛОГО АКТИВИТИ, ЧТОБЫ ОТОБРАЗИТЬ ТОЛЬКО ЕГО ДАННЫЕ
        Bundle arguments = getIntent().getExtras();
        login = arguments.get("secretkey").toString();

        logintext=(TextView)findViewById(R.id.textView_login);
        name=(TextView)findViewById(R.id.textView_name);
        logintext.setText(login);
        fieldsList=(ListView)findViewById(R.id.list);
        getdata();
    }

    //ВЫТАСКИВАЕМ ЗАПРОСАМИ ДАННЫЕ ЭТОГО ПОЛЬЗОВАТЕЛЯ
    private void getdata()
    {
        db = new Database_users(this);
        SQLiteDatabase database=db.getWritableDatabase();
        userCursor =  database.rawQuery("select * from "+ Database_users.TABLE_FIELDS+" WHERE " + Database_users.KEY_IDUSERS + " =?", new String[]{login});
        String[] headers = new String[] {Database_users.KEY_NAME_FIELDS, Database_users.KEY_SIZE_FIELDS};
        SimpleCursorAdapter userAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item,
                userCursor, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);
        fieldsList.setAdapter(userAdapter);
    }
}