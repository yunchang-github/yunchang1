package com.weiziplus.springboot.utils;

public class AreaCastUtil {
    public static String castToSalesChannel(String area){
        if (area == null){
            return null;
        }
        if ("US".equals(area)){
            return "Amazon.com";
        }
        if ("CA".equals(area)){
            return "Amazon.ca";
        }
        if ("MX".equals(area)){
            return "Amazon.com.mx";
        }
        if ("UK".equals(area)){
            return "Amazon.co.uk";
        }
        if ("GB".equals(area)){
            return "Amazon.co.uk";
        }
        if ("DE".equals(area)){
            return "Amazon.de";
        }
        if ("ES".equals(area)){
            return "Amazon.es";
        }
        if ("FR".equals(area)){
            return "Amazon.fr";
        }
        if ("IT".equals(area)){
            return "Amazon.it";
        }
        if ("JP".equals(area)){
            return "Amazon.co.jp";
        }
        if ("AU".equals(area)){
            return "Amazon.com.au";
        }
        return null;
    }

    public static int castToDomainId(String area){
        if (area == null){
            return 0;
        }
        if ("US".equals(area)){
            return 1;
        }
        if ("CA".equals(area)){
            return 6;
        }
        if ("MX".equals(area)){
            return 0;
        }
        if ("UK".equals(area)){
            return 2;
        }
        if ("GB".equals(area)){
            return 2;
        }
        if ("DE".equals(area)){
            return 3;
        }
        if ("ES".equals(area)){
            return 9;
        }
        if ("FR".equals(area)){
            return 4;
        }
        if ("IT".equals(area)){
            return 8;
        }
        if ("JP".equals(area)){
            return 5;
        }
        if ("AU".equals(area)){
            return 0;
        }
        return 0;
    }
}
