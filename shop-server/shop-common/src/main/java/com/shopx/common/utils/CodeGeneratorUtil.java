package com.shopx.common.utils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CodeGeneratorUtil {

    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyMMdd");
    private static final DateTimeFormatter TIMESTAMP_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    public static String generateVerifyCode(String prefix) {
        String datePart = LocalDateTime.now().format(DATE_FMT);
        String randomPart = generateRandom(CHARS, 8);
        return prefix + datePart + randomPart;
    }

    public static String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FMT);
        String randomPart = generateRandom("0123456789", 4);
        return "ORD" + timestamp + randomPart;
    }

    public static String generateBatchNo() {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FMT);
        String randomPart = generateRandom(CHARS, 4);
        return "BATCH" + timestamp + randomPart;
    }

    private static String generateRandom(String source, int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(source.charAt(SECURE_RANDOM.nextInt(source.length())));
        }
        return sb.toString();
    }
}
