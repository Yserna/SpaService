package com.example.diego.spaservice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Solicitudes extends AppCompatActivity {

    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitudes);

        listView = (ExpandableListView) findViewById(R.id.lvExp);
        initData();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listHash);
        listView.setAdapter(listAdapter);
    }

    private void initData() {
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();

        listDataHeader.add("Cliente 1");
        listDataHeader.add("Cliente 2");
        listDataHeader.add("Cliente 3");
        listDataHeader.add("Cliente 4");
        listDataHeader.add("Cliente 5");
        listDataHeader.add("Cliente 6");

        List<String> cl1 = new ArrayList<>();
        cl1.add("Pedicure");

        List<String> cl2 = new ArrayList<>();
        cl2.add("Manicure");
        cl2.add("Pedicure");

        List<String> cl3 = new ArrayList<>();
        cl3.add("Masaje tonificante");

        List<String> cl4 = new ArrayList<>();
        cl4.add("Pedicure");
        cl4.add("Digitopuntura");
        cl4.add("Cholaterapia");

        List<String> cl5 = new ArrayList<>();
        cl5.add("Pedicure");

        List<String> cl6 = new ArrayList<>();
        cl6.add("Pedicure");

        listHash.put(listDataHeader.get(0), cl1);
        listHash.put(listDataHeader.get(1), cl2);
        listHash.put(listDataHeader.get(2), cl3);
        listHash.put(listDataHeader.get(3), cl4);
        listHash.put(listDataHeader.get(4), cl5);
        listHash.put(listDataHeader.get(5), cl6);
    }
}
