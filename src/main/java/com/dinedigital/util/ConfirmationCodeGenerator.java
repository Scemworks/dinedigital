package com.dinedigital.util;

import java.security.SecureRandom;

public final class ConfirmationCodeGenerator {
    private static final String ALPHANUM = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // avoid confusing chars
    private static final SecureRandom RNG = new SecureRandom();

    private ConfirmationCodeGenerator() {}

    public static String generate(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int idx = RNG.nextInt(ALPHANUM.length());
            sb.append(ALPHANUM.charAt(idx));
        }
        return sb.toString();
    }
}
