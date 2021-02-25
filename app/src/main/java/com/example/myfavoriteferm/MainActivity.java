package com.example.myfavoriteferm;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

//ОКНО ВЫБОРА: АВТОРИЗАЦИЯ ИЛИ РЕГИСТРАЦИЯ
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__menu);
       // db = new Database_users(this);
    }

    public void Choose(View view){
        Intent intent;
        switch (view.getId()) {
            case R.id.button_lk:
                intent = new Intent(this, Activity_auth.class);
                startActivity(intent);
                break;
                //fragment = new Fragment_auth();
            case R.id.button_regist:
                intent = new Intent(this, Activity_regist.class);
                startActivity(intent);
                break;
        }
        //FragmentManager fm = getFragmentManager();
        //FragmentTransaction ft = fm.beginTransaction();
        //ft.replace(R.id.fr_place, fragment);
        //ft.commit();
    }
}
