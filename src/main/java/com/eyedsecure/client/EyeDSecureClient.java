package com.eyedsecure.client;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: boksman
 * Date: 4/29/13
 * Time: 8:39 PM
 */
@SuppressWarnings("unused")
public class EyeDSecureClient {

    protected EyeDSecureService service;

    protected String clientId;
    protected byte[] sharedKey;

    protected String urls[] = {
            "http://localhost:8080/eyedsecure-server/API", // todo: setup https
            // todo: add secondary
    };

    protected String userAgent;

    private static final Integer OTP_MIN_LEN = 4;
    private static final Integer OTP_MAX_LEN = 12;


    public EyeDSecureClient(String clientId, String sharedKey) {
        setClientId(clientId);
        setSharedKey(sharedKey);
        service = new EyeDSecureService();
    }


    protected static String getNonce() {
        return java.util.UUID.randomUUID().toString().replaceAll("-", "");
    }

    public Response activate(String tokenId, boolean activate) throws RequestException {
        String nonce = java.util.UUID.randomUUID().toString().replaceAll("-", "");
        return activate(tokenId, nonce, activate);
    }

    protected String getParamString(String nonce, String tokenId) throws RequestException {
        return getParamString(nonce, tokenId, null, null);
    }


    protected String getParamString(String nonce, String tokenId, String otp, String challengeId) throws RequestException {
        Map<String, String> reqMap = new HashMap<String, String>();
        if(nonce!=null) reqMap.put("no", nonce);
        if(clientId!=null) reqMap.put("id", String.valueOf(clientId));
        if(tokenId!=null) reqMap.put("tid", tokenId);
        if(otp!=null) reqMap.put("otp", otp);
        if(challengeId!=null) reqMap.put("cid", challengeId);


        StringBuilder paramStrBuilder = new StringBuilder();
        for (String key : reqMap.keySet()) {
            if (paramStrBuilder.length() != 0) {
                paramStrBuilder.append("&");
            }
            try {
                paramStrBuilder.append(key).append("=").append(URLEncoder.encode(reqMap.get(key), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RequestException("Failed to encode parameter.", e);
            }
        }

        String paramStr = paramStrBuilder.toString();

        String s;
        try {
            s = URLEncoder.encode(Signature.calculate(paramStr, sharedKey), "UTF-8");
        } catch (SignatureException e) {
            throw new RequestException("Failed signing of request", e);
        } catch (UnsupportedEncodingException e) {
            throw new RequestException("Failed to encode signature", e);
        }
        return paramStr.concat("&s=" + s);

    }


    /**
     * Send token activation request to server
     * The ClientId must be authorized to send this type of request.
     */
    public Response activate(String tokenId, String nonce, boolean activate) throws RequestException {
        if (sharedKey == null) throw new IllegalArgumentException("This type of request requires a shared key");

        String paramStr = getParamString(nonce, tokenId);

        List<String> serverUrls = new ArrayList<String>();
        for (String url : getUrls()) {
            if (activate) serverUrls.add(url.concat("/activate?").concat(paramStr));
            else serverUrls.add(url.concat("/deactivate?").concat(paramStr));
        }

        Response response = service.fetch(serverUrls, userAgent, new ResponseParserDefaultImpl());


        // Verify the signature
        StringBuilder keyValueStr = new StringBuilder();
        for (Map.Entry<String, String> entry : response.getKeyValueMap().entrySet()) {
            if ("s".equals(entry.getKey())) {
                continue;
            }
            if (keyValueStr.length() > 0) {
                keyValueStr.append("&");
            }
            keyValueStr.append(entry.getKey()).append("=").append(entry.getValue());
        }
        try {
            String signature = Signature.calculate(keyValueStr.toString(), sharedKey).trim();
            if (response.getSig() != null && !response.getSig().equals(signature) &&
                    !response.getResponseCode().equals(ResponseCode.BAD_SIGNATURE)) {
                // don't throw a RequestFailure if the server responds with bad signature
                throw new RequestException("Signatures miss-match");
            }
        } catch (SignatureException e) {
            throw new RequestException("Failed to calculate the response signature.", e);
        }


        // All fields are not returned on an error
        // If there is an error response, don't need to check them.
        if (!ResponseCode.isErrorCode(response.getResponseCode())) {
            // Verify the action
            if (response.getAction() == null ||
                    (activate && !"a".equals(response.getAction())) ||
                    (!activate && !"d".equals(response.getAction()))) {
                throw new RequestException("Action mismatch in response, is there a man-in-the-middle?");
            }


            // Verify the nonce
            if (response.getNonce() == null || !nonce.equals(response.getNonce())) {
                throw new RequestException("Nonce mismatch in response, is there a man-in-the-middle?");
            }

            // Verify the tokenId
            if (response.getTokenId() == null || !tokenId.equals(response.getTokenId())) {
                throw new RequestException("Token mismatch in response, is there a man-in-the-middle?");
            }

            // Verify server time, for additional security you can verify the UTC timestamp is reasonable
            if (response.getServerTimeStamp() == null) {
                throw new RequestException("Missing server timestamp");
            }

            if (response.getSig() == null) {
                throw new RequestException("Missing signature");
            }

        }


        return response;
    }


    public Response requestChallenge(String tokenId) throws RequestException {
        return requestChallenge(tokenId, getNonce());
    }





    /**
     * Send challenge request to server
     * The ClientId must be authorized to send this type of request.
     */
    public Response requestChallenge(String tokenId, String nonce) throws RequestException {
        if (sharedKey == null) throw new IllegalArgumentException("This type of request requires a shared key");
        String paramStr = getParamString(nonce, tokenId);

        List<String> serverUrls = new ArrayList<String>();
        for (String url : getUrls()) {
            serverUrls.add(url.concat("/requestChallengeImage?").concat(paramStr));
        }

        ChallengeRequestResponse response = (ChallengeRequestResponse)service.fetch(serverUrls, userAgent, new ResponseParserChallengeRequestImpl());

        // Verify the signature
        StringBuilder keyValueStr = new StringBuilder();
        for (Map.Entry<String, String> entry : response.getKeyValueMap().entrySet()) {
            if ("s".equals(entry.getKey())) {
                continue;
            } else if ("i".equals(entry.getKey())) {

                //MessageDigest digest = MessageDigest.getInstance("MD5")
                //String imageSignature = new BigInteger(1,digest.digest(image)).toString(16);

                continue;
            }
            if (keyValueStr.length() > 0) {
                keyValueStr.append("&");
            }
            keyValueStr.append(entry.getKey()).append("=").append(entry.getValue());
        }
        try {
            String signature = Signature.calculate(keyValueStr.toString(), sharedKey).trim();
            if (response.getSig() != null && !response.getSig().equals(signature) &&
                    !response.getResponseCode().equals(ResponseCode.BAD_SIGNATURE)) {
                // don't throw a RequestFailure if the server responds with bad signature
                throw new RequestException("Signatures miss-match");
            }


            String imageSignature = Signature.calculateImageSignature(response.getImage()).trim();
            String imageSignatureIn = response.getKeyValueMap().get("is");
            if (imageSignatureIn != null && !imageSignatureIn.equals(imageSignature)) {
                throw new RequestException("Image signature miss-match");
            }
        } catch (SignatureException e) {
            throw new RequestException("Failed to calculate the response signature.", e);
        }


        // All fields are not returned on an error
        // If there is an error response, don't need to check them.
        if (!ResponseCode.isErrorCode(response.getResponseCode())) {
            // Verify the action
            if (response.getAction() == null || (!"rc".equals(response.getAction()))) {
                throw new RequestException("Action mismatch in response, is there a man-in-the-middle?");
            }


            // Verify the nonce
            if (response.getNonce() == null || !nonce.equals(response.getNonce())) {
                throw new RequestException("Nonce mismatch in response, is there a man-in-the-middle?");
            }

            // Verify the tokenId
            if (response.getTokenId() == null || !tokenId.equals(response.getTokenId())) {
                throw new RequestException("Token mismatch in response, is there a man-in-the-middle?");
            }

            // Verify server time, for additional security you can verify the UTC timestamp is reasonable
            if (response.getServerTimeStamp() == null) {
                throw new RequestException("Missing server timestamp");
            }

            if (response.getSig() == null) {
                throw new RequestException("Missing signature");
            }

        }


        return response;
    }


    public String[] getUrls() {
        return urls;
    }

    public void setUrls(String[] urls) {
        this.urls = urls;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setSharedKey(String sharedKey) {
        this.sharedKey = Base64.decodeBase64(sharedKey.getBytes());
    }

    public String getSharedKey() {
        return new String(Base64.encodeBase64(this.sharedKey));
    }

    public Response validateOTP(String tokenId, String otp, String challengeId) throws RequestException {
        if (sharedKey == null) throw new IllegalArgumentException("This type of request requires a shared key");
        return validateOTP(tokenId, getNonce(), otp, challengeId);
    }

    public Response validateOTP(String tokenId, String otp) throws RequestException {
        if (sharedKey == null) throw new IllegalArgumentException("This type of request requires a shared key");
        return validateOTP(tokenId, getNonce(), otp, null);
    }


    /**
     * Validate OTP from most recent challenge
     *
     * @param tokenId     - Public token id/Reg code
     * @param nonce       - Nonce to check reply from server
     * @param otp         - One-time-password
     * @param challengeId - This id is returned on a challenge request and is optional in some cases.
     *                    The sever can be setup to require a challenge id on OTP validation requests as an added
     *                    precaution. In some cases we may not want to or are unable to provide challenge id.
     *                    For example if the image is displayed on the cloud/website and login is from an independent
     *                    linux terminal via a standard username/password field.
     * @return
     * @throws RequestException
     */
    public Response validateOTP(String tokenId, String nonce, String otp, String challengeId) throws RequestException {
        if (sharedKey == null) throw new IllegalArgumentException("This type of request requires a shared key");

        if(!isValidOTPFormat(otp)) {
            throw new IllegalArgumentException("Invalid OTP format");
        }


        String paramStr = getParamString(nonce, tokenId, otp, challengeId);

        List<String> serverUrls = new ArrayList<String>();
        for (String url : getUrls()) {
            serverUrls.add(url.concat("/validateOTP?").concat(paramStr));
        }

        Response response = service.fetch(serverUrls, userAgent, new ResponseParserDefaultImpl());

        // Verify the signature
        StringBuilder keyValueStr = new StringBuilder();
        for (Map.Entry<String, String> entry : response.getKeyValueMap().entrySet()) {
            if ("s".equals(entry.getKey())) {
                continue;
            }
            if (keyValueStr.length() > 0) {
                keyValueStr.append("&");
            }
            keyValueStr.append(entry.getKey()).append("=").append(entry.getValue());
        }
        try {
            String signature = Signature.calculate(keyValueStr.toString(), sharedKey).trim();
            if (response.getSig() != null && !response.getSig().equals(signature) &&
                    !response.getResponseCode().equals(ResponseCode.BAD_SIGNATURE)) {
                // don't throw a RequestFailure if the server responds with bad signature
                throw new RequestException("Signatures miss-match");
            }
        } catch (SignatureException e) {
            throw new RequestException("Failed to calculate the response signature.", e);
        }


        // All fields are not returned on an error
        // If there is an error response, don't need to check them.
        if (!ResponseCode.isErrorCode(response.getResponseCode())) {
            // Verify the action
            if (response.getAction() == null || !"v".equals(response.getAction())) {
                throw new RequestException("Action mismatch in response, is there a man-in-the-middle?");
            }


            // Verify the nonce
            if (response.getNonce() == null || !nonce.equals(response.getNonce())) {
                throw new RequestException("Nonce mismatch in response, is there a man-in-the-middle?");
            }

            // Verify the tokenId
            if (response.getTokenId() == null || !tokenId.equals(response.getTokenId())) {
                throw new RequestException("Token mismatch in response, is there a man-in-the-middle?");
            }

            // Verify server time, for additional security you can verify the UTC timestamp is reasonable
            if (response.getServerTimeStamp() == null) {
                throw new RequestException("Missing server timestamp");
            }

            if (response.getSig() == null) {
                throw new RequestException("Missing signature");
            }


        }


        return response;
    }

    public static boolean isValidOTPFormat(String otp) {
        if (otp == null){
            return false;
        }
        int len = otp.length();
        boolean isPrintable = true;
        for (char c : otp.toCharArray()) {
            if (c < 0x20 || c > 0x7E) {
                isPrintable = false;
                break;
            }
        }
        return isPrintable && (OTP_MIN_LEN <= len && len <= OTP_MAX_LEN);
    }





}
