package net.hudup.core.logistic;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


/**
 * This utility class provides methods of encryption and dercyption.
 * It also contains the secret key for encrypting internal information of Hudup framework.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Cipher {
	
	
	/**
	 * Name of symmetric cipher algorithm (secret-key encryption/decryption algorithm) in Hudup Framework.
	 */
	private static final String CALG = "AES";
	
	
	/**
	 * The secret key for encrypting internal information of Hudup framework.
	 */
	private static SecretKey secretKey = null;
	
	
	/**
	 * Default constructor. It creates the secret key {@link #secretKey} for encrypting internal information of Hudup framework.
	 */
	public Cipher() {
		secretKey = new SecretKeySpec(
				new byte[] { 's', 't', 'b', 'q', 's', 'k', '6', '1', 'm', 'q', 'e','g', 'x', 'i', 'o', 'k' }, 
				CALG); 
	}

	
	/**
	 * Encrypting specified text data by symmetric cipher algorithm whose name is specified by constants {@link #CALG}.
	 * @param data specified text data
	 * @return encrypted text by symmetric cipher algorithm whose name is specified by constants {@link #CALG}.
	 */
	public String encrypt(String data) {
		try {
			javax.crypto.Cipher c = javax.crypto.Cipher.getInstance(CALG);
	        c.init(javax.crypto.Cipher.ENCRYPT_MODE, secretKey);
	        
	        byte[] encode = c.doFinal(data.getBytes());
	        String code = new BASE64Encoder().encode(encode);
	        return toHex(code);
		}
		catch (Exception e) {
			e.printStackTrace();
			return data;
		}
		
    }

	
	/**
	 * Decrypting the specified encrypted data by symmetric cipher algorithm whose name is specified by constants {@link #CALG}.
	 * 
	 * @param encrypted specified encrypted data
	 * @return plain text decrypted from specified encrypted data by symmetric cipher algorithm whose name is specified by constants {@link #CALG}.
	 */
	public String decrypt(String encrypted) {
		try {
			javax.crypto.Cipher c = javax.crypto.Cipher.getInstance(CALG);
	        c.init(javax.crypto.Cipher.DECRYPT_MODE, secretKey);
	        
	        String code = fromHex(encrypted);
	        byte[] decode = new BASE64Decoder().decodeBuffer(code);
	        String text = new String(c.doFinal(decode));
	        
	        return text;
		}
		catch (Exception e) {
			e.printStackTrace();
			return encrypted;
		}
		
    }
	
	
	/**
	 * Converting the plain text into the string of hex numbers.
	 * @param text specified plain text.
	 * @return string of hex numbers converted from specified plain text. 
	 */
	private static String toHex(String text) {
		byte[] bytes = text.getBytes();
		
		StringBuilder buffer = new StringBuilder(); 
		for(int i = 0; i < bytes.length; i++) 
			buffer.append(String.format("%x", bytes[i]));
	    
		return buffer.toString(); 
	} 
	
	
	/**
	 * Converting string of hex numbers into the the plain text. This method is opposite to the method {@link #toHex(String)}.
	 * @param hexText string of hex numbers.
	 * @return plain text converted from the string of hex numbers.
	 */
	private static String fromHex(String hexText) { 
		StringBuilder buffer = new StringBuilder();
	    
		for (int i = 0; i < hexText.length(); i += 2) { 
			buffer.append( (char) Integer.parseInt(hexText.substring(i, i + 2), 16)); 
		} 
	    return buffer.toString(); 
	} 
	
	
	/**
	 * Encrypting the specified plain text into the MD5-encrypted text by MD5 algorithm.
	 * @param text specified plain text.
	 * @return MD5-encrypted text encrypted from specified plain text by MD5 algorithm.
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
