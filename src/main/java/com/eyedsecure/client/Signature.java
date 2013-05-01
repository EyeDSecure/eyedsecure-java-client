package com.eyedsecure.client;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

public class Signature {


    /**
     *
     * @param data
     * @param key
     * @param macAlgorithm  HmacSHA1, HmacSHA256, HmacSHA384, HmacSHA512
     * @return
     * @throws SignatureException
     */
	public static String calculate(String data, byte[] key, String macAlgorithm) throws SignatureException {
		try {
			SecretKeySpec signingKey = new SecretKeySpec(key, macAlgorithm);
			Mac mac = Mac.getInstance(macAlgorithm);
			mac.init(signingKey);        
			byte[] raw = mac.doFinal(data.getBytes("UTF-8"));
			return new String(Base64.encodeBase64(raw));
		} catch (InvalidKeyException e) {
			throw new SignatureException("Invalid sharedKey in signature.", e);
        } catch (NoSuchAlgorithmException e) {
            throw new SignatureException("No such algorithm for signature", e);
		} catch (IllegalStateException e) {
			throw new SignatureException("Illegal state in signature", e);
		} catch (UnsupportedEncodingException e) {
			throw new SignatureException("Unsupported encoding for signature", e);
		}
	}

    public static String calculate(String data, byte[] key) throws SignatureException {
        return calculate(data, key, "HmacSHA1");
    }

}
