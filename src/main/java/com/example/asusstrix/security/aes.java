package com.example.asusstrix.security;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class aes {

    //region Decrypt/Encrypt

    public static byte[] generateKey(String secret) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] key = (secret).getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16); // use only first 128 bit

        return key;
    }
    public static String encrypt(String plainText,String pwd) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec secretKey = new SecretKeySpec(generateKey(pwd), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] cipherText = cipher.doFinal(plainText.getBytes("UTF8"));
            return new String(Base64.getEncoder().encode(cipherText),"UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String encryptedText,String pwd) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            SecretKeySpec secretKey = new SecretKeySpec(generateKey(pwd), "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] cipherText = Base64.getDecoder().decode(encryptedText.getBytes("UTF8"));
            return new String(cipher.doFinal(cipherText),"UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedText;
    }


    //endregion
}
