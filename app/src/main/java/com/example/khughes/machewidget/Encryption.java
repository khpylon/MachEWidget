package com.example.khughes.machewidget;

import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import androidx.preference.PreferenceManager;

import com.example.khughes.machewidget.R;
import com.example.khughes.machewidget.StoredData;
import com.example.khughes.machewidget.db.UserInfoDao;
import com.example.khughes.machewidget.db.UserInfoDatabase;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.GCMParameterSpec;

public class Encryption {

    // Code for encryption and decryption of personal data
    private static final String AndroidKeyStore = "AndroidKeyStore";
    private static final String AES_MODE = "AES/GCM/NoPadding";
    private static final String KEY_ALIAS = "MacheEWidget";
    private static final int GCM_IV_LENGTH = 12;
    private static KeyStore keyStore = null;

    private final Context context;

    public Encryption(Context context) {
        this.context = context;
    }

    // Generate a key in the Android Keystore
    private void generateKey() {
        try {
            keyStore = KeyStore.getInstance(AndroidKeyStore);
            keyStore.load(null);
            if (!keyStore.containsAlias(KEY_ALIAS)) {
                KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, AndroidKeyStore);
                keyGenerator.init(
                        new KeyGenParameterSpec.Builder(KEY_ALIAS,
                                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                                .setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                                .setRandomizedEncryptionRequired(false)
                                .build());
                keyGenerator.generateKey();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get the application's secret key; password is randomly generated for the app
    private java.security.Key getSecretKey(char[] password) throws Exception {
        generateKey();
        return keyStore.getKey(KEY_ALIAS, password);
    }

    // Encrypt
    private String encrypt(char[] password, String input) throws Exception {
        //Prepare the nonce
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[GCM_IV_LENGTH];
        secureRandom.nextBytes(iv);

        Cipher c = Cipher.getInstance(AES_MODE);
        Key key = getSecretKey(password);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
        c.init(Cipher.ENCRYPT_MODE, key, parameterSpec);
        byte[] encodedBytes = c.doFinal(input.getBytes(StandardCharsets.UTF_8));

        // Put IV and cipherText into a Base64 String for storage
        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encodedBytes.length);
        byteBuffer.put(iv);
        byteBuffer.put(encodedBytes);
        return Base64.getEncoder().encodeToString(byteBuffer.array());
    }

    private String decrypt(char[] password, String input) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_MODE);
        Key key = getSecretKey(password);

        // Get byte[] back from stored string
        byte[] cipherBytes = Base64.getDecoder().decode(input);

        // Pull the IV out of the packet
        GCMParameterSpec gcmIv = new GCMParameterSpec(128, cipherBytes, 0, GCM_IV_LENGTH);
        cipher.init(Cipher.DECRYPT_MODE, key, gcmIv);

        // Everything else is the ciphertext
        byte[] plainText = cipher.doFinal(cipherBytes, GCM_IV_LENGTH, cipherBytes.length - GCM_IV_LENGTH);
        return new String(plainText, StandardCharsets.UTF_8);
    }

    public String getPlaintextString(String cipher) {
        String value = null;

        // Just in case, make sure we're supposed to save this
        boolean savingCredentials = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getResources().getString(R.string.save_credentials_key), true);
        if (savingCredentials) {
            StoredData appInfo = new StoredData(context);
            String password = appInfo.getSecretPassword();
            if (password != null && cipher != null) {
                try {
                    value = decrypt(password.toCharArray(), cipher);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    public String getCryptoString(String value) {
        String cipher = null;

        // Just in case, make sure we're supposed to save this
        boolean savingCredentials = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getResources().getString(R.string.save_credentials_key), true);
        if (savingCredentials) {
            StoredData appInfo = new StoredData(context);
            String password = appInfo.getSecretPassword();
            if (password != null && value != null) {
                try {
                    cipher = encrypt(password.toCharArray(), value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return cipher;
    }

    public void clearCredentials() {
        new Thread( () -> {
            UserInfoDatabase.getInstance(context).userInfoDao().clearCredentials();
        }).start();
    }

}
