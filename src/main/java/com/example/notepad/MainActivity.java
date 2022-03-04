package com.example.notepad;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
Button add,search,recycle,date;
    ListView listView;
    TextView et;
    Spinner kindspinner;
    List<Map<String,Object>> datas;
    SimpleAdapter adapter;
    private static final String[] countriesStr = {"全部","生活","娱乐","学习","工作"};
    private ArrayAdapter<String> Adapter;
    private List<String> allCountries;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.onResume();
        readsort();
        kindspinner = findViewById(R.id.kindspinner);
        kindspinner.setAdapter(Adapter);
        add=findViewById(R.id.add);
        search=findViewById(R.id.search);
                recycle=findViewById(R.id.recycle);
                listView=findViewById(R.id.listview);
                date=findViewById(R.id.date);
                et=findViewById(R.id.et);
                kindspinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        if(parent.getSelectedItem().toString()=="全部"){
                            read();
                        }
                        else{
                            readother();
                        }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                PopupMenu  menu=new PopupMenu(MainActivity.this,view);
                getMenuInflater().inflate(R.menu.item,menu.getMenu());
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case R.id.del:
                                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage("是否确认删除？").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Map m=datas.get(position);
                                        int _id= (int) m.get("_id");
                                        String title= (String) m.get("title");
                                        String text= (String) m.get("text");
                                        String myspinner= (String) m.get("myspinner");
                                        String time= (String) m.get("time");
                                        SQLiteDatabase db=openOrCreateDatabase("mynotes.db",
                                                Context.MODE_PRIVATE,null);
                                        if(db.delete("mynotes","_id=?",
                                                new String[]{_id+""})>0){
                                            datas.remove(position);
                                            adapter.notifyDataSetChanged();

                                            SQLiteDatabase db2=openOrCreateDatabase("recycle.db",
                                                    Context.MODE_PRIVATE,null);
                                            ContentValues value=new ContentValues();
                                            value.put("title",title);
                                            value.put("text",text);
                                            value.put("myspinner",myspinner);
                                            value.put("time",time);
                                            db2.insert("recycle",null,value);

                                        }


                                    }
                                }).setNegativeButton("取消",null).show();
                                break;
                            case R.id.modify:
                                Map<String,Object> m=datas.get(position);
                                Intent intent=new Intent(MainActivity.this,ModifyActivity.class);
                                intent.putExtra("myspinner", (String) m.get("myspinner"));
                                intent.putExtra("title", (String) m.get("title"));
                                intent.putExtra("text", (String) m.get("text"));
                                intent.putExtra("_id", (int) m.get("_id"));
                                startActivity(intent);
                                break;
                        }
                        return true;
                    }
                });
                menu.show();
                return true;
            }
        });
    }


    public void click(View view){
        switch (view.getId()){
            case R.id.add:
                Intent intent=new Intent(MainActivity.this,addActivity.class);
                startActivity(intent);
                initsort();
                break;
            case R.id.search:
                Intent intent1=new Intent(MainActivity.this,searchActivity.class);
                startActivity(intent1);
                break;
            case R.id.recycle:
                Intent intent2 =new Intent(MainActivity.this,recycleActivity.class);
                startActivity(intent2);
                initrecycle();
                break;

        }
    }
    public void selectdate(View v){
        Calendar c = Calendar.getInstance();
        new DateActivity(MainActivity.this, 0, new DateActivity.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear,
                                  int startDayOfMonth
            ) {
                String textString = String.format("%d.%02d.%02d", startYear,
                        startMonthOfYear + 1, startDayOfMonth);
                et.setText(textString);

            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), true).show();
    }

    public void searchyByDate(View v){
        String ti=et.getText().toString();
        datas=new ArrayList<>();
        SQLiteDatabase db=openOrCreateDatabase("mynotes.db",
                Context.MODE_PRIVATE,null);
        Cursor cursor=db.query("mynotes", null,"time like?",new String[]{"%"+ti+"%"},null,null,null);
        while (cursor.moveToNext()){
            Map<String,Object> map=new HashMap<>();
            map.put("_id",cursor.getInt(cursor.getColumnIndex("_id")));
            map.put("myspinner",cursor.getString(cursor.getColumnIndex("myspinner")));
            map.put("title",cursor.getString(cursor.getColumnIndex("title")));
            map.put("text",cursor.getString(cursor.getColumnIndex("text")));
            map.put("time",cursor.getString(cursor.getColumnIndex("time")));
            datas.add(map);
        }
        adapter=new SimpleAdapter(this,datas,R.layout.item1,
                new String[]{"myspinner","title","text","time"},new int[]{R.id.kind,R.id.title,R.id.text,R.id.time});
        listView.setAdapter(adapter);
        cursor.close();

    }
    private void readsort(){
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
        super.onResume();
        Adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,allCountries);

        Adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
    }


    private void read(){
        super.onResume();
        datas=new ArrayList<>();
        SQLiteDatabase db=openOrCreateDatabase("mynotes.db",
                Context.MODE_PRIVATE,null);
        Cursor cursor=db.query("mynotes", null,null,null,null,null,null);
        while (cursor.moveToNext()){
            Map<String,Object> map=new HashMap<>();
            map.put("_id",cursor.getInt(cursor.getColumnIndex("_id")));
            map.put("myspinner",cursor.getString(cursor.getColumnIndex("myspinner")));
            map.put("title",cursor.getString(cursor.getColumnIndex("title")));
            map.put("text",cursor.getString(cursor.getColumnIndex("text")));
            map.put("time",cursor.getString(cursor.getColumnIndex("time")));
            datas.add(map);
        }
        adapter=new SimpleAdapter(this,datas,R.layout.item1,
                new String[]{"myspinner","title","text","time"},new int[]{R.id.kind,R.id.title,R.id.text,R.id.time});
        listView.setAdapter(adapter);
        cursor.close();
    }
    private void readother(){
        super.onResume();
        datas=new ArrayList<>();
        SQLiteDatabase db=openOrCreateDatabase("mynotes.db",
                Context.MODE_PRIVATE,null);
        Cursor cursor=db.query("mynotes", null,"myspinner=?",new String[]{ kindspinner.getSelectedItem().toString() },null,null,null);
        while (cursor.moveToNext()){
            Map<String,Object> map=new HashMap<>();
            map.put("_id",cursor.getInt(cursor.getColumnIndex("_id")));
            map.put("myspinner",cursor.getString(cursor.getColumnIndex("myspinner")));
            map.put("title",cursor.getString(cursor.getColumnIndex("title")));
            map.put("text",cursor.getString(cursor.getColumnIndex("text")));
            map.put("time",cursor.getString(cursor.getColumnIndex("time")));
            datas.add(map);
        }
        adapter=new SimpleAdapter(this,datas,R.layout.item1,
                new String[]{"myspinner","title","text","time"},new int[]{R.id.kind,R.id.title,R.id.text,R.id.time});
        listView.setAdapter(adapter);
        cursor.close();
    }

    protected void onResume(){
        super.onResume();
        readsort();
        read();

    }
    private void initrecycle() {
        SQLiteDatabase db=openOrCreateDatabase("recycle.db",
                Context.MODE_PRIVATE,null);
        db.execSQL("create table if not exists recycle(_id INTEGER PRIMARY KEY autoincrement,"
                +"myspinner varchar,title varchar,text varchar,time varchar)");
    }
    private void initsort() {
        SQLiteDatabase db=openOrCreateDatabase("sort.db",
                Context.MODE_PRIVATE,null);
        db.execSQL("create table if not exists sort(_id INTEGER PRIMARY KEY autoincrement,"
                +"sort varchar)");

    }

}
