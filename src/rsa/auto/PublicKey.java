package rsa.auto;

import java.math.BigInteger;

public record PublicKey(BigInteger exponent, BigInteger modulus) {
}
