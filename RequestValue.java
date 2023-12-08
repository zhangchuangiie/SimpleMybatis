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


    public String getString(String s){
        if(this.request.getParameter(s)==null) return null;
        return this.request.getParameter(s);
    }
    public Integer getInteger(String s){
        if(this.request.getParameter(s)==null) return null;
        return Integer.valueOf(this.request.getParameter(s));
    }
    public Long getLong(String s){
        if(this.request.getParameter(s)==null) return null;
        return Long.valueOf(this.request.getParameter(s));
    }

    public Short getShort(String s){
        if(this.request.getParameter(s)==null) return null;
        return Short.valueOf(this.request.getParameter(s));
    }

    public Float getFloat(String s){
        if(this.request.getParameter(s)==null) return null;
        return Float.valueOf(this.request.getParameter(s));
    }

    public Double getDouble(String s){
        if(this.request.getParameter(s)==null) return null;
        return Double.valueOf(this.request.getParameter(s));
    }

    public Object getObject(String s){
        Object o = this.request.getParameter(s);
        if(o==null) return null;
        if(isInteger(o.toString())){
            o=Integer.valueOf((String) o);
        }
        return o;
    }

}
