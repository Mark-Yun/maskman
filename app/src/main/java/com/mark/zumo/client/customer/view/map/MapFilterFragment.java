package com.mark.zumo.client.customer.view.map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.mark.zumo.client.customer.R;
import com.mark.zumo.client.customer.entity.Store;
import com.mark.zumo.client.customer.util.FilterSettingUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mark on 20. 3. 11.
 */
public class MapFilterFragment extends Fragment {

    public static MapFilterFragment newInstance() {

        Bundle args = new Bundle();

        MapFilterFragment fragment = new MapFilterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.color_plenty) AppCompatTextView colorPlenty;
    @BindView(R.id.checkbox_plenty) MaterialCheckBox checkboxPlenty;
    @BindView(R.id.color_some) AppCompatTextView colorSome;
    @BindView(R.id.checkbox_some) MaterialCheckBox checkboxSome;
    @BindView(R.id.color_few) AppCompatTextView colorFew;
    @BindView(R.id.checkbox_few) MaterialCheckBox checkboxFew;
    @BindView(R.id.color_empty) AppCompatTextView colorEmpty;
    @BindView(R.id.checkbox_empty) MaterialCheckBox checkboxEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_filter, container, false);
        ButterKnife.bind(this, view);
        inflate();
        return view;
    }

    private void inflate() {
        checkboxPlenty.setChecked(FilterSettingUtils.isShownFilterSetting(getContext(), Store.PLENTY));
        checkboxPlenty.setOnCheckedChangeListener((buttonView, isChecked) ->
                FilterSettingUtils.setFilterSetting(getContext(), Store.PLENTY, isChecked)
        );
        checkboxSome.setChecked(FilterSettingUtils.isShownFilterSetting(getContext(), Store.SOME));
        checkboxSome.setOnCheckedChangeListener((buttonView, isChecked) ->
                FilterSettingUtils.setFilterSetting(getContext(), Store.SOME, isChecked)
        );
        checkboxFew.setChecked(FilterSettingUtils.isShownFilterSetting(getContext(), Store.FEW));
        checkboxFew.setOnCheckedChangeListener((buttonView, isChecked) ->
                FilterSettingUtils.setFilterSetting(getContext(), Store.FEW, isChecked)
        );
        checkboxEmpty.setChecked(FilterSettingUtils.isShownFilterSetting(getContext(), Store.EMPTY));
        checkboxEmpty.setOnCheckedChangeListener((buttonView, isChecked) ->
                FilterSettingUtils.setFilterSetting(getContext(), Store.EMPTY, isChecked)
        );
    }
}
