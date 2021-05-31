/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.elcom.vinamailreport.common.interfaces;

/**
 *
 * @author Administrator
 */
public interface MailInterface {

    public boolean send(String tos) throws Exception;
    public String getContend();
    public String getContend(String contentFile, String sqlFile);
}
