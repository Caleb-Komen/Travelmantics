package com.foozenergy.travelmantics;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class TravelAdapter extends RecyclerView.Adapter<TravelAdapter.TravelHolder> {

    Context context;
    ArrayList<Travel> travelList;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ChildEventListener childEventListener;

    public TravelAdapter(Context context) {
        this.context = context;

        firebaseDatabase = FirebaseUtil.firebaseDatabase;
        databaseReference = FirebaseUtil.databaseReference;
        travelList = FirebaseUtil.travels;
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Travel travel = dataSnapshot.getValue(Travel.class);
                travelList.add(travel);
                travel.setId(dataSnapshot.getKey());
                notifyItemInserted(travelList.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        databaseReference.addChildEventListener(childEventListener);
    }

    @NonNull
    @Override
    public TravelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.travel_item_list, parent, false);
        return new TravelHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TravelHolder holder, int position) {
        Travel travel = travelList.get(position);

        Glide.with(context)
                .load(travel.getPhotoUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .override(200, 200)
                .into(holder.photo);
        holder.title.setText(travel.getTitle());
        holder.price.setText(travel.getPrice());
        holder.description.setText(travel.getDescription());
    }

    @Override
    public int getItemCount() {
        return travelList.size();
    }

    class TravelHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView price;
        TextView description;
        ImageView photo;

        public TravelHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
            description = itemView.findViewById(R.id.description);
            photo = itemView.findViewById(R.id.photo);

            itemView.setOnClickListener(onClickListener);
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                Travel travel = travelList.get(position);
                Intent intent = new Intent(context, InsertActivity.class);
                intent.putExtra(InsertActivity.TRAVEL_DEAL, travel);
                context.startActivity(intent);
            }
        };
    }
}
