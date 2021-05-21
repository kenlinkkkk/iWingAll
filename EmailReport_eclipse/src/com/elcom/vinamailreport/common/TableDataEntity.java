/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.vinamailreport.common;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tig3r4ev3r
 */
public class TableDataEntity {
    public static final class DATA_QUERY_TYPE 
    {
        public static final int TYPE_QUERY = 0;
        public static final int TYPE_STORE = 1;
    }
    
    public static final class DATA_FOOTER_TYPE 
    {
        public static final int TYPE_NONE = 0;
        public static final int TYPE_SUM = 1;
        public static final int TYPE_QUERY = 2;
        public static final int TYPE_STORE = 3;
    }
    
    private List<String> style = new ArrayList<String>();
    
    private String header = "";
    private String data_header = "";
    private String data_query = "";
    private String data_list = "";
//    private String sum_list = "";
    private int data_query_type = 0;
    private String footer_list = "";
    private int  footer_type = 0;
    private String footer_query = "";
    private int driver_type = 0;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getData_header() {
        return data_header;
    }

    public void setData_header(String data_header) {
        this.data_header = data_header;
    }

    public String getData_query() {
        return data_query;
    }

    public void setData_query(String data_query) {
        this.data_query = data_query;
    }

    public String getData_list() {
        return data_list;
    }

    public void setData_list(String data_list) {
        this.data_list = data_list;
    }

//    public String getSum_list() {
//        return sum_list;
//    }
//
//    public void setSum_list(String sum_list) {
//        this.sum_list = sum_list;
//    }

    public int getData_query_type() {
        return data_query_type;
    }

    public void setData_query_type(int data_query_type) {
        this.data_query_type = data_query_type;
    }

    public String getFooter_list() {
        return footer_list;
    }

    public void setFooter_list(String footer_list) {
        this.footer_list = footer_list;
    }

    public int getFooter_type() {
        return footer_type;
    }

    public void setFooter_type(int footer_type) {
        this.footer_type = footer_type;
    }

    public String getFooter_query() {
        return footer_query;
    }

    public void setFooter_query(String footer_query) {
        this.footer_query = footer_query;
    }

    public List<String> getStyle() {
        return style;
    }

    public void setStyle(List<String> style) {
        this.style = style;
    }

    public int getDriver_type() {
        return driver_type;
    }

    public void setDriver_type(int driver_type) {
        this.driver_type = driver_type;
    }
}
