package com.example.notepad;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class searchActivity extends AppCompatActivity {
    EditText editText;
    Button button;
    ListView listView;
    List<Map<String,Object>> datas;
    SimpleAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        button=findViewById(R.id.search);
        editText=findViewById(R.id.edittext);
        listView=findViewById(R.id.listview);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSearch();
            }
        });
    }


    private void selectSearch() {
        String mt=editText.getText().toString();

        datas=new ArrayList<>();
        SQLiteDatabase db=openOrCreateDatabase("mynotes.db",
                Context.MODE_PRIVATE,null);
        Cursor cursor=db.query("mynotes",null,"text like?",new String[]{"%"+mt+"%"},null,null,null,null);
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
}
