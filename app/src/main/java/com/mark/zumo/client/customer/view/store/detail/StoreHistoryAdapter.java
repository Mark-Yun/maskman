package com.mark.zumo.client.customer.view.store.detail;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.mark.zumo.client.customer.ContextHolder;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.entity.StoreHistory;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 20. 3. 21.
 */
class StoreHistoryAdapter extends RecyclerView.Adapter<StoreHistoryAdapter.ViewHolder> {

    private static final int[] DAYS_OF_WEEK = {
            R.string.day_of_week_1,
            R.string.day_of_week_2,
            R.string.day_of_week_3,
            R.string.day_of_week_4,
            R.string.day_of_week_5,
            R.string.day_of_week_6,
            R.string.day_of_week_7,
    };

    private final List<StoreHistory> storeHistoryList;

    StoreHistoryAdapter() {
        this.storeHistoryList = new CopyOnWriteArrayList<>();
    }

    void setStoreHistoryList(final List<StoreHistory> storeHistoryList) {
        this.storeHistoryList.clear();
        this.storeHistoryList.addAll(storeHistoryList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        LayoutInflater layoutInflater = parent.getContext().getSystemService(LayoutInflater.class);
        View view = layoutInflater.inflate(R.layout.card_view_store_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Context context = ContextHolder.getContext();
        if (position == 0) {
            holder.label.setText("");
            holder.start.setText(context.getString(R.string.stock_at));
            holder.end.setText(context.getString(R.string.empty_at));
            return;
        }
        int dayOfWeek = Calendar.getInstance(Locale.getDefault()).get(Calendar.DAY_OF_WEEK);
        int dayOfPosition = (position - 1 + dayOfWeek) % 7;
        final StoreHistory storeHistory = storeHistoryList.get(dayOfPosition);

        holder.label.setText(DAYS_OF_WEEK[position - 1]);
        if (!TextUtils.isEmpty(storeHistory.stock_at) && storeHistory.stock_at.length() > 5) {
            holder.start.setText(storeHistory.stock_at.substring(0, 5));
        } else {
            holder.start.setText("-");
        }

        if (!TextUtils.isEmpty(storeHistory.empty_at) && storeHistory.empty_at.length() > 5) {
            holder.end.setText(storeHistory.empty_at.substring(0, 5));
        } else {
            holder.end.setText("-");
        }

        if (position == 6) {
            holder.label.setTextColor(context.getColor(android.R.color.holo_blue_light));
        } else if (position == 7) {
            holder.label.setTextColor(context.getColor(android.R.color.holo_red_light));
        } else {
            holder.label.setTextColor(context.getColor(android.R.color.darker_gray));
        }
    }

    @Override
    public int getItemCount() {
        return this.storeHistoryList.size() + 1;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.label) AppCompatTextView label;
        @BindView(R.id.start) AppCompatTextView start;
        @BindView(R.id.end) AppCompatTextView end;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
