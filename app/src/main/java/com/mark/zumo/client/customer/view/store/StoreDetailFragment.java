package com.mark.zumo.client.customer.view.store;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.bloc.MainViewBLOC;
import com.mark.zumo.client.customer.entity.Store;
import com.mark.zumo.client.customer.util.DateUtils;
import com.mark.zumo.client.customer.util.StoreUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.function.Consumer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mark on 20. 3. 11.
 */
public class StoreDetailFragment extends Fragment {

    private static final String TAG = StoreDetailFragment.class.getSimpleName();

    private static final String CODE_KEY = "code";

    @BindView(R.id.subscription) SwitchMaterial subscription;
    @BindView(R.id.name) AppCompatTextView name;
    @BindView(R.id.type) AppCompatTextView type;
    @BindView(R.id.addr) AppCompatTextView addr;
    @BindView(R.id.stock) AppCompatTextView stock;
    @BindView(R.id.stock_at) Chronometer stockAt;
    @BindView(R.id.created_at) Chronometer createdAt;
    @BindView(R.id.close) AppCompatImageView close;

    private MainViewBLOC mainViewBLOC;
    private Consumer<Fragment> onCloseClicked;

    public static StoreDetailFragment newInstance(final String code) {
        Bundle args = new Bundle();

        args.putString(CODE_KEY, code);
        StoreDetailFragment fragment = new StoreDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public StoreDetailFragment onCloseClicked(final Consumer<Fragment> consumer) {
        onCloseClicked = consumer;
        return this;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewBLOC = ViewModelProviders.of(this).get(MainViewBLOC.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_detail, container, false);
        ButterKnife.bind(this, view);
        inflateView();
        return view;
    }

    private void inflateView() {
        if (getArguments() == null) {
            return;
        }

        String code = getArguments().getString(CODE_KEY);

        mainViewBLOC.observableStore(code)
                .doOnNext(this::onLoadStore)
                .subscribe();
    }

    private void onLoadStore(final Store store) {
        name.setText(store.name);
        type.setText(StoreUtils.getTypeLabel(getContext(), store.type));
        addr.setText(store.addr);
        stock.setText(StoreUtils.getStockLabel(getContext(), store.remain_stat));
        final Date stockAtDate = DateUtils.createDate(store.stock_at);
        if (stockAtDate != null) {
            updateChronometer(stockAt, stockAtDate);
        }

        final Date createAtDate = DateUtils.createDate(store.create_at);
        if (createAtDate != null) {
            updateChronometer(createdAt, createAtDate);
        }

    }

    private void updateChronometer(final Chronometer chronometer, final Date createAtDate) {
        long time = createAtDate.getTime();
        long currentTime = Calendar.getInstance().getTimeInMillis();
        chronometer.setFormat("%s 전");
        chronometer.setBase(SystemClock.elapsedRealtime() - (currentTime - time));
        chronometer.start();
    }

    @OnClick(R.id.close)
    void onCloseClicked() {
        if (onCloseClicked == null) {
            return;
        }

        onCloseClicked.accept(this);
    }
}
