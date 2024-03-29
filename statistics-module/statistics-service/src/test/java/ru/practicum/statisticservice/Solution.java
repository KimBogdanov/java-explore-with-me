package ru.practicum.statisticservice;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class Solution {
    public static void main(String[] args) {
        LocalDateTime time = LocalDateTime.now();
        System.out.println("Locale Date Time " + time);
        String encode = URLEncoder.encode(time.toString(), StandardCharsets.UTF_8);
        System.out.println("Encode time " + encode);
        System.out.println("Decode time " + URLDecoder.decode(encode));

        System.out.println("Our realisation");
        String strTime = "2020-05-05 00:00:00";
        System.out.println("String timme: " + strTime);
        String encode1 = URLEncoder.encode(strTime);
        System.out.println("Encode string time " + encode1);
        System.out.println("Decode string time " + URLDecoder.decode(encode1));
    }
}
