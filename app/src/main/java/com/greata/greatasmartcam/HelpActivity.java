package com.greata.greatasmartcam;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class HelpActivity extends AppCompatActivity {

    private ListView helpList;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        helpList = (ListView) findViewById(R.id.help_qa_list);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, getResources().getStringArray(R.array.list_help_q));
        helpList.setAdapter(arrayAdapter);
        getSupportActionBar().setTitle("常見問題");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}
