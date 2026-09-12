package com.example.demo.test;

public interface BtyesEncryptor{
    byte[] encrypt(byte[] byteArray);
    byte[] decrypt(byte[] encryptedText);
}
