package com.eyedsecure.client;

/**
 * Response associated with a challenge request
 */
public class ChallengeRequestResponse extends Response {

    private String challengeId;                 // Challenge id returned on requestChallenge responses

    private byte[] image;                       // optional image as a byte array for image requests


    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }


    public String getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(String challengeId) {
        this.challengeId = challengeId;
    }
}
