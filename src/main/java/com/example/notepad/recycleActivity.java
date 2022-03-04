package com.example.notepad;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class recycleActivity extends AppCompatActivity {
    ListView listView;
    List<Map<String,Object>> datas;
    SimpleAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle);
        listView=findViewById(R.id.recycle);
        read();
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                PopupMenu menu=new PopupMenu(recycleActivity.this,view);
                getMenuInflater().inflate(R.menu.item1,menu.getMenu());
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case R.id.del:
                                AlertDialog.Builder builder=new AlertDialog.Builder(recycleActivity.this);
                                builder.setMessage("是否确认删除？").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Map m=datas.get(position);
                                        int _id= (int) m.get("_id");
                                        SQLiteDatabase db=openOrCreateDatabase("recycle.db",
                                                Context.MODE_PRIVATE,null);
                                        if(db.delete("recycle","_id=?",
                                                new String[]{_id+""})>0){
                                            datas.remove(position);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                }).setNegativeButton("取消",null).show();
                                break;
                            case R.id.recover:
                                Map m=datas.get(position);
                                int _id= (int) m.get("_id");
                                String title= (String) m.get("title");
                                String text= (String) m.get("text");
                                String myspinner= (String) m.get("myspinner");
                                String time= (String) m.get("time");
                                SQLiteDatabase db=openOrCreateDatabase("recycle.db",
                                        Context.MODE_PRIVATE,null);
                                if(db.delete("recycle","_id=?",
                                        new String[]{_id+""})>0){
                                    datas.remove(position);
                                    adapter.notifyDataSetChanged();
                                }
                                SQLiteDatabase db2=openOrCreateDatabase("mynotes.db",
                                        Context.MODE_PRIVATE,null);
                                ContentValues value=new ContentValues();
                                value.put("title",title);
                                value.put("text",text);
                                value.put("myspinner",myspinner);
                                value.put("time",time);
                                db2.insert("mynotes",null,value);
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
    private void read(){
        datas=new ArrayList<>();
        SQLiteDatabase db=openOrCreateDatabase("recycle.db",
                Context.MODE_PRIVATE,null);
       Cursor cursor=db.query("recycle",null,null,null,null,null,null);
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
