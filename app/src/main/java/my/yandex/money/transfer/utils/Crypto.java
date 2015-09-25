package my.yandex.money.transfer.utils;

import android.util.Base64;
import android.util.Log;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class Crypto {
    private static final String TAG = Crypto.class.getName();

    private Crypto() { /* */ }

    public static String encrypt(String plainToken, String pin) {
        try {
            Cipher cipher = getCipher();
            cipher.init(Cipher.ENCRYPT_MODE, getKeySpec(pin), getIvSpec());
            byte[] encryptedToken = cipher.doFinal(plainToken.getBytes());
            return Base64.encodeToString(encryptedToken, Base64.DEFAULT);
        } catch (Exception e) {
            Log.d(TAG, "Encryption has failed!");
        }
        return null;
    }

    public static String decrypt(String encryptedToken, String pin) {
        try {
            Cipher cipher = getCipher();
            cipher.init(Cipher.DECRYPT_MODE, getKeySpec(pin), getIvSpec());
            byte[] plainToken = cipher.doFinal(Base64.decode(encryptedToken, Base64.DEFAULT));
            return new String(plainToken);
        } catch (Exception e) {
            Log.d(TAG, "Decryption has failed!");
        }
        return null;
    }

    private static Cipher getCipher() throws Exception {
        return Cipher.getInstance("DESede/CBC/PKCS5Padding");
    }

    private static IvParameterSpec getIvSpec() {
        return new IvParameterSpec(Preferences.getInitializationVector(8));
    }

    private static SecretKeySpec getKeySpec(String pin) {
        byte[] key = Preferences.getBaseKey(16);
        byte[] salt = pin.getBytes();
        if (salt.length > key.length) throw new IllegalArgumentException("PIN is too long!");

        // Since key length for 3DES must be equal to 16,
        // the beginning of it is defined by the user (PIN-code)
        // and the rest part is generated automatically
        System.arraycopy(salt, 0, key, 0, salt.length);
        return new SecretKeySpec(key, "DESede");
    }
}
