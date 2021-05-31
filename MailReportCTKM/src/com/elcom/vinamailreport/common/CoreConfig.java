/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.vinamailreport.common;

import com.elcom.utils.misc.Config;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

/**
 *
 * @author tig3r4ev3r
 */
public class CoreConfig extends Config {

    public List<TableDataEntity> lstData = new ArrayList<TableDataEntity>();

    public CoreConfig(String filename) {
        super(filename);
        load();
    }

    public void load() {
        super.load();
        scatter();
    }

    private void scatter() {

        lstData.clear();

        for (int i = 1; i < 10; i++) {
            String header = getKey("CONTENT" + "." + i, "HEADER", null);
            String data_header = getKey("CONTENT" + "." + i, "DATA_HEADER", null);
            String data_query = getKey("CONTENT" + "." + i, "DATA_QUERY", "");
            //0 - Query, 1 - StoreProcedure
            int driver_type = getKeyInt("CONTENT" + "." + i, "DATA_DRIVER", 0);
            int data_query_type = getKeyInt("CONTENT" + "." + i, "DATA_QUERY_TYPE", 0);
            String data_list = getKey("CONTENT" + "." + i, "DATA_LIST", null);
            int footer_type = getKeyInt("CONTENT" + "." + i, "DATA_FOOTER_TYPE", 0);
            String style = getKey("CONTENT" + "." + i, "STYLES", "#F64747;#81CFE0;#C5EFF7");

            if (data_header == null || data_query == null || data_list == null) {
                continue;
            }
            TableDataEntity entity = new TableDataEntity();
            String[] stls = style.split(";");
            
            if(stls.length < 3)
            {
                stls = String.valueOf("#F64747;#81CFE0;#C5EFF7").split(";");
            }
            
            for(String s : stls)
            {
                entity.getStyle().add(s);
            }
            
            
            entity.setData_header(data_header);
            entity.setDriver_type(driver_type);
            if(driver_type == 0)
            {
                entity.setData_list(data_list.toUpperCase());
                entity.setData_query(data_query.toUpperCase());
            }
            else
            {
                entity.setData_list(data_list);
                entity.setData_query(data_query);
            }
            entity.setHeader(header);
            entity.setData_query_type(data_query_type);
            entity.setFooter_type(footer_type);
            if (footer_type != TableDataEntity.DATA_FOOTER_TYPE.TYPE_NONE) {
                String footer_list = getKey("CONTENT" + "." + i, "DATA_FOOTER_LIST", "");
                String footer_query = getKey("CONTENT" + "." + i, "DATA_FOOTER_QUERY", "");
                entity.setFooter_list(footer_list.toUpperCase());
                entity.setFooter_query(footer_query);
            }

            lstData.add(entity);
        }
    }

    public void store() {
        gather();
        super.store();
    }

    private void gather() {

        for (int i = 0; i < lstData.size(); i++) {
            TableDataEntity info = lstData.get(i);
            setKey("CONTENT." + (i + 1), "HEADER", info.getHeader());
            setKey("CONTENT." + (i + 1), "DATA_HEADER", info.getData_header());
            setKey("CONTENT." + (i + 1), "DATA_QUERY", info.getData_query());
            setKey("CONTENT." + (i + 1), "DATA_LIST", info.getData_list());


        }


    }

    public List<TableDataEntity> getLstData() {
        return lstData;
    }

    public void setLstData(List<TableDataEntity> lstData) {
        this.lstData = lstData;
    }
}
