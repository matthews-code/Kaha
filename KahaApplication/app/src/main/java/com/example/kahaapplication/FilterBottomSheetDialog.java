package com.example.kahaapplication;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class FilterBottomSheetDialog extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;
    //Filter elements
    private EditText etMinPrice;
    private EditText etMaxPrice;
    private EditText etLength;
    private EditText etWidth;
    private EditText etHeight;
    private Spinner spnSpaceType;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.spaces_filter, container, false);

        etMinPrice = v.findViewById(R.id.et_min_price);
        etMaxPrice = v.findViewById(R.id.et_max_price);
        etLength = v.findViewById(R.id.et_filter_length);
        etWidth = v.findViewById(R.id.et_filter_width);
        etHeight = v.findViewById(R.id.et_filter_height);
        spnSpaceType =(Spinner) v.findViewById(R.id.spn_space_type);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.spaces_array_filter, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSpaceType.setAdapter(adapter);

        Button btnApplyFilters = v.findViewById(R.id.btn_filter_apply);
        btnApplyFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onButtonClicked("Applied Filters!",
                        etMaxPrice.getText().toString().trim(),
                        etMinPrice.getText().toString().trim(),
                        etLength.getText().toString().trim(),
                        etWidth.getText().toString().trim(),
                        etHeight.getText().toString().trim(),
                        spnSpaceType.getSelectedItem().toString().trim()
                );
                dismiss();
            }
        });

        return v;
    }

    public interface BottomSheetListener {
        void onButtonClicked(String text, String maxPrice, String minPrice, String length, String width,
                             String height, String type);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
             + " must implement BottomSheetListener");
        }
    }
}
