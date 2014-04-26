package com.cisco.dvbu.ps.deploytool.gui.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyTest {
    private static Pattern p;
    private static Map<String, String> vars;
    
    static {
        p = Pattern.compile ("\\$[^\\$]+\\$|\\$\\w+|%[^%]+%|%\\w+");
        vars = new HashMap<String, String>();
        vars.put ("variable1", "lacement");
        vars.put ("variable2", "rep$variable1");
    }

    public MyTest () {
        super ();
    }
    
    public String myReplace (String inStr) {
        StringBuffer sb = new StringBuffer();
        if (inStr == null) return "";

        Matcher m = p.matcher (inStr);
        
        while (m.find()) {
            m.appendReplacement (sb, myReplace(vars.get(m.group().replaceFirst("^\\$([^$]+)\\$$", "$1").replaceFirst("^\\$", "").replaceFirst("^%([^%]+)%$", "$1").replaceFirst("^%", ""))));
        }
        m.appendTail (sb);
        
        return sb.toString();
    }

    public static void main (String[] args) {
        MyTest mt = new MyTest();
        String result = mt.myReplace ("This is a $variable2 test.");
        
        System.out.println ("result = " + result);
    }
}
