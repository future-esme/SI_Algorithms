package rsa.self;

import java.math.BigInteger;
import java.util.Scanner;

public class RSA {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String main = "abcdefghijklmnopqrstuvwxyz";

        int p;
        int q;
        do {
            System.out.print("Enter prime p: ");
            p = scanner.nextInt();
            System.out.print("Enter prime q (!=p): ");
            q = scanner.nextInt();
        } while (p == q);

        System.out.println("Prime number p: " + p);
        System.out.println("Prime number q: " + q);

        System.out.println("Generating Public/Private key-pairs!");
        KeyPair keyPair = generateKeyPair(p, q);
        System.out.println("Your public key is (e, n): (" + keyPair.publicKey().exponent() + ", " + keyPair.publicKey().modulus() + ")");
        System.out.println("Your private key is (d, n): (" + keyPair.privateKey().exponent() + ", " + keyPair.privateKey().modulus() + ")");

        System.out.print("Enter the message: ");
        scanner.nextLine();  // Consume newline character
        String message = scanner.nextLine();

        // Converting into lower case and removing spaces
        message = message.replace(" ", "");
        message = message.toLowerCase();
        int[] arr = new int[message.length()];
        int[] cipherText = new int[message.length()];

        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            if (main.indexOf(c) != -1) {
                arr[i] = main.indexOf(c);
            }
        }

        for (int i = 0; i < arr.length; i++) {
            cipherText[i] = encrypt(keyPair.publicKey(), arr[i]);
        }

        System.out.print("Encrypted message (Cipher Text): ");
        for (int i : cipherText) {
            System.out.print(i + " ");
        }
        System.out.println();

        int[] plain = new int[cipherText.length];
        for (int i = 0; i < cipherText.length; i++) {
            plain[i] = decrypt(keyPair.privateKey(), cipherText[i]);
        }

        StringBuilder plainText = new StringBuilder();
        for (int i : plain) {
            plainText.append(main.charAt(i));
        }

        System.out.println("Plain text array: ");
        for (int i : plain) {
            System.out.print(i + " ");
        }
        System.out.println();
        System.out.println("Decrypted message (Plain Text): " + plainText);
    }

    public static KeyPair generateKeyPair(int p, int q) {
        int n = p * q;
        System.out.println("Value of n: " + n);

        // Phi is the Euler's totient of n
        int phi = (p - 1) * (q - 1);
        System.out.println("Value of phi(n): " + phi);

        // Choose an integer e such that e and phi(n) are co-prime
        Scanner scanner = new Scanner(System.in);
        int e;
        do {
            System.out.print("Enter e such that is co-prime to " + phi + ": ");
            e = scanner.nextInt();
        } while (gcd(e, phi) != 1);

        System.out.println("Value of exponent(e) entered is: " + e);

        // To generate the private key
        int d = multiplicativeInverse(e, phi);

        return new KeyPair(new PublicKey(e, n), new PrivateKey(d, n));
    }

    public static int multiplicativeInverse(int a, int m) {
        a = a % m;
        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1) {
                return x;
            }
        }
        return 1;
    }

    public static int gcd(int a, int b) {
        if (b == 0) {
            return a;
        } else {
            return gcd(b, a % b);
        }
    }

    public static int encrypt(PublicKey publicKey, int toEncrypt) {
        int key = publicKey.exponent();
        int n = publicKey.modulus();
        BigInteger result = BigInteger.valueOf(toEncrypt).modPow(BigInteger.valueOf(key), BigInteger.valueOf(n));
        return result.intValue();
    }
    public static int decrypt(PrivateKey privateKey, int toDecrypt) {
        int key = privateKey.exponent();
        int n = privateKey.modulus();
        BigInteger result = BigInteger.valueOf(toDecrypt).modPow(BigInteger.valueOf(key), BigInteger.valueOf(n));
        return result.intValue();
    }
}