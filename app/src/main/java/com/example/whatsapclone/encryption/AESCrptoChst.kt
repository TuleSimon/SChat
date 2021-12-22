package com.example.whatsapclone.encryption


import android.util.Base64
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class AESCrptoChst (key: String) {
    var keyValue: ByteArray

    @Throws(Exception::class)
    fun encrypt(plainText: String): String {
        val key = generateKey()
        val c =
            Cipher.getInstance(ALGORITHM)
        c.init(Cipher.ENCRYPT_MODE, key)
        val encVal = c.doFinal(plainText.toByteArray())
        return encode(encVal)!!
    }

    fun decode(encodedData: String?): ByteArray? {
        return if (encodedData == null) {
            null
        } else Base64.decode(encodedData, 0)
    }


    fun encode(data: ByteArray?): String? {
        return if (data == null) {
            null
        } else Base64.encodeToString(data, 0)
    }


    @Throws(Exception::class)
    fun decrypt(cipherText: String?): String {
        val key = generateKey()
        val c =  Cipher.getInstance(ALGORITHM)
        c.init(Cipher.DECRYPT_MODE, key)
        val decodedValue: ByteArray = decode(cipherText)!!
        val decValue = c.doFinal(decodedValue)
        return String(decValue)
    }

    @Throws(Exception::class)
    private fun generateKey(): Key {
        return SecretKeySpec(keyValue, ALGORITHM)
    } //    public static void main(String[] args) {

    //        try {
    //
    //            AESCrypt aes = new AESCrypt("lv39eptlvuhaqqsr");
    //
    //            String encryptedText = aes.encrypt("Meet is a good Boy");
    //            System.out.println("Encrypted Text - " + encryptedText);
    //
    //            String decryptedText = aes.decrypt(encryptedText);
    //            System.out.println("Decrypted Text - " + decryptedText);
    //
    //        } catch (Exception e) {
    //
    //            Logger.getLogger(AESCrypt.class.getName()).log(Level.SEVERE, null, e);
    //        }
    //    }
    companion object {
        const val ALGORITHM = "AES"
    }

    init {
        keyValue = key.toByteArray()
    }
}