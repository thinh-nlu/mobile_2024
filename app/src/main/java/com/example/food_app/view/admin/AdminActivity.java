package com.example.food_app.view.admin;

import android.view.LayoutInflater;

import com.example.food_app.base.BaseActivity;
import com.example.food_app.databinding.ActivityAdminBinding;

public class AdminActivity extends BaseActivity<ActivityAdminBinding> {
    @Override
    protected ActivityAdminBinding setViewBinding() {
        return ActivityAdminBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void viewListener() {

    }
}
