package com.mark.zumo.client.customer.view.store.detail;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mark.zumo.client.customer.ContextHolder;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.bloc.MainViewBLOC;
import com.mark.zumo.client.customer.bloc.SubscribeBLOC;
import com.mark.zumo.client.customer.entity.Store;
import com.mark.zumo.client.customer.util.DateUtils;
import com.mark.zumo.client.customer.util.StoreUtils;

import java.util.Optional;
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
    @BindView(R.id.stock_at) AppCompatTextView stockAt;
    @BindView(R.id.created_at) AppCompatTextView createdAt;
    @BindView(R.id.close) AppCompatImageButton close;

    private MainViewBLOC mainViewBLOC;
    private SubscribeBLOC subscribeBLOC;

    private Consumer<Fragment> onCloseClicked;
    private Store store;
    private AlertDialog alertDialog;

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
        subscribeBLOC = ViewModelProviders.of(this).get(SubscribeBLOC.class);
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
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mainViewBLOC.observableStore(code)
                .doOnNext(this::onLoadStore)
                .subscribe();

        if (firebaseUser != null) {
            subscription.setEnabled(false);
            subscribeBLOC.observableSub(firebaseUser.getUid(), code)
                    .firstElement()
                    .doOnSuccess(this::onFirstLoadSubscription)
                    .subscribe();
        }

        subscription.setOnClickListener(this::onSubscriptionClicked);
    }

    private void onLoadSubscription(final boolean isChecked) {
        subscription.setEnabled(true);
        Log.d(TAG, "onLoadSubscription: isChecked=" + isChecked);
    }

    private void onFirstLoadSubscription(final boolean isChecked) {
        Log.d(TAG, "onFirstLoadSubscription: isChecked=" + isChecked);

        subscription.setEnabled(true);
        subscription.setChecked(isChecked);

        observeSubscriptionInfo();
    }

    private boolean onSubscriptionClicked(final View view) {
        if (getArguments() == null) {
            return true;
        }

        Log.d(TAG, "onSubscriptionClicked: ");
        subscription.setEnabled(false);

        String code = getArguments().getString(CODE_KEY);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            return true;
        }

        if (subscription.isChecked()) {
            Toast.makeText(ContextHolder.getContext(), "새로 입고되면 알림으로 알려드릴게요.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ContextHolder.getContext(), "입고 알림을 해제합니다.", Toast.LENGTH_SHORT).show();
        }

        subscribeBLOC.subscribe(firebaseUser.getUid(), code, subscription.isChecked())
                .doOnError(this::onSubscribeError)
                .subscribe();

        return true;
    }

    private void onSubscribeError(final Throwable t) {
        Toast.makeText(ContextHolder.getContext(), "오류가 발생했습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        subscription.setChecked(!subscription.isChecked());
        subscription.setEnabled(true);

        observeSubscriptionInfo();
    }

    private void observeSubscriptionInfo() {
        if (getArguments() == null) {
            return;
        }

        String code = getArguments().getString(CODE_KEY);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            return;
        }

        subscribeBLOC.observableSub(firebaseUser.getUid(), code)
                .doOnNext(this::onLoadSubscription)
                .doOnError(this::onSubscribeError)
                .subscribe();
    }

    private void onLoadStore(final Store store) {
        this.store = store;

        name.setText(store.name);
        type.setText(StoreUtils.getTypeLabel(getContext(), store.type));
        addr.setText(store.addr);
        stock.setText(StoreUtils.getStockLabel(getContext(), store.remain_stat));
        stockAt.setText(DateUtils.convertTimeStamp(store.stock_at));
        createdAt.setText(DateUtils.convertTimeStamp(store.create_at));
    }

    @OnClick(R.id.close)
    void onCloseClicked() {
        if (onCloseClicked == null) {
            return;
        }

        onCloseClicked.accept(this);
    }

    @OnClick(R.id.copy)
    void onCopyClicked() {
        ClipboardManager clipboardManager = getContext().getSystemService(ClipboardManager.class);
        clipboardManager.setPrimaryClip(ClipData.newPlainText("약국 판매처 주소", addr.getText().toString()));
        Toast.makeText(ContextHolder.getContext(), "클립보드에 주소를 복사했습니다.", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.navi)
    public void onNaviClicked() {
        onCloseClicked();

        if (getContext() == null || store == null) {
            return;
        }

        LayoutInflater layoutInflater = getContext().getSystemService(LayoutInflater.class);
        View rootView = layoutInflater.inflate(R.layout.dialog_select_navigation, null, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(layoutManager);

        NavigationAdapter navigationAdapter = new NavigationAdapter(
                store.lat, store.lng, store.name,
                () -> Optional.ofNullable(alertDialog).ifPresent(Dialog::cancel)
        );

        recyclerView.setAdapter(navigationAdapter);

        alertDialog = new MaterialAlertDialogBuilder(getActivity())
                .setTitle(store.name + "까지 길찾기")
                .setView(rootView)
                .setCancelable(true)
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel())
                .create();

        alertDialog.show();
    }
}
