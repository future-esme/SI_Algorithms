package dsa;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;

public class DSA {

    private static final BigInteger p = new BigInteger("50702342087986984684596540672785294493370824085308498450535565701730450879745310594069460940052367603038103747343106687981163754506284021184158903198888031001641800021787453760919626851704381009545624331468658731255109995186698602388616345118779571212089090418972317301933821327897539692633740906524461904910061687459642285855052275274576089050579224477511686171168825003847462222895619169935317974865296291598100558751976216418469984937110507061979400971905781410388336458908816885758419125375047408388601985300884500733923194700051030733653434466714943605845143519933901592158295809020513235827728686129856549511535000228593790299010401739984240789015389649972633253273119008010971111107028536093543116304613269438082468960788836139999390141570158208410234733780007345264440946888072018632119778442194822690635460883177965078378404035306423001560546174260935441728479454887884057082481520089810271912227350884752023760663");
    private static final BigInteger q = new BigInteger("63762351364972653564641699529205510489263266834182771617563631363277932854227");

    public static void main(String[] args) {

        HashMap<String, BigInteger> verificationAndSigningKeys = getVerificationAndSigningKeys();

        System.out.println("----------------------------");
        System.out.println("Signing and Verification Keys:");
        System.out.println("x: " + verificationAndSigningKeys.get("x"));
        System.out.println("y:" + verificationAndSigningKeys.get("y"));
        System.out.println("g:" + verificationAndSigningKeys.get("g"));
        System.out.println("p:" + p);
        System.out.println("q:" + q);

        BigInteger message = generateMessage();

        HashMap<String, BigInteger> signatures = getMessageSignature(verificationAndSigningKeys, message);
        BigInteger s = signatures.get("s");
        BigInteger r = signatures.get("r");

        System.out.println("----------------------------");
        System.out.println("Signing:");
        System.out.println("Message to be signed m : " + message);
        System.out.println("Signature sigma = (r, s) :");
        System.out.println("r  : " + r);
        System.out.println("s  : " + s);

        //create a verification key store since, signing key should not be send to verification module
        HashMap<String, BigInteger> verificationKeys = new HashMap<>();
        verificationKeys.put("y", verificationAndSigningKeys.get("y"));
        verificationKeys.put("g", verificationAndSigningKeys.get("g"));

        // Receive the data from signature verifier	 and send it to the verification
        HashMap<String, BigInteger> verifiedSignature = verifySignature(signatures, verificationKeys, message);

        System.out.println("----------------------------");
        System.out.println("Verification:");
        System.out.println("w: " + verifiedSignature.get("w"));
        System.out.println("u1: " + verifiedSignature.get("u1"));
        System.out.println("u2: " + verifiedSignature.get("u2"));
        System.out.println("v: " + verifiedSignature.get("v"));
        System.out.println("----------------------------");
        System.out.print("Result :");
        if (((verifiedSignature.get("result")).compareTo(BigInteger.ZERO)) == 0) {
            System.out.println(" Signature does not match");
        } else if (((verifiedSignature.get("result")).compareTo(BigInteger.ONE)) == 0) {
            System.out.println(" Signature matches");
        }

    }

    private static HashMap<String, BigInteger> getVerificationAndSigningKeys() {
        HashMap<String, BigInteger> vk = new HashMap<>();
        try {
            BigInteger g;
            BigInteger power = (p.subtract(BigInteger.ONE)).divide(q);

            g = BigInteger.TWO.modPow(power, p);
            if (g.compareTo(BigInteger.ONE) == 0) {
                System.out.println("h is 1, terminating the program.");
                System.exit(0);
            } else {
                BigInteger x = getSecretRandomNumberX();
                BigInteger y = g.modPow(x, p);

                vk.put("x", x);
                vk.put("y", y);
                vk.put("g", g);
            }
        } catch (Exception ex) {
            System.out.println("Exception occurred while getting verificaiton keys: " + ex);
        }

        return vk;
    }

    private static BigInteger getSecretRandomNumberX() {
        BigInteger randomNumber = BigInteger.ZERO;
        try {
            do {
                SecureRandom secureRandomNumber = new SecureRandom();
                randomNumber = new BigInteger(q.bitLength(), secureRandomNumber);
            } while ((randomNumber.compareTo(BigInteger.TWO) < 0) || (randomNumber.compareTo(q) > 0));

        } catch (Exception ex) {
            System.out.println("Exception occurred while getting randomNumber: " + ex);
        }
        return randomNumber;
    }

    private static BigInteger getSecretRandomNumberK() {
        BigInteger randomNumber = BigInteger.ZERO;
        try {
            BigInteger randomNumberLowerLimit = BigInteger.TWO;
            BigInteger randomNumberUpperLimit = q.subtract(BigInteger.ONE);

            do {
                SecureRandom secureRandomNumber = new SecureRandom();
                randomNumber = new BigInteger(q.bitLength(), secureRandomNumber);

            } while ((randomNumber.compareTo(randomNumberLowerLimit) < 0) || (randomNumber.compareTo(randomNumberUpperLimit) > 0));

        } catch (Exception ex) {
            System.out.println("Exception occurred while getting randomNumber: " + ex);
        }
        return randomNumber;
    }


