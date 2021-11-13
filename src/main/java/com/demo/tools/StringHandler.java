package com.demo.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringHandler {
    public static long getIpNum(String ip){
        long ipNum=0;
        if(ip!=null&&!ip.equals("")){
            String[] subips=ip.split("\\.");
            for(int i=0;i<subips.length;i++){
                ipNum+=Integer.parseInt(subips[i]);
                if(i<subips.length-1)
                    ipNum=ipNum<<8;
            }
        }
        return ipNum;
    }

    public static String timeTostr(Date date){
        String strDate="";
        if(date!=null){
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            strDate=format.format(date);
        }
        return strDate;
    }
}
