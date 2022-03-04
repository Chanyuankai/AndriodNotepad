package com.example.notepad;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ModifyActivity extends AppCompatActivity {
    EditText myspinner,title,text;
    int _id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        myspinner=findViewById(R.id.myspinner);
        title=findViewById(R.id.title);
        text=findViewById(R.id.text);
        Intent intent=getIntent();
        myspinner.setText(intent.getStringExtra("myspinner"));
        title.setText(intent.getStringExtra("title"));
        text.setText(intent.getStringExtra("text"));
        _id=intent.getIntExtra("_id",-1);
    }
    public void click(View view){
        switch (view.getId()){
            case R.id.modify:
                SQLiteDatabase db=openOrCreateDatabase("mynotes.db",
                        Context.MODE_PRIVATE,null);
                ContentValues value=new ContentValues();
                value.put("myspinner",myspinner.getText().toString());
                value.put("title",title.getText().toString());
                value.put("text",text.getText().toString());
                if(db.update("mynotes",value,"_id=?",
                        new String[]{_id+""})>0){
                    Toast.makeText(ModifyActivity.this,"更新成功",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}