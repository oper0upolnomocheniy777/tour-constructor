package com.sfedu.touragency.util;

public class TextShrinker {
    public static String shrink(String text, int size) {
        if (text.length() > size) {
            return text.substring(0, size) + "...";
        }

        return text;
    }

}
