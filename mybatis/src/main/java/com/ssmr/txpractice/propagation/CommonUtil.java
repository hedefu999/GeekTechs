package com.ssmr.txpractice.propagation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommonUtil {
    public static String getTimeStr(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return formatter.format(now);
    }

}
