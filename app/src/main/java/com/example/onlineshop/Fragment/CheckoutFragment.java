package com.example.onlineshop.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.onlineshop.Domain.ItemsModel;
import com.example.onlineshop.Domain.OrderModel;
import com.example.onlineshop.Domain.UserModel;
import com.example.onlineshop.R;
import com.example.onlineshop.databinding.FragmentCheckoutBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CheckoutFragment extends Fragment {

    private FragmentCheckoutBinding binding;
    private ArrayList<ItemsModel> cartItems;
    private double totalAmount;

    public static CheckoutFragment newInstance(ArrayList<ItemsModel> items, double total) {
        CheckoutFragment fragment = new CheckoutFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("cart_items", items);
        args.putDouble("total_amount", total);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cartItems = getArguments().getParcelableArrayList("cart_items");
            totalAmount = getArguments().getDouble("total_amount");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.totalAmountTxt.setText("Total to be paid: $" + String.format("%.2f", totalAmount));

        setupButtons();
    }

    private void setupButtons() {
        binding.backBtn.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        binding.confirmOrderBtn.setOnClickListener(v -> placeOrder());
    }

    private void placeOrder() {
        String address = binding.addressEdt.getText().toString().trim();
        String phone = binding.phoneEdt.getText().toString().trim();

        if (address.isEmpty()) {
            binding.addressEdt.setError("Address is required");
            binding.addressEdt.requestFocus();
            return;
        }
        if (phone.isEmpty()) {
            binding.phoneEdt.setError("Phone number is required");
            binding.phoneEdt.requestFocus();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserModel currentUser = snapshot.getValue(UserModel.class);

                    DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders").child(userId);
                    String orderId = ordersRef.push().getKey();

                    OrderModel newOrder = new OrderModel();
                    newOrder.setOrderId(orderId);
                    newOrder.setDate(System.currentTimeMillis());
                    newOrder.setTotalAmount(totalAmount);
                    newOrder.setItems(cartItems);
                    newOrder.setUserId(userId);
                    newOrder.setDeliveryAddress(address);
                    newOrder.setPhoneNumber(phone);

                    if (currentUser != null) {
                        newOrder.setUserName(currentUser.getName());
                        newOrder.setUserProfileImageUrl(currentUser.getProfileImageUrl());
                    }

                    if (orderId != null) {
                        ordersRef.child(orderId).setValue(newOrder).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                clearCart(userId);
                            } else {
                                binding.progressBar.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Failed to place order.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Could not retrieve user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearCart(String userId) {
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Carts").child(userId);
        cartRef.removeValue().addOnCompleteListener(task -> {
            binding.progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Order placed successfully!", Toast.LENGTH_LONG).show();

            if (getParentFragmentManager() != null) {
                getParentFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .commit();
            }
        });
    }
}