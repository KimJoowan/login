package com.example.demo.test;


import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

public class SaltTest {
		
	@Test
	public void name() {
		String salt = KeyGenerators.string().generateKey(); 
		String password = "my_secure_password";
		String valueToEncrypt = "HELLO";
		
		TextEncryptor e = Encryptors.text(password, salt);
		String encrypted = e.encrypt(valueToEncrypt);
		String decrypted = e.decrypt(encrypted);
		
		System.out.println("암호화 결과: " + encrypted);
		System.out.println("복호화 결과: " + decrypted);
		
		decrypted = e.decrypt(encrypted);
		
		System.out.println("복호화 결과: " + decrypted);
	}
}
