package my.yandex.money.transfer.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import java.math.BigDecimal;
import my.yandex.money.transfer.R;
import my.yandex.money.transfer.activities.hierarchy.SecurityActivity;
import my.yandex.money.transfer.utils.EditTextInputFilter;

public class TransferActivity extends SecurityActivity {
    private EditText to;
    private EditText amountDue;
    private EditText amount;
    private EditText message;
    private CheckBox codepro;
    private EditText expirePeriod;
    private SeekBar expirePeriodSeekBar;
    private TextView expirePeriodDescription;

    private boolean changesFromAmount = false;
    private boolean changesFromAmountDue = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        setUpViews();
        setUpCallbacks();
    }


    private void setUpViews() {
        to = (EditText) findViewById(R.id.input_to);
        amountDue = (EditText) findViewById(R.id.input_amount_due);
        amount = (EditText) findViewById(R.id.input_amount);
        message = (EditText) findViewById(R.id.input_message);
        codepro = (CheckBox) findViewById(R.id.input_codepro);
        expirePeriod = (EditText) findViewById(R.id.input_expire_period);
        expirePeriodSeekBar = (SeekBar) findViewById(R.id.seek_bar_expire_period);
        expirePeriodDescription = (TextView) findViewById(R.id.expire_period_description);

        toggleSecurityDetailsVisibility(codepro.isChecked());
    }


    private void setUpCallbacks() {
        amountDue.setFilters(EditTextInputFilter.getFilter());
        amountDue.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) changesFromAmount = false;
            }
        });
        amountDue.addTextChangedListener(new Watcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (changesFromAmount) return;
                changesFromAmountDue = true;
                amount.setText(getPaymentComplement(s, true).toString());
            }
        });


        amount.setFilters(EditTextInputFilter.getFilter());
        amount.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) changesFromAmountDue = false;
            }
        });
        amount.addTextChangedListener(new Watcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (changesFromAmountDue) return;
                changesFromAmount = true;
                amountDue.setText(getPaymentComplement(s, false).toString());
            }
        });


        expirePeriodSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    expirePeriod.setText(Integer.toString(progress + 1));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                expirePeriod.requestFocus();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { /* */ }
        });


        expirePeriod.addTextChangedListener(new Watcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int progress = 1;
                try {
                    progress = Integer.parseInt(s.toString());
                } catch (NumberFormatException ignored) { /* */ }
                expirePeriodSeekBar.setProgress(progress - 1);
            }
        });


        codepro.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleSecurityDetailsVisibility(isChecked);
            }
        });
    }


    private BigDecimal getPaymentComplement(CharSequence src, boolean greater) {
        BigDecimal first = BigDecimal.ZERO;
        try {
            first = new BigDecimal(src.toString()).setScale(2, BigDecimal.ROUND_HALF_UP);
        } catch (NumberFormatException ignored) { /* */ }

        if (first.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal second;
        if (greater) {
            second = first.multiply(new BigDecimal(1.005)).setScale(2, BigDecimal.ROUND_HALF_UP);
            if (second.compareTo(first) == 0) {
                second = second.add(new BigDecimal(0.01)).setScale(2, BigDecimal.ROUND_HALF_UP);
            }
        } else {
            second = first.divide(new BigDecimal(1.005), BigDecimal.ROUND_HALF_UP);
            if (second.compareTo(first) == 0) {
                second = second.subtract(new BigDecimal(0.01)).setScale(2, BigDecimal.ROUND_HALF_UP);
            }
        }

        return second;
    }


    private void toggleSecurityDetailsVisibility(boolean display) {
        int visibility = display ? TextView.VISIBLE : TextView.GONE;
        expirePeriodDescription.setVisibility(visibility);
        expirePeriodSeekBar.setVisibility(visibility);
        expirePeriod.setVisibility(visibility);
    }


    private class Watcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* Stub! */ }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { /* Stub! */ }

        @Override
        public void afterTextChanged(Editable s) { /* Stub! */ }
    }
}
