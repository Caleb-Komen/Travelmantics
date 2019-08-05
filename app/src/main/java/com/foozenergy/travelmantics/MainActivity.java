package com.foozenergy.travelmantics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TravelAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.travel_list_menu, menu);
        MenuItem insertMenu = menu.findItem(R.id.travel_deal);
        if (FirebaseUtil.isAdmin) {
            insertMenu.setVisible(true);
        }
        else {
            insertMenu.setVisible(false);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.travel_deal:
                startActivity(new Intent(this, InsertActivity.class));
                return true;
            case R.id.logout:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                FirebaseUtil.attachAuthListener();
                            }
                        });
                FirebaseUtil.detachAuthListener();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtil.openFbReference("travel", this);
        adapter = new TravelAdapter(this);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        FirebaseUtil.attachAuthListener();
        showMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.detachAuthListener();
    }
    public void showMenu() {
        invalidateOptionsMenu();
    }
}
