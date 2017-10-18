package com.greata.greatasmartcam;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;


public class AddDeviceActivity extends AppCompatActivity {

    private AddFragment f1, f2, f3;
    private FragmentManager fManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        f1 = AddFragment.newInstance(R.layout.fragment_one);
        f2 = AddFragment.newInstance(R.layout.fragment_two);
        f3 = AddFragment.newInstance(R.layout.fragment_three);
        fManager = getFragmentManager();
        fManager.beginTransaction().add(R.id.frameFragment, f1).commit();

    }

    @Override
    protected void onStart() {
        super.onStart();
        List<String> list = new ArrayList<String>();

        list.add("利优视Wifi");
        list.add("利优视Wifi HD版");
        list.add("利优云监控");
        list.add("利优云监控 S");
        list.add("利优云监控 2S");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        Spinner sp = (Spinner) f1.getView().findViewById(R.id.spinner);
        sp.setAdapter(adapter);


    }

    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.button_next:
                fManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).hide(f1).add(R.id.frameFragment, f2).addToBackStack(null).commit();
                break;
            case R.id.button_next_2:
                fManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).hide(f2).add(R.id.frameFragment, f3).addToBackStack(null).commit();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                ;
        }
        return super.onOptionsItemSelected(item);
    }
}
