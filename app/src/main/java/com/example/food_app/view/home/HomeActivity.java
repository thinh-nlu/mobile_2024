package com.example.food_app.view.home;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food_app.R;
import com.example.food_app.base.BaseActivity;
import com.example.food_app.databinding.ActivityHomeBinding;
import com.example.food_app.helper.CallBack;
import com.example.food_app.model.Category;
import com.example.food_app.model.DrawerItemModel;
import com.example.food_app.model.Food;
import com.example.food_app.model.News;
import com.example.food_app.repository.Repository;
import com.example.food_app.service.OrderService;
import com.example.food_app.utils.Constant;
import com.example.food_app.utils.SharePreferenceUtils;
import com.example.food_app.view.admin.AdminActivity;
import com.example.food_app.view.cart.CartActivity;
import com.example.food_app.view.favourite.FavouriteActivity;
import com.example.food_app.view.food_detail.FoodDetailActivity;
import com.example.food_app.view.history.HistoryActivity;
import com.example.food_app.view.home.adapter.CategoryAdapter;
import com.example.food_app.view.home.adapter.DrawerItemCustomAdapter;
import com.example.food_app.view.home.adapter.FoodAdapter;
import com.example.food_app.view.home.adapter.NewAdapter;
import com.example.food_app.view.home.seemore.SeeMoreActivity;
import com.example.food_app.view.profile.ProfileActivity;
import com.example.food_app.view.search.SearchActivity;
import com.example.food_app.view.splash.SplashActivity;
import com.example.food_app.view.user.UserActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity<ActivityHomeBinding> implements CallBack.ItemClickListener{
    private CategoryAdapter categoryAdapter;
    private FoodAdapter foodAdapter;
    private NewAdapter newAdapter;
    private List<Food> foodList = new ArrayList<>();
    private List<Food> filterList = new ArrayList<>();
    private List<News> newsList = new ArrayList<>();
    private String cate;
    private ProgressDialog loadingDataDialog;
    private DrawerItemModel[] drawerItemGeneral;
    private int NOTIFICATIONS_PERMISSION_REQUEST_CODE = 112;

    @Override
    protected ActivityHomeBinding setViewBinding() {
        return ActivityHomeBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void initView() {
        initDrawer();

        //start service
        Intent serviceIntent = new Intent(this, OrderService.class);
        startService(serviceIntent);
        checkPermissionCamera();
        requestPermission();
        initLoadingData();
        getListFood(new CallBack.OnDataLoad() {
            @Override
            public void onDataLoad() {
                loadingDataDialog.cancel();
                initRcvCategory();
                initFoodAdapter();
                initRcvNew();
            }
        });
//        if (SharePreferenceUtils.getBoolean(Constant.FIRST_INSTALL, false)) {
//            if(SharePreferenceUtils.getString(Constant.CHEATING,"false").equals("false")) {
//                if (foodList.size() == 0) {
//                    foodList.addAll(Repository.listFood());
//                    rf.child("Foods").setValue(foodList);
//                }
//            }
//            SharePreferenceUtils.putBoolean(Constant.FIRST_INSTALL, false);
//        }
        Log.d("cqq", foodList.size() + "");
    }


    @Override
    protected void viewListener() {
        binding.btnMenu.setOnClickListener(v -> {
            openDrawer();
        });

        binding.drawer.imgClose.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(binding.clDrawer);
        });

        binding.drawer.btnSignOut.setOnClickListener(v -> {
            signOut();
        });

        binding.tvSeeMore.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, SeeMoreActivity.class));
        });

        binding.btnCart.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, CartActivity.class));
        });

        binding.clSearch.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, SearchActivity.class)));
    }

    private void initDrawer() {
        DrawerItemModel[] drawerItemSetting = new DrawerItemModel[0];

        DrawerItemCustomAdapter settingDrawerAdapter = new DrawerItemCustomAdapter(this, R.layout.layout_drawer_setting_item, drawerItemSetting, this::selectItemSetting);
        binding.drawer.lvSetting.setAdapter(settingDrawerAdapter);

        if (SharePreferenceUtils.getBoolean(Constant.ADMIN, false)) {
            drawerItemGeneral = new DrawerItemModel[4];
            drawerItemGeneral[0] = new DrawerItemModel(R.drawable.ic_profile, "Trang cá nhân");
            drawerItemGeneral[1] = new DrawerItemModel(R.drawable.ic_history, "Lịch sử đơn hàng");
            drawerItemGeneral[2] = new DrawerItemModel(R.drawable.ic_farvourite, "Danh sách yêu thích");
            drawerItemGeneral[3] = new DrawerItemModel(R.drawable.ic_profile, "Trang quản lí");
        } else {
            drawerItemGeneral = new DrawerItemModel[3];
            drawerItemGeneral[0] = new DrawerItemModel(R.drawable.ic_profile, "Trang cá nhân");
            drawerItemGeneral[1] = new DrawerItemModel(R.drawable.ic_history, "Lịch sử đơn hàng");
            drawerItemGeneral[2] = new DrawerItemModel(R.drawable.ic_farvourite, "Danh sách yêu thích");
        }

        DrawerItemCustomAdapter generalDrawerAdapter = new DrawerItemCustomAdapter(this, R.layout.layout_drawer_general_item, drawerItemGeneral, this);
        binding.drawer.lvGeneral.setAdapter(generalDrawerAdapter);
    }

    private void openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START);
    }

    private void getListFood(CallBack.OnDataLoad listener) {
        foodList.clear();
        rf.child("Foods").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d : snapshot.getChildren()) {
                    Food food = d.getValue(Food.class);
                    foodList.add(food);
                }
                listener.onDataLoad();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void initRcvCategory() {
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category("Pizza", false));
        categoryList.add(new Category("drinks", false));
        categoryList.add(new Category("snacks", false));
        categoryList.add(new Category("sauce", false));
        categoryList.add(new Category("rice", false));
        categoryList.add(new Category("chicken", false));
        categoryList.add(new Category("potato", false));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        categoryAdapter = new CategoryAdapter(categoryList, this);
        categoryAdapter.callBackCategory(new CallBack.OnCategoryCallBack() {
            @Override
            public void onClick(String category) {
                cate = category;
                filterFoodByCategory(cate);
            }
        });
        filterFoodByCategory(categoryAdapter.setDefaultCheck());
        binding.rcvCategory.setLayoutManager(linearLayoutManager);
        binding.rcvCategory.setAdapter(categoryAdapter);
    }
    private void initRcvNew() {
        newsList.add(new News("15 đồ ăn nhanh ngon miệng và tốt cho sức khỏe",
                R.drawable.background_image1,
                4.2,
                "30,200 lượt xem",
                "https://soha.vn/15-do-an-nhanh-ngon-mieng-va-tot-cho-suc-khoe-2019031111174121.htm"));

        newsList.add(new News("12 món ăn nhẹ lành mạnh và tốt cho sức khỏe",
                R.drawable.background_image2,
                4.5,
                "45,200 lượt xem",
                "https://laodong.vn/suc-khoe/12-mon-an-nhe-lanh-manh-va-tot-cho-suc-khoe-937033.ldo"));

        newsList.add(new News("Tổng hợp những món ăn bồi bổ sức khỏe cho người bệnh ",
                R.drawable.background_image3,
                4.6,
                "50,200 lượt xem",
                "https://medlatec.vn/tin-tuc/tong-hop-nhung-mon-an-boi-bo-suc-khoe-cho-nguoi-benh-s51-n29730"));

        newsList.add(new News("15 món ăn bổ dưỡng phục hồi sức khỏe cho người bệnh",
                R.drawable.background_image4,
                4.6,
                "55,200 lượt xem",
                "https://www.dienmayxanh.com/vao-bep/tong-hop-15-mon-an-bo-duong-phuc-hoi-suc-khoe-cho-nguoi-benh-10659"));

        newsList.add(new News("12 món ăn vặt lành mạnh cho dân văn phòng",
                R.drawable.background_image5,
                4.7,
                "48,200 lượt xem",
                "https://hellobacsi.com/an-uong-lanh-manh/bi-quyet-an-uong-lanh-manh/12-mon-an-vat-ngon-khoe-danh-cho-dan-van-phong/"));


        newAdapter = new NewAdapter(this, newsList);
        binding.rcvNew.setAdapter(newAdapter);
        binding.rcvNew.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

    }

    private void initFoodAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        foodAdapter = new FoodAdapter(this, filterList, idFood -> {
            Intent intent = new Intent(HomeActivity.this, FoodDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("idFood", idFood);
            intent.putExtras(bundle);
            startActivity(intent);
        });
        binding.rcvFood.setLayoutManager(linearLayoutManager);
        binding.rcvFood.setAdapter(foodAdapter);

    }

    private void filterFoodByCategory(String cate) {
        filterList.clear();
        for (Food f : foodList) {
            if (f.getCategory().equals(cate)) {
                filterList.add(f);
            }
        }
        initFoodAdapter();
    }

    private void initLoadingData() {
        loadingDataDialog = new ProgressDialog(this);
        loadingDataDialog.setMessage("Dang tai data");
        loadingDataDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDataDialog.setCancelable(false);
        loadingDataDialog.show();
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT > 32) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[] { Manifest.permission.POST_NOTIFICATIONS },
                        NOTIFICATIONS_PERMISSION_REQUEST_CODE);
            }
        }
    }
    private void checkPermissionCamera() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO}, 1222);
            } else {
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1222);
            } else {
            }
        }

    }
    private void signOut() {
        mAuth.signOut();
        // Chuyển hướng người dùng đến màn hình đăng nhập hoặc màn hình chính
        Intent intent = new Intent(HomeActivity.this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        SharePreferenceUtils.putString(Constant.USERNAME,"");
        SharePreferenceUtils.putString(Constant.PASSWORD,"");
        startActivity(intent);
        finish(); // Đảm bảo người dùng không thể quay lại màn hình này bằng nút back
    }

    private void selectItemSetting(int position) {
        Intent intent = new Intent();
        if (position == 0) {
            intent = new Intent(this, ProfileActivity.class);
        }

        startActivity(intent);
        binding.drawerLayout.closeDrawer(binding.clDrawer);
    }

    private void selectItemGeneral(int position) {
        Intent intent;
        switch (position) {
            case 0:
                intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                break;
            case 1:
                intent = new Intent(this, HistoryActivity.class);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(this, FavouriteActivity.class);
                startActivity(intent);
                break;
            case 3:
                intent = new Intent(this, AdminActivity.class);
                startActivity(intent);
                break;
        }

        binding.drawerLayout.closeDrawer(binding.clDrawer);
    }

    @Override
    public void onClick(int position) {
        selectItemGeneral(position);
    }
}
