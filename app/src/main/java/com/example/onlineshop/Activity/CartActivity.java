package com.example.onlineshop.Activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.onlineshop.Adapter.CartAdapter;
import com.example.onlineshop.Domain.ItemsModel;
import com.example.onlineshop.Helper.ManagmentCart;
import com.example.onlineshop.databinding.ActivityCartBinding;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {
    private ActivityCartBinding binding;
    private ManagmentCart managmentCart;
    private CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managmentCart = new ManagmentCart(this);

        setVariable();
        initCartList();
        observeCartData();
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void initCartList() {
        binding.cartView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        cartAdapter = new CartAdapter(new ArrayList<>(), this);
        binding.cartView.setAdapter(cartAdapter);
    }

    private void observeCartData() {
        managmentCart.getCartList().observe(this, new Observer<ArrayList<ItemsModel>>() {
            @Override
            public void onChanged(ArrayList<ItemsModel> items) {

                if (items == null || items.isEmpty()) {
                    binding.emptyTxt.setVisibility(View.VISIBLE);
                    binding.scrollView3.setVisibility(View.GONE);
                } else {
                    binding.emptyTxt.setVisibility(View.GONE);
                    binding.scrollView3.setVisibility(View.VISIBLE);
                }

                cartAdapter.updateList(items);

                calculateCart(items);
            }
        });
    }

    private void calculateCart(ArrayList<ItemsModel> list) {
        double percentTax = 0.02;
        double delivery = 10;

        double itemTotal = managmentCart.getTotalFee(list);

        double tax = Math.round((itemTotal * percentTax) * 100.0) / 100.0;
        double total = Math.round((itemTotal + tax + delivery) * 100.0) / 100.0;

        binding.totalFeeTxt.setText("$" + itemTotal);
        binding.taxTxt.setText("$" + tax);
        binding.deliveryTxt.setText("$" + delivery);
        binding.totalTxt.setText("$" + total);
    }
}