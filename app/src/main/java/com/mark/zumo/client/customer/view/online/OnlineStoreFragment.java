package com.mark.zumo.client.customer.view.online;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mark.zumo.client.customer.ContextHolder;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.bloc.OnlineStoreBLOC;
import com.mark.zumo.client.customer.entity.OnlineStore;
import com.mark.zumo.client.customer.view.SplashActivity;
import com.mark.zumo.client.customer.view.signin.SignInActivity;

import java.util.Optional;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 20. 3. 21.
 */
public class OnlineStoreFragment extends Fragment {

    private static final String TAG = "OnlineStoreFragment";

    @BindView(R.id.title) AppCompatTextView title;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.subscription) SwitchMaterial subscription;
    @BindView(R.id.header) ConstraintLayout header;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.touch_protector) ConstraintLayout touchProtector;

    private OnlineStoreBLOC onlineStoreBLOC;

    public static OnlineStoreFragment newInstance(final Bundle bundle) {
        OnlineStoreFragment fragment = new OnlineStoreFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onlineStoreBLOC = ViewModelProviders.of(this).get(OnlineStoreBLOC.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_online_store, container, false);
        ButterKnife.bind(this, view);
        inflateView();
        return view;
    }

    private void inflateView() {
        final OnlineStoreAdapter onlineStoreAdapter = new OnlineStoreAdapter(
                this::onStoreSubscriptionChanged,
                this::onOnlineStoreClicked,
                this::onHideStore
        );

        final String selectedStoreUrl = Optional.ofNullable(getArguments())
                .map(bundle -> bundle.getString(SplashActivity.KEY_ONLINE_STORE_URL))
                .orElse("");

        if (!TextUtils.isEmpty(selectedStoreUrl)) {
            onlineStoreAdapter.setSelectedStoreUrl(selectedStoreUrl);
            getArguments().remove(SplashActivity.KEY_ONLINE_STORE_URL);
        }

        recyclerView.setAdapter(onlineStoreAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        header.setOnClickListener(v -> linearLayoutManager.scrollToPositionWithOffset(0, 0));
        swipeRefreshLayout.setOnRefreshListener(this::queryOnlineStoreList);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);

        onlineStoreBLOC.observeOnlineStoreList()
                .doOnNext(onlineStoreAdapter::setOnlineStoreList)
                .subscribe();
        queryOnlineStoreList();

        observeNewOnlineStoreSub();
        subscription.setOnClickListener(v -> onClickSubscription());
    }

    private void queryOnlineStoreList() {
        touchProtector.setVisibility(View.VISIBLE);
        onlineStoreBLOC.queryOnlineStoreList()
                .doOnSuccess(x -> swipeRefreshLayout.setRefreshing(false))
                .doOnError(x -> swipeRefreshLayout.setRefreshing(false))
                .doOnSuccess(x -> touchProtector.setVisibility(View.GONE))
                .doOnError(x -> touchProtector.setVisibility(View.GONE))
                .subscribe();
    }

    private void observeNewOnlineStoreSub() {
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            onlineStoreBLOC.observableSubscriptionNewOnlineStore(currentUser.getUid())
                    .doOnNext(subscription::setChecked)
                    .subscribe();
        }
    }

    private Void onHideStore(final OnlineStore onlineStore, final Runnable onCanceled) {
        onlineStoreBLOC.setHideOnlineStore(onlineStore, true);
        if (getView() != null) {
            Snackbar.make(getView(), onlineStore.store_name + "의 판매 정보가 숨겨졌습니다.", Snackbar.LENGTH_LONG)
                    .setAction("실행취소", v -> {
                        onCanceled.run();
                        onlineStoreBLOC.setHideOnlineStore(onlineStore, false);
                    })
                    .show();
        }

        return null;
    }

    private void onClickSubscription() {

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            SignInActivity.startActivityWithFade(getActivity(), this::onSignInSuccess,
                    this::onSignInFailed);
            return;
        }

        if (subscription.isChecked()) {
            Toast.makeText(ContextHolder.getContext(), "새로운 온라인 판매점 발견시 알려드릴게요.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ContextHolder.getContext(), "새로운 온라인 판매점 알림을 해제합니다.", Toast.LENGTH_SHORT).show();
        }

        subscription.setEnabled(false);
        onlineStoreBLOC.subscribeNewOnlineStore(currentUser.getUid(), subscription.isChecked())
                .doOnSuccess(aBoolean -> subscription.setEnabled(true))
                .doOnError(this::onSubscribeError)
                .subscribe();
    }

    private void onSignInSuccess() {
        observeNewOnlineStoreSub();
        onClickSubscription();
    }

    private void onSignInFailed() {
        subscription.setChecked(!subscription.isChecked());
    }

    private void onSubscribeError(final Throwable throwable) {
        Log.e(TAG, "onSubscribeError: ", throwable);
        Toast.makeText(ContextHolder.getContext(), "알림 설정에 실패했습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        subscription.setEnabled(true);
        subscription.setChecked(!subscription.isChecked());
    }

    private Void onStoreSubscriptionChanged(final OnlineStore onlineStore, final boolean isChecked) {
        if (isChecked) {
            Toast.makeText(ContextHolder.getContext(), "판매 시간 5분전에 알려드릴게요.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ContextHolder.getContext(), "알림을 해제합니다.", Toast.LENGTH_SHORT).show();
        }
        onlineStoreBLOC.onSubscribeOnlineStore(onlineStore, isChecked);
        return null;
    }

    private void onOnlineStoreClicked(final OnlineStore onlineStore) {
        onlineStoreBLOC.onClick(onlineStore);
    }
}
