package com.shinemo.smartgrid.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AESUtil {

	private static final Logger log = LoggerFactory.getLogger(AESUtil.class);

	private static int TAG_LENGTH = 16;
	private static int IV_LENGTH = 12;
	private static String AES_MODE = "AES/GCM/PKCS5Padding";
	private static int KEY_SIZE = 256;

	public static String encrypt(String plainText, String key) {
		if (key != null && key.length() == TAG_LENGTH) {
			try {
				SecretKeySpec secretKeySpec = getSecretKeySpec(key);
				Cipher cipher = Cipher.getInstance(AES_MODE);
				byte[] bytePlainText = plainText.getBytes("UTF-8");
				byte[] initVector = new byte[IV_LENGTH];
				SecureRandom random = new SecureRandom();
				random.nextBytes(initVector);
				GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH * 8, initVector);
				cipher.init(1, secretKeySpec, spec);
				byte[] cipherText = cipher.doFinal(bytePlainText);
				byte[] cipherTextWithIv = new byte[cipherText.length + IV_LENGTH];
				System.arraycopy(initVector, 0, cipherTextWithIv, 0, IV_LENGTH);
				System.arraycopy(cipherText, 0, cipherTextWithIv, IV_LENGTH, cipherText.length);
				return parseByte2HexStr(cipherTextWithIv);
			} catch (Exception e) {
				log.error("[encrypt] error,plainText:" + plainText + ",key:" + key, e);
				return null;
			}
		} else {
			log.error("[encrypt] key error,plainText:" + plainText + ",key:" + key);
		}
		return null;
	}

	public static String decrypt(String content, String key) {
		if (key != null && key.length() == TAG_LENGTH) {
			byte[] encryptedtext = parseHexStr2Byte(content);
			byte[] plaintext = null;
			try {
				byte[] initVector = new byte[IV_LENGTH];
				byte[] cipherText = new byte[encryptedtext.length - IV_LENGTH];
				System.arraycopy(encryptedtext, 0, initVector, 0, IV_LENGTH);
				System.arraycopy(encryptedtext, IV_LENGTH, cipherText, 0, cipherText.length);
				GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH * 8, initVector);
				SecretKeySpec secretKeySpec = getSecretKeySpec(key);
				Cipher cipher = Cipher.getInstance(AES_MODE);
				cipher.init(2, secretKeySpec, spec);
				plaintext = cipher.doFinal(cipherText);
				return new String(plaintext);
			} catch (Exception e) {
				log.error("[decrypt] error,content:" + content + ",key:" + key, e);
			}
			return null;
		} else {
			return null;
		}
	}

	private static SecretKeySpec getSecretKeySpec(String key)
		throws NoSuchAlgorithmException, UnsupportedEncodingException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
		secureRandom.setSeed(key.getBytes("UTF-8"));
		keygen.init(KEY_SIZE, secureRandom);
		SecretKey secretKey = keygen.generateKey();
		byte[] keyByte = secretKey.getEncoded();
		SecretKeySpec secretKeySpec = new SecretKeySpec(keyByte, "AES");
		return secretKeySpec;
	}

	private static String parseByte2HexStr(byte[] buf) {
		StringBuilder hs = new StringBuilder();

		for (int n = 0; buf != null && n < buf.length; ++n) {
			String stmp = Integer.toHexString(buf[n] & 255);
			if (stmp.length() == 1) {
				hs.append('0');
			}

			hs.append(stmp);
		}

		return hs.toString().toLowerCase();
	}

	private static byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1) {
			return null;
		} else {
			byte[] result = new byte[hexStr.length() / 2];

			for (int i = 0; i < hexStr.length() / 2; ++i) {
				int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
				int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
				result[i] = (byte) (high * 16 + low);
			}

			return result;
		}
	}

	public static void main(String[] args) {

		String mobile = "18817350871";
		String seed = "71a25f582a266454";
		String ret = encrypt(mobile, seed);
		String expect = "cc128b78b781eb9614d79af2f88abad3044bd53e8bc1123d0009fc1fe99868d7c929e2a3c015cb";
		System.out.println(ret);
		System.out.println(expect.equals(ret));

		String expect1 = decrypt(expect, seed);
		System.out.println(expect1);

		String ret1 = decrypt(ret, seed);
		System.out.println(ret1);

	}

}