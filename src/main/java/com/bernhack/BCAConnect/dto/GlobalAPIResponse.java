package com.bernhack.BCAConnect.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GlobalAPIResponse {

    private int statusCode;
    private String message;
    private Object data;
}
