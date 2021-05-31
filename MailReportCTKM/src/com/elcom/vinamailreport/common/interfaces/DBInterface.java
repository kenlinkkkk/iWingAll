/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elcom.vinamailreport.common.interfaces;

import com.elcom.vinamailreport.common.messages.Report;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Administrator
 */
public interface DBInterface {

    public Report getInput();
    public String getAddress();
    public List<HashMap<String, String>> getDataFromQuery(String query);
    public List<HashMap<String, String>> getDataFromStore(String query);
    public List<HashMap<String, String>> getDataFromStoreMySQL(String query);
    public boolean runSummary(Date report_date);
}
