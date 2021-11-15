package com.example.imagetranslation;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.ListView;

import java.util.ArrayList;

public class ListItemAdapter extends BaseAdapter {
    ArrayList<ListItem> items = new ArrayList<ListItem>();
    Context context;


    // ArrayList 크기 리턴
    @Override
    public int getCount() {
        return items.size();
    }

    // 해당 position 위치에 있는 item 리턴 -> 몇번째 item인지
    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    // position 리턴
    @Override
    public long getItemId(int position) {
        return position;
    }

    // position에 위치한 데이터를 화면에 출력하기 위한 메서드
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        context = parent.getContext();
        ListItem listItem = items.get(position);
        int pos = position;


        // listview item을 inflate하여 convertView 참조
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        // 데이터 참조
        TextView mothertongueTxt = convertView.findViewById(R.id.mothertongue);
        TextView languageTxt = convertView.findViewById(R.id.language);

        // 데이터 set
        mothertongueTxt.setText(listItem.getMotherthong());
        languageTxt.setText(listItem.getLanguage());

        return convertView;
    }

    public void addItem(ListItem item) {
        items.add(item);
    }

}
