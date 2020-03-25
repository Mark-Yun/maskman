package com.mark.zumo.client.customer.view.online;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.mark.zumo.client.customer.ContextHolder;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.entity.OnlineStore;
import com.mark.zumo.client.customer.util.GlideUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import butterknife.BindView;
import butterknife.ButterKnife;
import kotlin.jvm.functions.Function2;

/**
 * Created by mark on 20. 3. 21.
 */
class OnlineStoreAdapter extends RecyclerView.Adapter<OnlineStoreAdapter.ViewHolder> {

    private static final String TAG = "OnlineStoreAdapter";

    private final Function2<OnlineStore, Boolean, Void> onSubscribe;
    private final Consumer<OnlineStore> onClickStore;
    private final Function2<OnlineStore, Runnable, Void> onHideStore;

    private final List<OnlineStore> onlineStoreList;

    private String selectedStoreUrl;
    private RecyclerView recyclerView;

    OnlineStoreAdapter(final Function2<OnlineStore, Boolean, Void> onSubscribe,
                       final Consumer<OnlineStore> onClickStore,
                       final Function2<OnlineStore, Runnable, Void> onClickHideStore) {
        this.onSubscribe = onSubscribe;
        this.onClickStore = onClickStore;
        this.onHideStore = onClickHideStore;

        onlineStoreList = new CopyOnWriteArrayList<>();
    }

    @StringRes
    private static int getSalesStatusLabel(final int status) {
        switch (status) {
            case OnlineStore.SOLD_OUT:
                return R.string.sold_out;
            case OnlineStore.PREPARING:
                return R.string.preparing_sale;
            case OnlineStore.ON_SALE:
                return R.string.on_sale;
        }

        return R.string.sold_out;
    }

    @ColorRes
    private static int getSalesStatusColor(final int status) {
        switch (status) {
            case OnlineStore.SOLD_OUT:
                return android.R.color.darker_gray;
            case OnlineStore.PREPARING:
                return android.R.color.holo_green_light;
            case OnlineStore.ON_SALE:
                return android.R.color.holo_blue_light;
        }

        return android.R.color.darker_gray;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    void setSelectedStoreUrl(final String storeUrl) {
        selectedStoreUrl = storeUrl;
    }

    void setOnlineStoreList(final List<OnlineStore> onlineStoreList) {
        this.onlineStoreList.clear();
        for (final OnlineStore onlineStore : onlineStoreList) {
            if (onlineStore.hide) {
                continue;
            }

            this.onlineStoreList.add(onlineStore);
        }

        Log.d(TAG, "setOnlineStoreList: onlineStoreList=" + this.onlineStoreList.size());
        notifyDataSetChanged();

        if (TextUtils.isEmpty(selectedStoreUrl)) {
            return;
        }

        int selectedStorePosition = -1;
        for (int i = 0; i < this.onlineStoreList.size(); i++) {
            final OnlineStore onlineStore = this.onlineStoreList.get(i);
            if (TextUtils.equals(selectedStoreUrl, onlineStore.store_url)) {
                selectedStorePosition = i;
                break;
            }
        }

        final int positionMoveTo = selectedStorePosition;
        Log.d(TAG, "setOnlineStoreList: selectedStorePosition=" + selectedStorePosition);
        if (positionMoveTo > -1) {
            Optional.ofNullable(recyclerView)
                    .map(RecyclerView::getLayoutManager)
                    .filter(LinearLayoutManager.class::isInstance)
                    .map(LinearLayoutManager.class::cast)
                    .ifPresent(layoutManager -> layoutManager.scrollToPositionWithOffset(positionMoveTo, 0));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        LayoutInflater layoutInflater = parent.getContext().getSystemService(LayoutInflater.class);
        View view = layoutInflater.inflate(R.layout.card_view_online_store, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final OnlineStore onlineStore = onlineStoreList.get(position);

        String title = onlineStore.title;
        if (!TextUtils.isEmpty(title)) {
            title = title.replaceAll("\\[.*\\] ", "");
        }
        holder.title.setText(title);
        holder.storeName.setText(onlineStore.store_name);
        holder.price.setText(onlineStore.price);
        holder.startTime.setText(onlineStore.start_time);
        holder.startTime.setVisibility(onlineStore.status == OnlineStore.PREPARING ? View.VISIBLE : View.GONE);
        holder.content.setOnClickListener(v -> onClickStore.accept(onlineStore));
        holder.subscription.setVisibility(onlineStore.status == OnlineStore.PREPARING ? View.VISIBLE : View.GONE);
        holder.subscription.setChecked(onlineStore.subscribe);
        holder.subscription.setOnClickListener(v -> onSubscribe.invoke(onlineStore, holder.subscription.isChecked()));
        holder.hide.setOnClickListener(v -> onHideStore(onlineStore));
        holder.status.setText(getSalesStatusLabel(onlineStore.status));
        holder.status.setTextColor(ContextHolder.getContext().getColor(getSalesStatusColor(onlineStore.status)));

        RequestOptions requestOptions = new RequestOptions()
                .transform(new CenterCrop(), new RoundedCorners(GlideUtils.dpToPx(16)));

        Glide.with(holder.image)
                .load(onlineStore.img_url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.image);

        if (TextUtils.equals(this.selectedStoreUrl, onlineStore.store_url)) {
            Animation animation = AnimationUtils.loadAnimation(ContextHolder.getContext(), R.anim.blink_animation);
            holder.itemView.setAnimation(animation);
            animation.start();
        }
    }

    private void onHideStore(final OnlineStore onlineStore) {
        int position = onlineStoreList.indexOf(onlineStore);
        notifyItemRemoved(position);
        onlineStoreList.remove(onlineStore);
        onHideStore.invoke(onlineStore, () -> {
            onlineStoreList.add(position, onlineStore);
            notifyItemInserted(position);
        });
    }

    @Override
    public int getItemCount() {
        return onlineStoreList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.content) ConstraintLayout content;
        @BindView(R.id.image) AppCompatImageView image;
        @BindView(R.id.title) AppCompatTextView title;
        @BindView(R.id.price) AppCompatTextView price;
        @BindView(R.id.store_name) AppCompatTextView storeName;
        @BindView(R.id.start_time) AppCompatTextView startTime;
        @BindView(R.id.subscription) SwitchMaterial subscription;
        @BindView(R.id.hide) MaterialButton hide;
        @BindView(R.id.status) AppCompatTextView status;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
