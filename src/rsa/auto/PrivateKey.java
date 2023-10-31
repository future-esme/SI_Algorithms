package rsa.auto;

import java.math.BigInteger;

public record PrivateKey(BigInteger exponent, BigInteger modulus) {
}
