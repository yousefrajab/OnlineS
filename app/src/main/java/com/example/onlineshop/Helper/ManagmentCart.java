package com.example.onlineshop.Helper;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.onlineshop.Domain.ItemsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManagmentCart {

    private Context context;
    private FirebaseAuth mAuth;
    private DatabaseReference cartRef;

    public ManagmentCart(Context context) {
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            this.cartRef = FirebaseDatabase.getInstance().getReference("Carts").child(mAuth.getCurrentUser().getUid());
        }
    }

    public LiveData<ArrayList<ItemsModel>> getCartList() {
        MutableLiveData<ArrayList<ItemsModel>> liveData = new MutableLiveData<>();
        if (cartRef != null) {
            cartRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<ItemsModel> list = new ArrayList<>();
                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                        ItemsModel item = itemSnapshot.getValue(ItemsModel.class);
                        list.add(item);
                    }
                    liveData.setValue(list);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Failed to load cart.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return liveData;
    }


    public void insertItem(ItemsModel item) {
        if (cartRef == null) return;

        DatabaseReference itemRef = cartRef.child(item.getTitle());

        itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ItemsModel existingItem = snapshot.getValue(ItemsModel.class);
                    int newQuantity = existingItem.getNumberinCart() + item.getNumberinCart();
                    itemRef.child("numberinCart").setValue(newQuantity);
                } else {
                    itemRef.setValue(item);
                }
                Toast.makeText(context, "Added to your Cart", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Failed to add item.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void plusItem(ItemsModel item) {
        if (cartRef == null) return;
        DatabaseReference itemRef = cartRef.child(item.getTitle());
        int newQuantity = item.getNumberinCart() + 1;
        itemRef.child("numberinCart").setValue(newQuantity);
    }

    public void minusItem(ItemsModel item) {
        if (cartRef == null) return;
        DatabaseReference itemRef = cartRef.child(item.getTitle());

        if (item.getNumberinCart() == 1) {
            itemRef.removeValue();
        } else {
            int newQuantity = item.getNumberinCart() - 1;
            itemRef.child("numberinCart").setValue(newQuantity);
        }
    }

    public Double getTotalFee(ArrayList<ItemsModel> list) {
        double fee = 0;
        for (int i = 0; i < list.size(); i++) {
            fee = fee + (list.get(i).getPrice() * list.get(i).getNumberinCart());
        }
        return fee;
    }
}