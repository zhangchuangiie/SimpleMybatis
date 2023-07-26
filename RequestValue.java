package com.example.demo.commom;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

public class RequestValue {

    HttpServletRequest request;
    public RequestValue(HttpServletRequest request) {
        this.request=request;
    }

    /*
     * 判断是否为整数
     * @param str 传入的字符串
     * @return 是整数返回true,否则返回false
     */
    public boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }


    public String string(String s){
        if(this.request.getParameter(s)==null) return null;
        return this.request.getParameter(s);
    }
    public Integer integer(String s){
        if(this.request.getParameter(s)==null) return null;
        return Integer.valueOf(this.request.getParameter(s));
    }
    public Long aLong(String s){
        if(this.request.getParameter(s)==null) return null;
        return Long.valueOf(this.request.getParameter(s));
    }
    public String s(String s){
        if(this.request.getParameter(s)==null) return null;
        return this.request.getParameter(s);
    }
    public Integer i(String s){
        if(this.request.getParameter(s)==null) return null;
        return Integer.valueOf(this.request.getParameter(s));
    }
    public Long l(String s){
        if(this.request.getParameter(s)==null) return null;
        return Long.valueOf(this.request.getParameter(s));
    }


    public Object object(String s){
        Object o = this.request.getParameter(s);
        if(o==null) return null;
        if(isInteger(o.toString())){
            o=Integer.valueOf((String) o);
        }
        return o;
    }

    public Object o(String s){
        return object(s);
    }
}
