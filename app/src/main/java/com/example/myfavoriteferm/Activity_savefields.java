package com.example.myfavoriteferm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

//ОКНО ДЛЯ ДОБАВЛЕНИЯ НОВОГО ПОЛЯ В БД
public class Activity_savefields extends AppCompatActivity {

    private String login_cord;
    private EditText cult,size,name,sort;
    private Database_users db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savefields);

        //ПЕРЕДАНО ОТ ПРОШЛОГО АКТИВИТИ ЛОГИН+КООРДИНАТЫ, КОТОРЫЕ НУЖНО ДОБАВИТЬ В БД
        Bundle arguments = getIntent().getExtras();
        login_cord = arguments.get("secretkey").toString();
        Toast.makeText(this, login_cord, Toast.LENGTH_SHORT).show();
        cult=(EditText)findViewById(R.id.EditText_cultivation);
        sort=(EditText)findViewById(R.id.EditText_sort);
        size=(EditText)findViewById(R.id.EditText_size);
        name=(EditText)findViewById(R.id.EditText_namefields);
    }
    private String login;

    //ВЫТАСКИВАЕМ ЛОГИН ИЗ ЛОГИН+КООРДИНАТЫ
   private String NeddLogin(String message)
   {
       String log="";
       for(int i=0;i<message.length();i++)
       {
           if(message.length()!='+')
               log+=message.charAt(i);
           else
               break;
       }
       return log;
   }
    public void ClickSave(View v) {
        switch (v.getId()) {
            case R.id.button_savefields:
                //ПРОВЕРКА НА ЗАПОЛНЕННОСТЬ ВСЕХ ПОЛЕЦ
                if (cult.getText() == null || size.getText() == null || name.getText() == null) {
                    Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                    break;
                }
                //ДОБАВЛЕНИЕ ДАННЫХ О НОВОМ ПОЛЕ В БД
                db = new Database_users(this);
                SQLiteDatabase database=db.getWritableDatabase();
                ContentValues contentValue = new ContentValues();

                contentValue.put(Database_users.KEY_NAME_FIELDS, name.getText().toString());
                contentValue.put(Database_users.KEY_CULTIVATION_FIELDS, cult.getText().toString());
                contentValue.put(Database_users.KEY_SIZE_FIELDS, size.getText().toString());
                contentValue.put(Database_users.KEY_SORT_FIELDS, sort.getText().toString());
                contentValue.put(Database_users.KEY_IDUSERS, login);
                database.insert(Database_users.TABLE_FIELDS, null, contentValue);
                Toast.makeText(getApplicationContext(), "Поле сохранено", Toast.LENGTH_SHORT).show();
                db.close();
                database.close();
                name.setText("");
                cult.setText("");
                size.setText("");
                sort.setText("");
                break;
        }
    }

}