package com.shycoder.android.travelmantics;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class DealsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deals);

        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.my_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        getDealsList();
    }

    private void getDealsList() {
        Query query = db.collection("deals");

        FirestoreRecyclerOptions<DealsResponse> response = new FirestoreRecyclerOptions.Builder<DealsResponse>()
                .setQuery(query, DealsResponse.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<DealsResponse, DealsHolder>(response) {
            @Override
            public void onBindViewHolder(DealsHolder holder, int position, DealsResponse model) {
                holder.title.setText(model.getTitle());
                holder.price.setText(model.getPrice());
                holder.desc.setText(model.getDescription());
                Glide.with(getApplicationContext())
                        .load(model.getImageUrl())
                        .into(holder.imageView);

            }

            @Override
            public DealsHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.deals_list, group, false);

                return new DealsHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

    }

    public class DealsHolder extends RecyclerView.ViewHolder {
       TextView title;
       TextView price;
       TextView desc;
       ImageView imageView;

        public DealsHolder(View itemView) {
            super(itemView);

            title = findViewById(R.id.deal_title);
            price = findViewById(R.id.deal_price);
            desc = findViewById(R.id.deal_desc);
            imageView = findViewById(R.id.deal_image);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                return true;

            case R.id.dashboard:
                Intent intent = new Intent(DealsActivity.this, AddDealActivity.class);
                startActivity(intent);

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(DealsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
