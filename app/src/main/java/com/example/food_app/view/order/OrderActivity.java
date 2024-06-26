package com.example.food_app.view.order;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.food_app.R;
import com.example.food_app.base.BaseActivity;
import com.example.food_app.databinding.ActivityOrderBinding;
import com.example.food_app.helper.CallBack;
import com.example.food_app.model.Cart;
import com.example.food_app.model.Order;
import com.example.food_app.model.User;
import com.example.food_app.utils.Constant;
import com.example.food_app.utils.SharePreferenceUtils;
import com.example.food_app.view.home.HomeActivity;
import com.example.food_app.view.home.adapter.OrderAdapter;
import com.example.food_app.view.profile.ChangeInfoActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class OrderActivity extends BaseActivity<ActivityOrderBinding> {
    private User currentUser = null;
    private Order order;
    private String actionOrder;
    private OrderAdapter orderAdapter;
    ProgressDialog progressDialog;

    @Override
    protected ActivityOrderBinding setViewBinding() {
        return ActivityOrderBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void initView() {
        binding.imvTickCard.setActivated(true);
        initLoadingData();
        getUserFromFirebase(() -> {
            setUpProfile();
            progressDialog.cancel();
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            order = (Order) bundle.getSerializable("order");
            actionOrder = bundle.getString("actionOrder");
        }
        initViewOrder();
        initAdapter();
        bindEvent();
        binding.tvSumValue.setText(formatCost((int) countSumPrice(order)));
    }

    @Override
    protected void viewListener() {
    }

    private void bindEvent() {
        binding.btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.btnStartOrder.setOnClickListener(v -> {
            if (order.getAddress().isEmpty()) {
                Toast.makeText(OrderActivity.this, "Vui lòng nhập địa chỉ cho đơn hàng này", Toast.LENGTH_SHORT).show();
            } else {
                order.setStatus(Constant.PENDING);
                updateOrder(order);
                startActivity(new Intent(OrderActivity.this, OrderSuccessActivity.class));
            }
        });

        binding.cvProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChangeInfoActivity.class);
            intent.putExtra("INVOICE_NUMBER", order.getInvoiceNumber());
            intent.putExtra("FROM", "ORDER");
            startActivity(intent);
            finish();
        });

        binding.cancelOrder.setOnClickListener(v -> {
            order.setStatus(Constant.CANCELLED);
            updateOrder(order);
            Toast.makeText(this, "Đã hủy", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
    }

    private void initAdapter() {
        orderAdapter = new OrderAdapter(this, order);
        binding.rvFood.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.rvFood.setAdapter(orderAdapter);
    }

    private void initViewOrder() {
        switch (actionOrder) {
            case Constant.AWAITING_PAYMENT:
                binding.btnStartOrder.setVisibility(View.VISIBLE);
                binding.llButton.setVisibility(View.GONE);
                binding.cancelOrder.setVisibility(View.GONE);
                binding.cvProfile.setEnabled(true);
                break;
            case Constant.PENDING:
                binding.btnStartOrder.setVisibility(View.GONE);
                binding.llButton.setVisibility(View.GONE);
                binding.cancelOrder.setVisibility(View.VISIBLE);
                binding.cvProfile.setEnabled(false);
                break;
            case Constant.DELIVERED:
                binding.btnStartOrder.setVisibility(View.GONE);
                binding.llButton.setVisibility(View.VISIBLE);
                binding.cancelOrder.setVisibility(View.GONE);
                binding.cvProfile.setEnabled(false);
                break;
            case Constant.CANCELLED:
                binding.btnStartOrder.setVisibility(View.GONE);
                binding.llButton.setVisibility(View.GONE);
                binding.cancelOrder.setVisibility(View.GONE);
                binding.cvProfile.setEnabled(false);
                break;

        }
    }

    private void getUserFromFirebase(CallBack.OnDataLoad listener) {
        rf.child("Users").child(user != null ? user.getUid() : "").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentUser = snapshot.getValue(User.class);
                }
                listener.onDataLoad();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateOrder(Order order) {
        Log.d("getInvoiceNumber", order.getInvoiceNumber());
        if (order.getAddress().equals("")) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ của bạn", Toast.LENGTH_SHORT).show();
        } else {
            rf.child("Order").child(user.getUid()).child(order.getInvoiceNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        rf.child("Order").child(user.getUid()).child(order.getInvoiceNumber()).setValue(order);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void setUpProfile() {
        binding.tvName.setText(order.getNameUser());
        binding.tvEmail.setText(order.getEmail());
        binding.tvContact.setText(order.getContact());
        binding.tvAddress.setText(!order.getAddress().equals("") ? order.getAddress() : "Hãy cập nhật đỉa chỉ của bạn");
        Glide.with(this).load(SharePreferenceUtils.getString(Constant.AVATAR, "")).into(binding.imvProfile);
    }

    private double countSumPrice(Order order) {
        double sum = 0.0;
        for (Cart c : order.getFoodList()) {
            sum += Double.valueOf(c.getNumber()) * c.getFood().getPrice();
        }
        return sum;
    }

    private void initLoadingData() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Dang tai data");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private String formatCost(int cost) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String formattedCost = decimalFormat.format(cost)+"đ";
        return formattedCost;
    }
}
