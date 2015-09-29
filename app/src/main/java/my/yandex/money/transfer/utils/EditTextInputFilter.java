package my.yandex.money.transfer.utils;

import android.text.InputFilter;
import android.text.Spanned;
import java.util.regex.Pattern;

public final class EditTextInputFilter implements InputFilter {
    private static final InputFilter[] filter = new InputFilter[]{new EditTextInputFilter()};
    private static final Pattern mPattern = Pattern.compile("[0-9]{0,20}+((\\.[0-9]{0,1})?)||(\\.)?");

    private EditTextInputFilter() { /* */ }

    public static InputFilter[] getFilter() {
        return filter;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        return !mPattern.matcher(dest).matches() ? "" : null;
    }
}
