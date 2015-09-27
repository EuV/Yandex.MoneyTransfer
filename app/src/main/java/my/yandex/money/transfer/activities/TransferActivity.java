package my.yandex.money.transfer.activities;

import android.os.Bundle;
import my.yandex.money.transfer.R;
import my.yandex.money.transfer.activities.hierarchy.SecurityActivity;

public class TransferActivity extends SecurityActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
    }
}
