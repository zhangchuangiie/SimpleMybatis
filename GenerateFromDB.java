package com.example.demo.util;

import io.swagger.annotations.ApiImplicitParam;

import java.util.LinkedHashMap;
import java.util.List;

public class GenerateFromDB {

    ///这里的字段描述查询语句需要修改
    List<LinkedHashMap<String, Object>> result =  JDBCUtil.select("desc user");

    private String getInterPara() {



        System.out.println("原始数据库字段信息:"+result+"\n");

        String reString = "";

        for (LinkedHashMap<String, Object> m : result) {

            String f = (String) m.get("Field");
            String t = (String) m.get("Type");

            //System.out.println(f+"-->"+t);
            ///这里的类型对应关系有的数据库需要修改
            String type = "";
            if(t.contains("bigint")){
                type = "Long";
            }else if(t.contains("smallint")){
                type = "Short";
            }else if(t.contains("int")){
                type = "Integer";
            }else if(t.contains("double")){
                type = "Double";
            }else if(t.contains("float")){
                type = "Float";
            }else if(t.contains("varchar")){
                type = "String";
            }else if(t.contains("text")){
                type = "String";
            }else if(t.contains("json")){
                type = "String";
            }else if(t.contains("datetime")){
                type = "String";
            }else{

            }
            if(f.equals("id")){
                reString += "@RequestParam(\""+f+"\")"+type+" "+f+","+"\n";
            }else{
                reString += "@RequestParam(name=\""+f+"\",required = false)"+type+" "+f+","+"\n";
            }

            //System.out.println("@RequestParam(name=\""+f+"\",required = false)"+type+" "+f+",");
        }

        return reString;
    }


    private String getParaList() {

        //System.out.println("result:"+result);

        String reString = "";

        for (LinkedHashMap<String, Object> m : result) {
            String f = (String) m.get("Field");
            String t = (String) m.get("Type");
            if(f.equals("id"))continue;
            reString += ""+f+",";
            //System.out.println("@RequestParam(name=\""+f+"\",required = false)"+type+" "+f+",");
        }


        return reString.substring(0, reString.length()-1)+"\n";
    }

    private String getTypeParaList() {

        //System.out.println("result:"+result);

        String reString = "";

        for (LinkedHashMap<String, Object> m : result) {
            String f = (String) m.get("Field");
            String t = (String) m.get("Type");
            if(f.equals("id"))continue;

            String type = "";
            if(t.contains("bigint")){
                type = "Long";
            }else if(t.contains("smallint")){
                type = "Short";
            }else if(t.contains("int")){
                type = "Integer";
            }else if(t.contains("double")){
                type = "Double";
            }else if(t.contains("float")){
                type = "Float";
            }else if(t.contains("varchar")){
                type = "String";
            }else if(t.contains("text")){
                type = "String";
            }else if(t.contains("json")){
                type = "String";
            }else if(t.contains("datetime")){
                type = "String";
            }else{

            }

            reString += type+" "+f+",";
            //System.out.println("@RequestParam(name=\""+f+"\",required = false)"+type+" "+f+",");
        }


        return reString.substring(0, reString.length()-1)+"\n";
    }


    private String getUptFilter() {
        //System.out.println("result:"+result);
        String reString = "";

        for (LinkedHashMap<String, Object> m : result) {

            String f = (String) m.get("Field");
            String t = (String) m.get("Type");
            if(f.equals("id"))continue;
            if(t.contains("bigint") || t.contains("int")){
                reString += "if("+f+" != null && "+f+"!=-1){sqlStr += \""+f+"=\"+"+f+"+\",\";}"+"\n";
            }else{
                reString += "if("+f+" != null && !\"\".equals("+f+")){sqlStr += \""+f+"='\"+"+f+"+\"',\";}"+"\n";
            }

        }
        return reString;
    }

    private String getSelectFilter() {
        //System.out.println("result:"+result);
        String reString = "";

        for (LinkedHashMap<String, Object> m : result) {

            String f = (String) m.get("Field");
            String t = (String) m.get("Type");
            if(f.equals("id"))continue;
            if(t.contains("bigint") || t.contains("int")){
                reString += "if("+f+" != null && "+f+"!=-1){sqlStr += \"and "+f+"=\"+"+f+"+\",\";}"+"\n";
            }else{
                reString += "if("+f+" != null && !\"\".equals("+f+")){sqlStr += \"and "+f+"='\"+"+f+"+\"',\";}"+"\n";
            }

        }
        return reString;
    }


    private String getUptFilterWithoutIf() {
        //System.out.println("result:"+result);
        String reString = "";

        for (LinkedHashMap<String, Object> m : result) {

            String f = (String) m.get("Field");
            String t = (String) m.get("Type");
            if(f.equals("id"))continue;
            if(t.contains("bigint") || t.contains("int")){
                reString += ""+f+"=\"+"+f+"+\",";
            }else{
                reString += ""+f+"='\"+"+f+"+\"',";
            }

        }
        return reString;
    }

