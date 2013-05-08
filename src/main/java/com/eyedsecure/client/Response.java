package com.eyedsecure.client;

import java.util.Map;
public class Response {

    private String action;                      // Action requested
    private String tokenId;                     // Public Token Id
    private String sig;                         // Signature
    private String serverTimeStamp;             // Server timestamp in UTC
    private ResponseCode responseCode;          // Response
    private String nonce;                       // Echos back the nonce from the request. Should match.
    private Map<String, String> keyValueMap;    // Map of response properties

    private byte[] image;                       // optional image as a byte array for image requests

	/**
	 * Signature of the response, with the same API key as the request.
	 * 
	 * @return response signature
	 */
    public String getSig() {
        return sig;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }


    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    /**
     *
     * Server response to the request.
     * 
     * @return response status
     */
    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }


    /**
     * Echos back the nonce from the request. Should match. 
     * @return nonce
     */
    public String getNonce() {
        return nonce;
    }
    
    /**
     * Returns all parameters from the response as a Map
     * 
     * @return map of all values
     */
    public Map<String, String> getKeyValueMap() {
        return keyValueMap;
    }

    public void setKeyValueMap(Map<String, String> keyValueMap) {
        this.keyValueMap = keyValueMap;
    }


    /**
     * Returns the public id of the returned OTP
     * 
     * @return public id
     */
    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }


    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    /**
     * Action requested
     * @return
     */
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getServerTimeStamp() {
        return serverTimeStamp;
    }

    public void setServerTimeStamp(String serverTimeStamp) {
        this.serverTimeStamp = serverTimeStamp;
    }
}
