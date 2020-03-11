package com.mark.zumo.client.customer.view.permission;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.mark.zumo.client.customer.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 19. 7. 14.
 */
class PermissionAdapter extends RecyclerView.Adapter<PermissionAdapter.PermissionViewHolder> {

    @NonNull
    @Override
    public PermissionViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        LayoutInflater layoutInflater = parent.getContext().getSystemService(LayoutInflater.class);
        View view = layoutInflater.inflate(R.layout.card_view_permission, parent, false);
        return new PermissionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PermissionViewHolder holder, final int position) {
        holder.icon.setImageResource(Permissions.PERMISSION_ICONS[position]);
        holder.permission.setText(Permissions.PERMISSION_NAMES[position]);
        holder.description.setText(Permissions.PERMISSION_DESCRIPTIONS[position]);
    }

    @Override
    public int getItemCount() {
        return Permissions.PERMISSIONS.length;
    }

    class PermissionViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.icon) AppCompatImageView icon;
        @BindView(R.id.permission) AppCompatTextView permission;
        @BindView(R.id.description) AppCompatTextView description;

        PermissionViewHolder(@NonNull final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
