package com.aliakbar.android.prod.bill;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.aliakbar.android.prod.R;

public class CartAdjustFragment extends Fragment {
    static int LIMIT, VALUE;
    TextView valueText, limitText;
    EditText enterValueInput;
    TextView apply, cancel;

    public View onCreateView(LayoutInflater inflater, ViewGroup vg,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart_adjust, vg, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        valueText = view.findViewById(R.id.realValue);
        limitText = view.findViewById(R.id.limitValue);

        enterValueInput = view.findViewById(R.id.value_text);
        enterValueInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!charSequence.toString().matches("")) {
                    try {
                        int fromEditText = Integer.parseInt(charSequence.toString());
                        if (fromEditText <= LIMIT && fromEditText > 0) {
                            VALUE = fromEditText;
                            enterValueInput.setError(null);
                        } else {
                            VALUE = 0;
                            enterValueInput.setError("Number Crossed The Limit");
                        }
                    } catch (NumberFormatException e) {
                        Log.i("", " is not a number");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        apply = view.findViewById(R.id.apply_adjust);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (VALUE <= LIMIT && VALUE > 0) {
                    ((CartAdjustActivity) getActivity()).fromFragmentAdjust(VALUE);
                    LIMIT = 0;
                    VALUE = 0;
                    getActivity().onBackPressed();
                }

            }
        });

        cancel = view.findViewById(R.id.cancel_adjust);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LIMIT = 0;
                VALUE = 0;
                getActivity().onBackPressed();
            }
        });

    }

    public void setupInput(int value, int limit) {
        ((CartAdjustActivity) getActivity()).setEnabledFalse();
        LIMIT = 0;
        VALUE = 0;
        limitText.setVisibility(View.GONE);
        if (value < 0) {
            int temp = value * -1;
            if (limit == 0) {
                valueText.setText("Substarct " + temp + " From Items to Stable.");
                LIMIT = value;
            } else {
                valueText.setText("Substarct " + temp + " From Items to Stable.");
                limitText.setVisibility(View.VISIBLE);
                limitText.setText("This Item Can Only Substarct Upto " + limit);
                LIMIT = limit;
            }
        } else {
            valueText.setText("Add " + value + " To The Items to Stable.");
            LIMIT = value;
        }
    }
}
