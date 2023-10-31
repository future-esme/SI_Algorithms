package des;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Des {
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String BLUE = "\u001B[34m";
    private static final int[][][] sBox = {
            {{14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7},
            {0,15,7,4,14,2,13,1,10,6,12,11,9,5,3,8},
            {4,1,14,8,13,6,2,11,15,12,9,7,3,10,5,0},
            {15,12,8,2,4,9,1,7,5,11,3,14,10,0,6,13}},
            {{15,1,8,14,6,11,3,4,9,7,2,13,12,0,5,10},
            {3,13,4,7,15,2,8,14,12,0,1,10,6,9,11,5},
            {0,14,7,11,10,4,13,1,5,8,12,6,9,3,2,15},
            {13,8,10,1,3,15,4,2,11,6,7,12,0,5,14,9}},
            {{10,0,9,14,6,3,15,5,1,13,12,7,11,4,2,8},
            {13,7,0,9,3,4,6,10,2,8,5,14,12,11,15,1},
            {13,6,4,9,8,15,3,0,11,1,2,12,5,10,14,7},
            {1,10,13,0,6,9,8,7,4,15,14,3,11,5,2,12}},
            {{7,13,14,3,0,6,9,10,1,2,8,5,11,12,4,15},
            {13,8,11,5,6,15,0,3,4,7,2,12,1,10,14,9},
            {10,6,9,0,12,11,7,13,15,1,3,14,5,2,8,4},
            {3,15,0,6,10,1,13,8,9,4,5,11,12,7,2,14}},
            {{2,12,4,1,7,10,11,6,8,5,3,15,13,0,14,9},
            {14,11,2,12,4,7,13,1,5,0,15,10,3,9,8,6},
            {4,2,1,11,10,13,7,8,15,9,12,5,6,3,0,14},
            {11,8,12,7,1,14,2,13,6,15,0,9,10,4,5,3}},
            {{12,1,10,15,9,2,6,8,0,13,3,4,14,7,5,11},
            {10,15,4,2,7,12,9,5,6,1,13,14,0,11,3,8},
            {9,14,15,5,2,8,12,3,7,0,4,10,1,13,11,6},
            {4,3,2,12,9,5,15,10,11,14,1,7,6,0,8,13}},
            {{4,11,2,14,15,0,8,13,3,12,9,7,5,10,6,1},
            {13,0,11,7,4,9,1,10,14,3,5,12,2,15,8,6},
            {1,4,11,13,12,3,7,14,10,15,6,8,0,5,9,2},
            {6,11,13,8,1,4,10,7,9,5,0,15,14,2,3,12}},
            {{13,2,8,4,6,15,11,1,10,9,3,14,5,0,12,7},
            {1,15,13,8,10,3,7,4,12,5,6,11,0,14,9,2},
            {7,11,4,1,9,12,14,2,0,6,10,13,15,3,5,8},
            {2,1,14,7,4,10,8,13,15,12,9,0,3,5,6,11}}
            };

    private static final int[] shiftTable =
            {1, 1, 2, 2,
            2, 2, 2, 2,
            1, 2, 2, 2,
            2, 2, 2, 1};

    private static final int[] initialPerm =
            {58, 50, 42, 34, 26, 18, 10, 2,
            60, 52, 44, 36, 28, 20, 12, 4,
            62, 54, 46, 38, 30, 22, 14, 6,
            64, 56, 48, 40, 32, 24, 16, 8,
            57, 49, 41, 33, 25, 17, 9, 1,
            59, 51, 43, 35, 27, 19, 11, 3,
            61, 53, 45, 37, 29, 21, 13, 5,
            63, 55, 47, 39, 31, 23, 15, 7};

    private static final int[] permCho1 =
            {57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18,
            10, 2, 59, 51, 43, 35, 27, 19, 11, 3, 60, 52, 44, 36,
            63, 55, 47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 30, 22,
            14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 28, 20, 12, 4};

    private static final int[] permCho2 =
            {14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 21, 10,
            23, 19, 12, 4, 26, 8, 16, 7, 27, 20, 13, 2,
            41, 52, 31, 37, 47, 55, 30, 40, 51, 45, 33, 48,
            44, 49, 39, 56, 34, 53, 46, 42, 50, 36, 29, 32};


    private static final int[] expanPerm =
            {32, 1, 2, 3, 4, 5,
            4, 5, 6, 7, 8, 9,
            8, 9, 10, 11, 12, 13,
            12, 13, 14, 15, 16, 17,
            16, 17, 18, 19, 20, 21,
            20, 21, 22, 23, 24, 25,
            24, 25, 26, 27, 28, 29,
            28, 29, 30, 31, 32, 1};

    private static final int[] permTable =
            {16, 7, 20, 21, 29, 12, 28, 17,
            1, 15, 23, 26, 5, 18, 31, 10,
            2, 8, 24, 14, 32, 27, 3, 9,
            19, 13, 30, 6, 22, 11, 4, 25};

    private static final int[] finalPerm =
            {40, 8, 48, 16, 56, 24, 64, 32,
            39, 7, 47, 15, 55, 23, 63, 31,
            38, 6, 46, 14, 54, 22, 62, 30,
            37, 5, 45, 13, 53, 21, 61, 29,
            36, 4, 44, 12, 52, 20, 60, 28,
            35, 3, 43, 11, 51, 19, 59, 27,
            34, 2, 42, 10, 50, 18, 58, 26,
            33, 1, 41, 9, 49, 17, 57, 25};

    private static final Map<Character, String> binMap = new HashMap<>();
    private static final Map<String, Character> hexMap = new HashMap<>();

    public static void initMaps() {
        binMap.put('0', "0000");
        binMap.put('1', "0001");
        binMap.put('2', "0010");
        binMap.put('3', "0011");
        binMap.put('4', "0100");
        binMap.put('5', "0101");
        binMap.put('6', "0110");
        binMap.put('7', "0111");
        binMap.put('8', "1000");
        binMap.put('9', "1001");
        binMap.put('A', "1010");
        binMap.put('B', "1011");
        binMap.put('C', "1100");
        binMap.put('D', "1101");
        binMap.put('E', "1110");
        binMap.put('F', "1111");
        hexMap.put("0000", '0');
        hexMap.put("0001", '1');
        hexMap.put("0010", '2');
        hexMap.put("0011", '3');
        hexMap.put("0100", '4');
        hexMap.put("0101", '5');
        hexMap.put("0110", '6');
        hexMap.put("0111", '7');
        hexMap.put("1000", '8');
        hexMap.put("1001", '9');
        hexMap.put("1010", 'A');
        hexMap.put("1011", 'B');
        hexMap.put("1100", 'C');
        hexMap.put("1101", 'D');
        hexMap.put("1110", 'E');
        hexMap.put("1111", 'F');
    }

    private static String xor(String a, String b) {
        StringBuilder answer = new StringBuilder();
        for (var i = 0; i < a.length(); i++) {
            if (a.charAt(i) == b.charAt(i)) {
                answer.append("0");
            } else {
                answer.append("1");
            }
        }
        return answer.toString();
    }

    private static String textToHex(String arg) {
        return String.format("%040x", new BigInteger(1, arg.getBytes(StandardCharsets.UTF_8)));
    }

    private static String hexToText(String arg) {
        var bytes = HexFormat.of().parseHex(arg);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static String hexToBin(String message) {
        StringBuilder answer = new StringBuilder();
        for (var i = 0; i < message.length(); i++) {
            answer.append(binMap.get(message.charAt(i)));
        }
        return answer.toString();
    }

    private static String binToHex(String message) {
        StringBuilder answer = new StringBuilder();
        for (var i = 0; i < message.length(); i += 4) {
            StringBuilder ch = new StringBuilder();
            ch
                    .append(message.charAt(i))
                    .append(message.charAt(i + 1))
                    .append(message.charAt(i + 2))
                    .append(message.charAt(i + 3));
            answer.append(hexMap.get(ch.toString()));
        }
        return answer.toString();
    }

    private static Integer binToDec(String message) {
        return Integer.parseInt(message, 2);
    }

    private static String decToBin(Integer number) {
        return String.format("%4s", Integer.toBinaryString(number)).replace(' ', '0');
    }

    private static String shiftLeft(String key, Integer nthShifts) {
        StringBuilder s = new StringBuilder();
        for (var i = 0; i < nthShifts; i++) {
            for (var j = 1; j < key.length(); j++) {
                s.append(key.charAt(j));
            }
            s.append(key.charAt(0));
            key = s.toString();
            s = new StringBuilder();
        }
        return key;
    }

    private static String initialPermutation(String plainText, int[] initialPermutation, Integer noBits) {
        StringBuilder permutation = new StringBuilder();
        for (var i = 0; i < noBits; i++) {
            permutation.append(plainText.charAt(initialPermutation[i] - 1));
        }
        return permutation.toString();
    }

    private static String encrypt(String message, String key, boolean decrypt) {
        printInfo("Encryption");
        message = hexToBin(message);
        message = initialPermutation(message, initialPerm, 64);
        printInfo("Message after initial permutation: ", binToHex(message));

        key = hexToBin(key);
        key = initialPermutation(key, permCho1, 56);
        printInfo("The converted 56bit key is: ", key);

        var leftKey = key.substring(0, 28);
        var rightKey = key.substring(28);

        var leftMessage = message.substring(0, 32);
        var rightMessage = message.substring(32);

        var keyFromPc2Bin = new ArrayList<String>();
        var keyFromPc2Hex = new ArrayList<String>();

        for (var k = 0; k < 16; k++) {
            leftKey = shiftLeft(leftKey, shiftTable[k]);
            rightKey = shiftLeft(rightKey, shiftTable[k]);
            var combinedKey = leftKey + rightKey;

            var roundKey = initialPermutation(combinedKey, permCho2, 48);
            keyFromPc2Bin.add(roundKey);
            keyFromPc2Hex.add(binToHex(roundKey));
        }
        if (decrypt) {
            Collections.reverse(keyFromPc2Bin);
            Collections.reverse(keyFromPc2Hex);
        }
        printInfo();
        //printInfo("Round: Left key part: Right key part: SubKey used:");

        for (var j = 0; j < 16; j ++) {
            var rightExpand = initialPermutation(rightMessage, expanPerm, 48);
            var xorX = xor(rightExpand, String.valueOf(keyFromPc2Bin.get(j)));

            var sBoxStrBuilder = new StringBuilder();
            for (var i = 0; i < 8; i++) {
                var row = binToDec(xorX.charAt(i * 6) + String.valueOf(xorX.charAt(i * 6 + 5)));
                var col = binToDec(xorX.charAt(i * 6 + 1) + String.valueOf(xorX.charAt(i * 6 + 2)) + xorX.charAt(i * 6 + 3) + xorX.charAt(i * 6 + 4));
                var val = sBox[i][row][col];
                sBoxStrBuilder.append(decToBin(val));
            }
            var sBoxStr = sBoxStrBuilder.toString();
            sBoxStr = initialPermutation(sBoxStr, permTable, 32);

            leftMessage = xor(leftMessage, sBoxStr);

            if (j != 15) {
                leftMessage = leftMessage + rightMessage;
                rightMessage = leftMessage.substring(0, (leftMessage.length() - rightMessage.length()));
                leftMessage = leftMessage.substring(rightMessage.length());
            }

            //printInfo(String.format("%02d", j + 1), "     ", binToHex(leftMessage), "     ", binToHex(rightMessage), "     ", String.valueOf(keyFromPc2Hex.get(j)));
        }

        var combinedMessage = leftMessage + rightMessage;
        return initialPermutation(combinedMessage, finalPerm, 64);
    }

    private static String padding(String message) {
        if (message.length() % 16 != 0) {
            printInfo("Padding required");
            StringBuilder messageBuilder = new StringBuilder(message);
            messageBuilder.append("0".repeat((16 - (messageBuilder.length() % 16))));
            message = messageBuilder.toString();
        } else {
            printInfo("No padding required");
        }
        return message;
    }

    private static void printInfo(String... varargs) {
        var message = new StringBuilder(BLUE);
        for (var arg : varargs) {
            message.append(arg);
        }
        message.append(RESET);
        System.out.println(message);
    }

    private static void printError(String... varargs) {
        var message = new StringBuilder(RED);
        for (var arg : varargs) {
            message.append(arg);
        }
        message.append(RESET);
        System.out.println(message);
    }

    private static String readLine() {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            return bufferedReader.readLine();
        } catch (Exception e) {
            printError("Entered text is not valid");
        }
        return "";
    }

    public static void main(String... varargs) {
        printInfo("Do you want to encrypt or decrypt a message? (type e/d)");
        var encryptDecryptChoice = readLine();
        var isDecrypt = encryptDecryptChoice.equals("d");
        printInfo("Enter the message to be %s: ".formatted((isDecrypt) ? "decrypted": "encrypted"));
        var plainText = readLine();
        if (plainText.length() == 0) {
            return;
        }
        //var textInHex = textToHex(plainText);
        plainText = padding(plainText);
        printInfo("Message after padding: ", plainText);

        printInfo("Enter the 64bit key for %s: ".formatted((isDecrypt) ? "decryption" : "encryption"));
        var key = readLine();
        if (key.length() == 0) {
            return;
        }
        //var keyHex = textToHex(key);
        key = padding(key);
        printInfo("Key after padding: ", key);
        initMaps();
        var cipherText = binToHex(encrypt(plainText, key, isDecrypt));
        printInfo("Cipher text is: ", cipherText);
    }
}