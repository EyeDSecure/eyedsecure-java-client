package com.eyedsecure.client;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class EyeDSecureClientTest {

    private EyeDSecureClient client = null;

    /*
     * API key for signing/verifying requests.
     *
     */
    private final String clientId = "0000000000000001";
    private final String sharedKey = "0123456789";
    private final String testTokenId = "T00000001TEST0";

    @Before
    public void setup() {
        client = new EyeDSecureClient(clientId, sharedKey);
    }

    @Test
    public void testMissingParam() throws RequestException {
        client.setClientId(null);
        Response response = client.activate(testTokenId, true);
        assertEquals(ResponseCode.MISSING_PARAMETER, response.getResponseCode());
    }

    @Test
    public void testShortClientId() throws RequestException {
        client.setClientId("xxx");
        Response response = client.activate(testTokenId, true);
        assertEquals(ResponseCode.MISSING_PARAMETER, response.getResponseCode());
    }


    @Test
    public void testNoSuchClientId() throws RequestException {
        client.setClientId("000000000000000X");
        Response response = client.activate(testTokenId, true);
        assertEquals(ResponseCode.NO_SUCH_CLIENT_ID, response.getResponseCode());
    }


    @Test
    public void testOperationNotAllowed() throws RequestException {
        client.setClientId("0000000000000002");     // User not allowed any functions
        Response response = client.activate(testTokenId, true);
        assertEquals(ResponseCode.OPERATION_NOT_ALLOWED, response.getResponseCode());
    }



    @Test
    public void testActivate() throws RequestException {
        Response response = client.activate(testTokenId, true);
        assertNotNull(response);
        assertEquals(response.getTokenId(), testTokenId);
        assertEquals(response.getAction(), "a");
        assertEquals(ResponseCode.SUCCESS, response.getResponseCode());
    }

    @Test
    public void testDeactivate() throws RequestException {
        Response response = client.activate(testTokenId, false);
        assertNotNull(response);
        assertEquals(response.getTokenId(), testTokenId);
        assertEquals(response.getAction(), "d");
        assertEquals(ResponseCode.SUCCESS, response.getResponseCode());
    }


    @Test
    public void testBadSignature() throws RequestException {
        client.setSharedKey("badapikey");
        Response response = client.activate(testTokenId, true);
        assertEquals(ResponseCode.BAD_SIGNATURE, response.getResponseCode());
    }


    @Test
    public void testReplay() throws RequestException {
        Response response1 = client.activate(testTokenId, "00000000000000000000000000000000", true);
        assertNotNull(response1);
        Response response2 = client.activate(testTokenId, "00000000000000000000000000000000", true);
        assertNotNull(response2);
        assertEquals(ResponseCode.REPLAYED_REQUEST, response2.getResponseCode());
    }

    @Test
    public void testMalformedNonce() throws RequestException {
        // Send large nonce
        Response response1 = client.activate(testTokenId, "03434340000000000000000000000000000000343242", true);
        assertNotNull(response1);
        assertEquals(ResponseCode.MISSING_PARAMETER, response1.getResponseCode());

        // Send large nonce
        Response response2 = client.activate(testTokenId, "0343", true);
        assertNotNull(response2);
        assertEquals(ResponseCode.MISSING_PARAMETER, response2.getResponseCode());
    }

    @Test
    public void testMalformedTokenId() throws RequestException {
        // Send large nonce
        Response response1 = client.activate("dddjh", true);
        assertNotNull(response1);
        assertEquals(ResponseCode.MISSING_PARAMETER, response1.getResponseCode());

        // Send large nonce
        Response response2 = client.activate("sdfjhfjdhfhdfkhdfhdhdfhdfjdhfjdhfjhdjhf", true);
        assertNotNull(response2);
        assertEquals(ResponseCode.MISSING_PARAMETER, response2.getResponseCode());
    }


    // todo: add additional test

}
