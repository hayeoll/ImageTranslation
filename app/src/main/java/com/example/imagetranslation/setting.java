package com.example.imagetranslation;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class setting extends AppCompatActivity {
    Button okBtn;
    Button cancleBtn;

    ListView listview;
    ListItemAdapter adapter;
    ListItem item;

    public static Context context_setting;
    public int pos;
    public String language;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        context_setting = this;

        // list view 참조
        listview = findViewById(R.id.languageList);
        // adapter 참조
        adapter = new ListItemAdapter();

        listview.setAdapter(adapter);
        listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        adapter.addItem(new ListItem("عربي", "Arabic"));
        adapter.addItem(new ListItem("中国人", "Chinese"));
        adapter.addItem(new ListItem("English", "English"));
        adapter.addItem(new ListItem("Français", "French"));
        adapter.addItem(new ListItem("Deutsch", "German"));
        adapter.addItem(new ListItem("हिंदी", "Hindi"));
        adapter.addItem(new ListItem("Italiano", "Italian"));
        adapter.addItem(new ListItem("日本語", "Japanese"));
        adapter.addItem(new ListItem("Монгол", "Mongolian"));
        adapter.addItem(new ListItem("Pусский", "Russian"));
        adapter.addItem(new ListItem("ไทย", "Thai"));
        adapter.addItem(new ListItem("Türk", "Turkish"));


        okBtn = findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check된 언어 정보 넘김
                int count;
                int checked = -1;
                count = adapter.getCount() ;

                if (count > 0) {
                    // 현재 선택된 아이템의 position 획득.
                    checked = listview.getCheckedItemPosition();
                }

                // 선택된 언어 정보 저장
                item = (ListItem) adapter.getItem(checked);
                language = item.getLanguage();


                pos = checked;

                if (checked < 0) {
                    Toast.makeText(getApplicationContext(), "언어 선택 안됨", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "현재 선택된 언어는 " + language + "입니다.", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("position", checked);
                intent.putExtra("language", language);
                startActivity(intent);
            }
        });

        cancleBtn = findViewById(R.id.cancleBtn);
        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "언어 선택 안됨", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

    }
}