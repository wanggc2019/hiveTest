package com.wgc.utils;

import java.util.Random;

public class testRandom {
    public static void main(String[] args) {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        long result;
        result=Math.round(Math.random() * 25 + 65);
        sb.append((char) result);
        System.out.println(sb);
    }
}
