package com.leon.aicenter.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextToNumber {
    private static final Map<Character, Integer> number = new HashMap<Character, Integer>() {{
        put('一', 1);
        put('二', 2);
        put('三', 3);
        put('四', 4);
        put('五', 5);
        put('六', 6);
        put('七', 7);
        put('八', 8);
        put('九', 9);
    }};

    private static final Map<Character, Integer> digit = new HashMap<Character, Integer>() {{
        put('十', 10);
        put('百', 100);
        put('千', 1000);
        put('万', 10000);
        put('亿', 100000000);
    }};

    private static List<Integer> temp;

    public static int toInteger(String str) {
        temp = new ArrayList<>();
        boolean ok = process(str);
        int res = 0;
        if (ok) {
            for (int i : temp) {
                res += i;
            }
        }
        return res;
    }

    public static int toNumber(String str) {
        int res = 0;
        for (int i = 0; i < str.length(); i++)
            if (digit.containsKey(str.charAt(i)))
                return 0;
            else if (number.containsKey(str.charAt(0))) {
                res *= 10;
                Integer j = number.get(str.charAt(0));
                if (j != null)
                    res += j;
            } else
                return 0;
        return res;
    }

    private static boolean process(String input) {
        if (input.equals(""))
            return true;
        else if (digit.containsKey(input.charAt(0))) {
            Integer i = digit.get(input.charAt(0));
            if (i != null) {
                if (temp.size() == 0 || temp.get(temp.size() - 1) >= i) {
                    if (input.charAt(0) == '十')
                        temp.add(i);
                    else
                        return false;
                } else {
                    int cur = 0;
                    while (temp.size() >= 1 && temp.get(temp.size() - 1) < i) {
                        cur += temp.get(temp.size() - 1);
                        temp.remove(temp.size() - 1);
                    }
                    temp.add(cur * i);
                }
            }
            return process(input.substring(1));
        } else if (number.containsKey(input.charAt(0))) {
            temp.add(number.get(input.charAt(0)));
            return process(input.substring(1));
        } else if (input.charAt(0) == '零') {
            return process(input.substring(1));
        } else {
            return false;
        }
    }
}
