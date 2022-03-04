package com.example.notepad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class addActivity extends AppCompatActivity {
    private static final String[] countriesStr = {"工作","生活","娱乐","学习"};
    private TextView myTextView;
    private EditText myEditText;
    private Button myButton_add;
    private Button myButton_remove;
    private Spinner mySpinner;
    private ArrayAdapter<String> adapter;
    private List<String> allCountries;
    EditText title,text;
    String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        allCountries = new ArrayList<String>();
        for(int i=0;i<countriesStr.length;i++)
        {
            allCountries.add(countriesStr[i]);
        }
        SQLiteDatabase db=openOrCreateDatabase("sort.db",
                Context.MODE_PRIVATE,null);
        Cursor cursor=db.query("sort", null,null,null,null,null,null);
        while (cursor.moveToNext()){
            allCountries.add(cursor.getString(cursor.getColumnIndex("sort")));
        }
        //新建ArrayAdapter对象并将allCountries传入
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,allCountries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //以findViewById()取的对象
        myTextView = (TextView)findViewById(R.id.myTextView);
        myEditText = (EditText)findViewById(R.id.myEditText);
        myButton_add=(Button)findViewById(R.id.myButton_add);
        myButton_remove = (Button)findViewById(R.id.myButton_remove);
        mySpinner = (Spinner)findViewById(R.id.myspinner);
        title=findViewById(R.id.title);
        text=findViewById(R.id.text);
        init();

        //将ArrayAdapter添加到Spinner对象中
        mySpinner.setAdapter(adapter);
        myButton_add.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newCountry = myEditText.getText().toString();
                SQLiteDatabase db=openOrCreateDatabase("sort.db",
                        Context.MODE_PRIVATE,null);
                ContentValues value=new ContentValues();

                for(int i=0;i<adapter.getCount();i++)
                {
                    if(newCountry.equals(adapter.getItem(i))){
                        return;
                    }
                }
                if(!newCountry.equals(""))
                {
                    value.put("sort",newCountry);
                    db.insert("sort",null,value);

                    adapter.add(newCountry);
                    int position = adapter.getPosition(newCountry);
                    //将Spinner选择在添加的值的位置
                    mySpinner.setSelection(position);
                    myEditText.setText("");
                }
            }
        });

        //为myButton_remove添加OnclickListener
        myButton_remove.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db=openOrCreateDatabase("sort.db",
                        Context.MODE_PRIVATE,null);
                if(mySpinner.getSelectedItem()!=null)
                {
                    db.delete("sort","sort=?",
                            new String[]{mySpinner.getSelectedItem().toString()+""});

                    adapter.remove(mySpinner.getSelectedItem().toString());

                    myEditText.setText("");
                    if(adapter.getCount()==0)
                    {
                        //将myTextView清空
                        myTextView.setText("");
                    }
                }
            }
        });


        mySpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //将所选mySpinner的值带入myTextView中
                myTextView.setText(parent.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    public void click(View v){
        switch (v.getId()){
            case R.id.save:
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
                Date date = new Date(System.currentTimeMillis());
                SQLiteDatabase db=openOrCreateDatabase("mynotes.db",
                        Context.MODE_PRIVATE,null);
                if(title.getText().toString().trim().equals("")){
                    mTitle=simpleDateFormat.format(date);
                }
                else{
                        mTitle=title.getText().toString();
                    }
                String mTime=simpleDateFormat.format(date);
                String mText=text.getText().toString();
                String mKind=mySpinner.getSelectedItem().toString();
                ContentValues value=new ContentValues();
                value.put("title",mTitle);
                value.put("text",mText);
                value.put("myspinner",mKind);
                value.put("time",mTime);
                db.insert("mynotes",null,value);
                Toast.makeText(addActivity.this,"保存成功",
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void init() {
        SQLiteDatabase db=openOrCreateDatabase("mynotes.db",
                Context.MODE_PRIVATE,null);
        db.execSQL("create table if not exists mynotes(_id INTEGER PRIMARY KEY autoincrement,"
                +"myspinner varchar,title varchar,text varchar,time varchar)");

    }
}

