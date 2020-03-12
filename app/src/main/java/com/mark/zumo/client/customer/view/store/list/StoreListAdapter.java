package com.mark.zumo.client.customer.view.store.list;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.entity.Store;
import com.mark.zumo.client.customer.util.DateUtils;
import com.mark.zumo.client.customer.util.MapUtils;
import com.mark.zumo.client.customer.util.StoreUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mark on 20. 3. 11.
 */
class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.ViewHolder> {

    private final List<Store> storeList;
    private final Location currentLocation;
    private final Consumer<String> onStoreSelect;
    private RecyclerView recyclerView;

    StoreListAdapter(final Location currentLocation, final List<Store> storeList,
                     final Consumer<String> onStoreSelect) {
        this.storeList = new ArrayList<>(storeList);
        this.currentLocation = currentLocation;
        this.onStoreSelect = onStoreSelect;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        LayoutInflater layoutInflater = parent.getContext().getSystemService(LayoutInflater.class);
        View view = layoutInflater.inflate(R.layout.card_view_store_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Context context = holder.itemView.getContext();
        final Store store = storeList.get(position);

        float distance = currentLocation.distanceTo(MapUtils.locationFrom(store.lat, store.lng));

        holder.icon.setImageResource(StoreUtils.getTypeDrawable(store.type));
        holder.name.setText(store.name);
        holder.stock.setText(StoreUtils.getStatusLabel(store.remain_stat));
        holder.stock.setBackgroundDrawable(context.getDrawable(StoreUtils.getStatusBackground(store.remain_stat)));
        holder.dist.setText(MapUtils.convertDistance(context, distance));
        holder.stockAt.setText(DateUtils.convertTimeStamp(store.stock_at));
        holder.itemView.setOnClickListener(v -> onStoreSelect.accept(store.code));
    }

    void sortWithStock(final boolean desc) {
        Maybe.fromAction(() -> {
            if (desc) {
                storeList.sort((o1, o2) -> StoreUtils.convertStock(o1.remain_stat) - StoreUtils.convertStock(o2.remain_stat));
            } else {
                storeList.sort((o2, o1) -> StoreUtils.convertStock(o1.remain_stat) - StoreUtils.convertStock(o2.remain_stat));
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(this::notifyDataSetChangedWithAnimation)
                .subscribe();
    }

    void sortWithDistance(final boolean desc) {
        Maybe.fromAction(() -> {
            if (desc) {
                storeList.sort((o1, o2) -> (int) (currentLocation.distanceTo(MapUtils.locationFrom(o1.lat, o1.lng))
                        - currentLocation.distanceTo(MapUtils.locationFrom(o2.lat, o2.lng))));
            } else {
                storeList.sort((o2, o1) -> (int) (currentLocation.distanceTo(MapUtils.locationFrom(o1.lat, o1.lng))
                        - currentLocation.distanceTo(MapUtils.locationFrom(o2.lat, o2.lng))));
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(this::notifyDataSetChangedWithAnimation)
                .subscribe();
    }

    void sortWithStockAt(final boolean desc) {
        Maybe.fromAction(() -> {
            if (desc) {
                storeList.sort((o1, o2) -> {
                    try {
                        return (int) (DateUtils.createDate(o1.stock_at).getTime() - DateUtils.createDate(o2.stock_at).getTime());
                    } catch (NullPointerException e) {
                        return 0;
                    }
                });
            } else {
                storeList.sort((o2, o1) -> {
                    try {
                        return (int) (DateUtils.createDate(o1.stock_at).getTime() - DateUtils.createDate(o2.stock_at).getTime());
                    } catch (NullPointerException e) {
                        return 0;
                    }
                });
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(this::notifyDataSetChangedWithAnimation)
                .subscribe();
    }

    @Override
    public int getItemCount() {
        return storeList.size();
    }

    private void notifyDataSetChangedWithAnimation() {
        notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.icon) AppCompatImageView icon;
        @BindView(R.id.name) AppCompatTextView name;
        @BindView(R.id.stock) AppCompatTextView stock;
        @BindView(R.id.dist) AppCompatTextView dist;
        @BindView(R.id.stock_at) Chronometer stockAt;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
