package com.jee.publicapi.util;

public class DeviceDetector {

    public static String getBrowser(String userAgent) {

    	 if (userAgent == null) return "Unknown";

    	    // Edge first, because its UA contains "Chrome"
    	    if (userAgent.contains("Edg")) return "Edge";
    	    if (userAgent.contains("OPR") || userAgent.contains("Opera")) return "Opera";
    	    if (userAgent.contains("Chrome")) return "Chrome";
    	    if (userAgent.contains("Firefox")) return "Firefox";
    	    if (userAgent.contains("Safari") && !userAgent.contains("Chrome")) return "Safari";

    	    return "Other";
    }

    public static String getOS(String userAgent) {

        if (userAgent == null) return "Unknown";

        if (userAgent.contains("Windows")) return "Windows";
        if (userAgent.contains("Mac")) return "MacOS";
        if (userAgent.contains("Linux")) return "Linux";
        if (userAgent.contains("Android")) return "Android";
        if (userAgent.contains("iPhone") || userAgent.contains("iPad")) return "iOS";

        return "Unknown";
    }

    public static String getDevice(String userAgent) {

        if (userAgent == null) return "Unknown";

        if (userAgent.contains("Mobile") || userAgent.contains("Android") || userAgent.contains("iPhone"))
            return "Mobile";

        if (userAgent.contains("Tablet") || userAgent.contains("iPad"))
            return "Tablet";

        return "Desktop";
    }
}