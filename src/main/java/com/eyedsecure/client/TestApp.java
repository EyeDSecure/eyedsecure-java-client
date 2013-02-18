package com.eyedsecure.client;

/**
 * Created with IntelliJ IDEA.
 * User: boksman
 * Date: 2/17/13
 * Time: 11:23 PM
 */
public class TestApp {

    private static final int PUBLIC_ID_MIN_LEN = 14;
    private static final int PUBLIC_ID_MAX_LEN = 14;

    private static final int CHALLENGE_ID_MIN_LEN = 32;
    private static final int CHALLENGE_ID_MAX_LEN = 32;

    public static void main(String args[]) {


        String publicId = null;
        String challengeId = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-id") || args[i].equals("--public-id")) {
                publicId = args[i + 1];
            } else if (args[i].equals("-cid") || args[i].equals("--challenge-id")) {
                challengeId = args[i + 1];
            }
        }

        String cmd = args[args.length - 1];

        if (cmd.equals("rc") || cmd.equals("request-challenge")) {
            if (publicId == null || publicId.length() > PUBLIC_ID_MAX_LEN || publicId.length() < PUBLIC_ID_MIN_LEN) {
                System.err.println("Error: Invalid or missing public-id");
                printUsage();
                return;
            }
            requestChallenge();
        } else if (cmd.equals("val") || cmd.equals("validate")) {
            if (publicId == null || publicId.length() > PUBLIC_ID_MAX_LEN || publicId.length() < PUBLIC_ID_MIN_LEN) {
                System.err.println("Error: Invalid or missing public-id");
                printUsage();
                return;
            }

            if (challengeId == null || challengeId.length() > CHALLENGE_ID_MAX_LEN || challengeId.length() < CHALLENGE_ID_MIN_LEN) {
                System.err.println("Error: Invalid or missing challenge-id");
                printUsage();
                return;
            }
            validateOTP();

        } else {
            System.err.println("Error: Missing or invalid command");
            printUsage();
        }

        System.exit(0);
    }

    private static void requestChallenge() {
        // todo:
    }

    private static void validateOTP() {
        // todo:
    }


    private static void printUsage() {
        System.err.println("\nTest Eye-D Secure client functions against Eye-D Secure server");
        System.err.println("\nUsage: java -jar client.jar [options] command");
        System.err.println("\n       java -jar client.jar [options] request-challenge");
        System.err.println("\n       java -jar client.jar [options] validate");

        System.err.println("\nEg. java -jar client.jar -id XXXX-YYYY-ZZZZZ request-challenge");
        System.err.println("\n    java -jar client.jar -id XXXX-YYYY-ZZZZZ -cid CHALLENGE-ID -otp OTP validate");

        System.err.println("\n\nCommands:");
        System.err.println("request-challenge - Request a challenge image from the server");
        System.err.println("validate - Validate a one-time (OTP) password given challenge id");

        System.err.println("\n\nOptions:");
        System.err.println("-id, --public-id - Eye-D Card public id");
        System.err.println("-cid, --challenge-id - Challenge id to be accompanied by OTP validation request");

        System.err.println("\n\nPlace your Eye-D Card over the challenge image to determine OTP");

    }
}
