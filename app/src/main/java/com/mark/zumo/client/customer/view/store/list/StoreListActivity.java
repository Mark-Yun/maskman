package com.mark.zumo.client.customer.view.store.list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.bloc.MainViewBLOC;
import com.mark.zumo.client.customer.entity.Store;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 20. 3. 11.
 */
public class StoreListActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 1231;
    public static final String KEY_CODE = "code";

    private static final List<Store> storeList = new CopyOnWriteArrayList<>();

    @BindView(R.id.name) AppCompatTextView name;
    @BindView(R.id.stock) MaterialButton stock;
    @BindView(R.id.dist) MaterialButton dist;
    @BindView(R.id.stock_at) MaterialButton stockAt;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private MainViewBLOC mainViewBLOC;
    private StoreListAdapter storeListAdapter;

    private boolean stockSort, stockAtSort = true, distSort = true;
    private String stockText, stockAtText, distText;

    public static void startActivity(final Activity activity, List<Store> storeList) {
        StoreListActivity.storeList.clear();
        StoreListActivity.storeList.addAll(storeList);
        //TODO: Refactor
        activity.startActivityForResult(new Intent(activity, StoreListActivity.class), REQUEST_CODE);
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);
        ButterKnife.bind(this);

        mainViewBLOC = ViewModelProviders.of(this).get(MainViewBLOC.class);
        inflateView();
    }

    private void inflateView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(
                this, R.anim.layout_animation_fall_down);
        recyclerView.setLayoutAnimation(animation);

        storeListAdapter = new StoreListAdapter(mainViewBLOC.getCurrentLocation(), storeList,
                this::onStoreSelect);

        recyclerView.setAdapter(storeListAdapter);

        distText = dist.getText().toString();
        stockText = stock.getText().toString();
        stockAtText = stockAt.getText().toString();

        dist.setText(distText + "-");
        stock.setText(stockText + "-");
        stockAt.setText(stockAtText + "-");
    }

    private void onStoreSelect(final String code) {
        Intent data = new Intent();
        setResult(RESULT_OK, data.putExtra(KEY_CODE, code));
        finish();
    }

    @OnClick(R.id.stock)
    public void onStockClicked() {
        storeListAdapter.sortWithStock(stockSort);
        stockSort = !stockSort;
        stock.setText(stockText + getOrderString(stockSort));
    }

    @OnClick(R.id.dist)
    public void onDistClicked() {
        storeListAdapter.sortWithDistance(distSort);
        distSort = !distSort;
        dist.setText(distText + getOrderString(distSort));
    }

    @OnClick(R.id.stock_at)
    public void onStockAtClicked() {
        storeListAdapter.sortWithStockAt(stockAtSort);
        stockAtSort = !stockAtSort;
        stockAt.setText(stockAtText + getOrderString(stockAtSort));
    }

    private String getOrderString(final boolean asc) {
        if (asc) {
            return "↑";
        } else {
            return "↓";
        }
    }
}