    //This function will give signing key
    private static BigInteger generateMessage() {
        BigInteger randomMessage = BigInteger.ZERO;
        try {
            BigInteger randomNumberLowerLimit = BigInteger.ONE;
            BigInteger randomNumberUpperLimit = q.subtract(BigInteger.ONE);
            do {
                SecureRandom secureRandomNumber = new SecureRandom();
                randomMessage = new BigInteger(q.bitLength(), secureRandomNumber);

            } while ((randomMessage.compareTo(randomNumberLowerLimit) < 0) || (randomMessage.compareTo(randomNumberUpperLimit) > 0));

        } catch (Exception ex) {
            System.out.println("Exception occurred while getting randomNumber: " + ex);
        }

        return randomMessage;
    }


    //This function will give signing key
    private static HashMap<String, BigInteger> getMessageSignature(HashMap<String, BigInteger> verificationAndSigningKeys, BigInteger message) {
        HashMap<String, BigInteger> messageSignature = new HashMap<>();
        BigInteger k;
        BigInteger r;
        BigInteger i;
        BigInteger s;

        try {
            //get the SHA256(message)

            BigInteger digestSha256 = getmessagedigestSha256(message);
            BigInteger mSHA256ModQ = digestSha256.mod(q);

            //Get the verification Key
            BigInteger x = verificationAndSigningKeys.get("x");

            BigInteger g = verificationAndSigningKeys.get("g");

            do {
                k = getSecretRandomNumberK();

                r = (g.modPow(k, p)).mod(q);
                i = k.modInverse(q);

                BigInteger xr = x.multiply(r);
                BigInteger xrModQ = xr.mod(q);

                BigInteger sha256AdditionXr = mSHA256ModQ.add(xrModQ);
                s = (i.multiply(sha256AdditionXr)).mod(q);

            } while (s.compareTo(BigInteger.ZERO) == 0);

            messageSignature.put("r", r);
            messageSignature.put("s", s);

        } catch (Exception ex) {
            System.out.println("Exception occurred while getting randomNumber: " + ex);
        }

        return messageSignature;
    }

    private static BigInteger getmessagedigestSha256(BigInteger message) {
        BigInteger hashBigInteger = BigInteger.ZERO;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhHash = digest.digest(message.toString().getBytes(StandardCharsets.UTF_8));

            String encodedHash = Base64.getEncoder().encodeToString(encodedhHash);
            byte[] decodedHash = Base64.getDecoder().decode(encodedHash);

            String hash256Hex = String.format("%032x", new BigInteger(1, decodedHash));

            hashBigInteger = new BigInteger(hash256Hex, 16);
        } catch (Exception ex) {
            System.out.println("Exception occurred while getting Message Digest: " + ex);
        }
        return hashBigInteger;
    }


    //VERIFY SIGNATURE
    private static HashMap<String, BigInteger> verifySignature(HashMap<String, BigInteger> signature,
                                                               HashMap<String, BigInteger> verificationKeys,
                                                               BigInteger message) {
        HashMap<String, BigInteger> signatureVerification = new HashMap<>();
        try {

            BigInteger rSignture = signature.get("r");
            BigInteger sSignature = signature.get("s");
            BigInteger hPublicKey = verificationKeys.get("g");
            BigInteger yPublicKey = verificationKeys.get("y");

            BigInteger messageHash = getmessagedigestSha256(message);
            BigInteger w = sSignature.modInverse(q);

            BigInteger wModQ = w.mod(q);

            BigInteger hash256Modq = messageHash.mod(q);
            BigInteger u1 = (wModQ.multiply(hash256Modq)).mod(q);
            BigInteger rSignatureModQ = rSignture.mod(q);
            BigInteger u2 = (rSignatureModQ.multiply(wModQ)).mod(q);
            BigInteger multiplier1 = hPublicKey.modPow(u1, p);
            BigInteger multiplier2 = yPublicKey.modPow(u2, p);

            BigInteger result = (multiplier1.multiply(multiplier2)).mod(p);

            BigInteger v = result.mod(q);

            BigInteger signatureVerificationResult = BigInteger.ZERO;
            if ((v.toString() != null) && (rSignture.toString() != null)) {
                if (v.compareTo(rSignture) == 0) {
                    signatureVerificationResult = BigInteger.ONE;
                }
            } else {
                System.out.println("result v or r is null");
            }

            signatureVerification.put("w", w);
            signatureVerification.put("u1", u1);
            signatureVerification.put("u2", u2);
            signatureVerification.put("v", v);
            signatureVerification.put("result", signatureVerificationResult);


        } catch (Exception ex) {
            System.out.println("Exception occurred while verifying signature: " + ex);
        }
        return signatureVerification;
    }

}
