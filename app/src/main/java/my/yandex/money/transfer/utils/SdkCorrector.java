package my.yandex.money.transfer.utils;

import com.yandex.money.api.methods.AccountInfo;
import com.yandex.money.api.model.Avatar;
import com.yandex.money.api.model.BalanceDetails;
import java.util.Arrays;


/**
 * Class 'Objects' requires API level 19 or higher,
 * but Objects.equals() and Objects.hash() are placed in the SDK...
 */
public final class SdkCorrector {
    private SdkCorrector() { /* */ }

    public static int hashCode(Object o) {
        if (o instanceof AccountInfo) {
            return hashCode((AccountInfo) o);
        }
        if (o instanceof BalanceDetails) {
            return hashCode((BalanceDetails) o);
        }
        if (o instanceof Avatar) {
            return hashCode((Avatar) o);
        }
        return o.hashCode();
    }

    public static int hashCode(AccountInfo info) {
        int result = info.account.hashCode();
        result = 31 * result + info.balance.hashCode();
        result = 31 * result + info.currency.hashCode();
        result = 31 * result + info.accountStatus.hashCode();
        result = 31 * result + info.accountType.hashCode();
        result = 31 * result + (info.avatar != null ? hashCode(info.avatar) : 0);
        result = 31 * result + hashCode(info.balanceDetails);
        result = 31 * result + info.linkedCards.hashCode();
        result = 31 * result + info.additionalServices.hashCode();
        result = 31 * result + info.yandexMoneyCards.hashCode();
        return result;
    }

    public static int hashCode(BalanceDetails details) {
        return Arrays.hashCode(new Object[]{
            details.total, details.available, details.depositionPending,
            details.blocked, details.debt, details.hold
        });
    }

    public static int hashCode(Avatar avatar) {
        return Arrays.hashCode(new Object[]{avatar.url, avatar.timestamp});
    }
}
