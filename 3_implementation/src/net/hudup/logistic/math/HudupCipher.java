package net.hudup.logistic.math;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class HudupCipher {
	
	
	/**
	 * 
	 */
	private static final String CALG = "AES";
	
	
	/**
	 * 
	 */
	private static SecretKey secretKey = null;
	
	
	/**
	 * 
	 */
	public HudupCipher() {
		secretKey = new SecretKeySpec(new TransferKey().transfer(), CALG); 
	}

	
	/**
	 * 
	 * @param data
	 * @return encrypted text
	 */
	public String encrypt(String data) {
		try {
	        Cipher cipher = Cipher.getInstance(CALG);
	        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
	        
	        byte[] encode = cipher.doFinal(data.getBytes());
	        String code = new BASE64Encoder().encode(encode);
	        return toHex(code);
		}
		catch (Exception e) {
			e.printStackTrace();
			return data;
		}
		
    }

	
	/**
	 * 
	 * @param encrypted
	 * @return plain text
	 */
	public String decrypt(String encrypted) {
		try {
	        Cipher cipher = Cipher.getInstance(CALG);
	        cipher.init(Cipher.DECRYPT_MODE, secretKey);
	        
	        String code = fromHex(encrypted);
	        byte[] decode = new BASE64Decoder().decodeBuffer(code);
	        String text = new String(cipher.doFinal(decode));
	        
	        return text;
		}
		catch (Exception e) {
			e.printStackTrace();
			return encrypted;
		}
		
    }
	
	
	/**
	 * 
	 * @param text
	 * @return hex text
	 */
	private static String toHex(String text) {
		byte[] bytes = text.getBytes();
		
		StringBuilder buffer = new StringBuilder(); 
		for(int i = 0; i < bytes.length; i++) 
			buffer.append(String.format("%x", bytes[i]));
	    
		return buffer.toString(); 
	} 
	
	
	/**
	 * 
	 * @param hexText
	 * @return plain text
	 */
	private static String fromHex(String hexText) { 
		StringBuilder buffer = new StringBuilder();
	    
		for (int i = 0; i < hexText.length(); i += 2) { 
			buffer.append( (char) Integer.parseInt(hexText.substring(i, i + 2), 16)); 
		} 
	    return buffer.toString(); 
	} 
	
	
	/**
	 * 
	 */
	public static void compileTransfer() {
		try {
			// 1. Input dialog or automatically
			// 2. Create java source (TransferKey) as memory String so as to change the return value of TransferKey.transfer
			// 3. Save such memory String to TransferKey.java
			// 4. Runtime.getRuntime().exec("javac TransferKey.java") so as to override current key;
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	/**
	 * 
	 * @param keyStore
	 * @return {@link SecretKey}
	 */
	@SuppressWarnings("unused")
	private static SecretKey loadKey(xURI keyStore) {
		UriAdapter adapter = new UriAdapter(keyStore);
		boolean existed = adapter.exists(keyStore);
		adapter.close();
		if (!existed)
			return null;
		
		try {
			adapter = new UriAdapter(keyStore);
			List<StringBuffer> lines = adapter.readLines(keyStore);
			adapter.close();
			if (lines.size() == 0)
				return null;
			String line = lines.get(0).toString().trim();
			if (line.isEmpty())
				return null;
			
			byte[] encoded = new BigInteger(line, 16).toByteArray(); 
			
		    return new SecretKeySpec(encoded, CALG); 
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/**
	 * 
	 * @param keyStore
	 * @param key
	 * @return whether save {@link SecretKey} successfully
	 */
	@SuppressWarnings("unused")
	private static boolean saveKey(xURI keyStore, SecretKey key) {
		byte[] encoded = key.getEncoded();
		
	    String keydesc = new BigInteger(1, encoded).toString(16); 
		UriAdapter adapter = new UriAdapter(keyStore);
		boolean success = adapter.saveText(keyStore, keydesc, false);
		adapter.close();
	    return success; 
	}
	
	
	/**
	 * 
	 * @return {@link SecretKey}
	 */
	@SuppressWarnings("unused")
	private static SecretKey genKey() {
		try {
			return KeyGenerator.getInstance(CALG).generateKey();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	
	/**
	 * 
	 * @param text
	 * @return md5 text
	 */
	public String md5Encrypt(String text) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(text.getBytes());
			StringBuffer buffer = new StringBuffer();
			for (Byte c : digest)
				buffer.append(c.toString());
			
			return buffer.toString();
		} 
		catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
}
