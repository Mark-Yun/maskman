package com.mark.zumo.client.customer.view.store.detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.mark.zumo.client.customer.ContextHolder;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.util.Navigation;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 20. 3. 14.
 */
class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.ViewHolder> {

    private final Context context;
    private final List<Navigation> navigationList;

    private final double lat, lng;
    private final String name;
    private final Runnable onSelectNavigation;

    NavigationAdapter(final double lat, final double lng, final String name, final Runnable onSelectNavigation) {
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.onSelectNavigation = onSelectNavigation;

        context = ContextHolder.getContext();
        navigationList = Arrays.stream(Navigation.values())
                .filter(navigation -> navigation.isInstalled(context))
                .collect(Collectors.toList());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        LayoutInflater layoutInflater = parent.getContext().getSystemService(LayoutInflater.class);
        View view = layoutInflater.inflate(R.layout.card_view_select_navigation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Navigation navigation = navigationList.get(position);

        holder.icon.setImageDrawable(navigation.getPackageIcon(context));
        holder.icon.setOnClickListener(v -> {
            navigation.startNavigation(lat, lng, name);
            onSelectNavigation.run();
        });
        holder.name.setText(navigation.getApplicationName(context));
    }

    @Override
    public int getItemCount() {
        return navigationList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.icon) AppCompatImageView icon;
        @BindView(R.id.name) AppCompatTextView name;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
