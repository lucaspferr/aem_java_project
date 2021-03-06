package com.mysite.core.utils;

import org.apache.sling.api.SlingHttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BuildResponse {

    public static void buildResponse(SlingHttpServletResponse resp, int statusCode, String jsonBody) throws IOException{
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setStatus(statusCode);
        resp.getWriter().write(jsonBody);
    }

    public static String idNotFound(String id, String name, String found){
        if(found.equals("")) return "{\"id\":\""+id+"\",\"message\":\""+name+" id not found\"}";
        else return "{\"id not found\":\""+id+"\",\"message\":\""+name+" id not found\",\"ids deleted\":\""+found+"\"}";
    }

    public static String noResultsFound(String nome){
        return "{\"query\":\""+nome+"\",\"message\":\"No results found\"}";
    }

    public static String invalidInput(String input){
        return "{\"input\":\""+input+"\",\"message\":\"Invalid input\"}";
    }

    public static String postFailed(String message){
        return "{\"exception\":\""+"SQL Integrity Constraint Violation "+"\",\"message\":\""+message+"\"}";
    }

    public static String invalidPayload(String entity){
        return "{\"entity\":\""+entity+"\",\"message\":\"Invalid payload\"}";
    }

    public static String invalidPayloadDetailed(String entity, String parameter){
        return "{\"entity\":\""+entity+"\",\"parameter\":\""+parameter+"\",\"message\":\"Invalid payload\"}";
    }

    public static String parameterException(String parameter){
        return "{\"parameter\":\""+parameter+"\",\"message\":\"Missing parameter and/or value\"}";
    }

}
