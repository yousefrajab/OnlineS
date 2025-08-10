package com.example.onlineshop.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.onlineshop.Adapter.CartAdapter;
import com.example.onlineshop.Domain.ItemsModel;
import com.example.onlineshop.Helper.ManagmentCart;
import com.example.onlineshop.R;
import com.example.onlineshop.databinding.FragmentCartBinding;

import java.util.ArrayList;

public class CartFragment extends Fragment {
    private FragmentCartBinding binding;
    private ManagmentCart managmentCart;
    private CartAdapter cartAdapter;
    private ArrayList<ItemsModel> currentCartItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        managmentCart = new ManagmentCart(getContext());
        initCartList();
        observeCartData();
        setupCheckoutButton();
    }

    private void setupCheckoutButton() {
        binding.checkoutBtn.setOnClickListener(v -> {
            if (currentCartItems == null || currentCartItems.isEmpty()) {
                Toast.makeText(getContext(), "Your cart is empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            double total = 0;
            try {
                total = Double.parseDouble(binding.totalTxt.getText().toString().replace("$", ""));
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error calculating total.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (getParentFragmentManager() != null) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, CheckoutFragment.newInstance(currentCartItems, total))
                        .addToBackStack(null)
                        .commit();
            }
        });
    }


    private void initCartList() {
        binding.cartView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        cartAdapter = new CartAdapter(new ArrayList<>(), getContext());
        binding.cartView.setAdapter(cartAdapter);
    }

    private void observeCartData() {
        managmentCart.getCartList().observe(getViewLifecycleOwner(), items -> {
            if (items == null || items.isEmpty()) {
                binding.emptyTxt.setVisibility(View.VISIBLE);
                binding.scrollViewCart.setVisibility(View.GONE);
            } else {
                binding.emptyTxt.setVisibility(View.GONE);
                binding.scrollViewCart.setVisibility(View.VISIBLE);
            }

            if (items != null) {
                currentCartItems = items;
                cartAdapter.updateList(items);
            }

            calculateCart(items);
        });
    }

    private void calculateCart(ArrayList<ItemsModel> list) {
        if (list == null) return;

        double percentTax = 0.02;
        double delivery = 10;
        double itemTotal = managmentCart.getTotalFee(list);
        double tax = Math.round((itemTotal * percentTax) * 100.0) / 100.0;
        double total = Math.round((itemTotal + tax + delivery) * 100.0) / 100.0;

        binding.totalFeeTxt.setText("$" + String.format("%.2f", itemTotal));
        binding.taxTxt.setText("$" + String.format("%.2f", tax));
        binding.deliveryTxt.setText("$" + String.format("%.2f", delivery));
        binding.totalTxt.setText("$" + String.format("%.2f", total));
    }
}