    private String getSelectFilterWithoutIf() {
        //System.out.println("result:"+result);
        String reString = "";

        for (LinkedHashMap<String, Object> m : result) {

            String f = (String) m.get("Field");
            String t = (String) m.get("Type");
            if(f.equals("id"))continue;
            if(t.contains("bigint") || t.contains("int")){
                reString += " and "+f+"=\"+"+f+"+\"";
            }else{
                reString += " and "+f+"='\"+"+f+"+\"'";
            }

        }
        return reString;
    }


    private String getUptFilterWithoutIfPre() {
        //System.out.println("result:"+result);
        String reString = "";

        for (LinkedHashMap<String, Object> m : result) {

            String f = (String) m.get("Field");
            String t = (String) m.get("Type");
            if(f.equals("id"))continue;
            if(t.contains("bigint") || t.contains("int")){
                reString += ""+f+"=?,";
            }else{
                reString += ""+f+"=?,";
            }

        }
        return reString;
    }

    private String getSelectFilterWithoutIfPre() {
        //System.out.println("result:"+result);
        String reString = "";

        for (LinkedHashMap<String, Object> m : result) {

            String f = (String) m.get("Field");
            String t = (String) m.get("Type");
            if(f.equals("id"))continue;
            if(t.contains("bigint") || t.contains("int")){
                reString += " and "+f+"=?";
            }else{
                reString += " and "+f+"=?";
            }

        }
        return reString;
    }

    private String getPostPara() {
        //System.out.println("result:"+result);
        String reString = "";

        for (LinkedHashMap<String, Object> m : result) {

            String f = (String) m.get("Field");
            String t = (String) m.get("Type");
            if(f.equals("id"))continue;
            if(t.contains("bigint") || t.contains("int")){
                reString += "paramMap.put(\""+f+"\",1);"+"\n";
            }else if(t.contains("datetime")){
                reString += "paramMap.put(\""+f+"\",\"2023-05-30 03:58:58\");"+"\n";
            }else{
                reString += "paramMap.put(\""+f+"\",\"a\");"+"\n";
            }

        }
        return reString;
    }

    private String getSwaggerPara() {
        //System.out.println("result:"+result);
        String reString = "";

        for (LinkedHashMap<String, Object> m : result) {

            String f = (String) m.get("Field");
            String t = (String) m.get("Type");
            if(f.equals("id"))continue;
            if(t.contains("bigint") || t.contains("int")){
                reString += "@ApiImplicitParam(name=\""+f+"\",defaultValue=\"1\"),"+"\n";
            }else if(t.contains("datetime")){
                reString += "@ApiImplicitParam(name=\""+f+"\",defaultValue=\"2023-05-30 03:58:58\"),"+"\n";
            }else{
                reString += "@ApiImplicitParam(name=\""+f+"\",defaultValue=\"aaa\"),"+"\n";
            }

        }
        return reString;

    }

    public static void main(String[] args) {



        String para1String = new GenerateFromDB().getInterPara();
        System.out.println("接口参数列表(区分了id非空) = " + "\n" + para1String);




        String paraList = new GenerateFromDB().getParaList();
        System.out.println("新增接口使用的参数列表串(过滤了id) = "  + "\n" + paraList);

        String typeParaList = new GenerateFromDB().getTypeParaList();
        System.out.println("新增接口使用的参数列表串(过滤了id) = "  + "\n" + typeParaList);

        String uptFilter = new GenerateFromDB().getUptFilter();
        System.out.println("修改接口使用的参数过滤语句(过滤了id) = "  + "\n" + uptFilter);

        String selectFilter = new GenerateFromDB().getSelectFilter();
        System.out.println("新增接口使用的参数列表串(过滤了id) = "  + "\n" + selectFilter);

        String uptFilterWithoutIf = new GenerateFromDB().getUptFilterWithoutIf();
        System.out.println("修改接口使用的参数过滤语句(过滤了id)(单句无需if) = "  + "\n" + uptFilterWithoutIf+ "\n");

        String selectFilterWithoutIf = new GenerateFromDB().getSelectFilterWithoutIf();
        System.out.println("查询接口使用的参数列表串(过滤了id)(单句无需if) = "  + "\n" + selectFilterWithoutIf+ "\n");

        String uptFilterWithoutIfPre = new GenerateFromDB().getUptFilterWithoutIfPre();
        System.out.println("修改接口使用的参数过滤语句(过滤了id)(单句无需if)(占位符) = "  + "\n" + uptFilterWithoutIfPre+ "\n");

        String selectFilterWithoutIfPre = new GenerateFromDB().getSelectFilterWithoutIfPre();
        System.out.println("查询接口使用的参数列表串(过滤了id)(单句无需if)(占位符) = "  + "\n" + selectFilterWithoutIfPre+ "\n");

        String postPara = new GenerateFromDB().getPostPara();
        System.out.println("测试客户请求参数(过滤了id) = "  + "\n" + postPara);

        String swaggerPara = new GenerateFromDB().getSwaggerPara();
        System.out.println("Swagger测试请求参数(过滤了id) = "  + "\n" + swaggerPara);


    }


}