package com.alias.utils;

import java.security.SecureRandom;

public class UsernameGenerator {

    private static final String CHAR_POOL = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int DEFAULT_LENGTH = 10;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generate() {
        return generate(DEFAULT_LENGTH);
    }

    public static String generate(int length) {
        StringBuilder sb = new StringBuilder("user_");
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(CHAR_POOL.length());
            sb.append(CHAR_POOL.charAt(index));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(generate());         // 示例输出: user_5fTq3KsZwL
        System.out.println(generate(6));        // 示例输出: user_f8GxPw
    }
}
