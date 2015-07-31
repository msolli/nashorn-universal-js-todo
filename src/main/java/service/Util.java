package service;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class Util {
    public static String toJson(Object o) {
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(o);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String capitalize(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        return new StringBuilder(strLen)
                .append(Character.toTitleCase(str.charAt(0)))
                .append(str.substring(1))
                .toString();
    }
}
