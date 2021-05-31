/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.elcom.vinamailreport.common.messages;

import java.util.List;

/**
 *
 * @author Administrator
 */
public class Report {

    private List<ReportEntity> list;
    private String time;
//    private int active;
//    private int deactive;
//    private int totalActive;
//    private int totalDeactive;

//    public int getActive() {
//        return active;
//    }
//
//    public void setActive(int active) {
//        this.active = active;
//    }
//
//    public int getDeactive() {
//        return deactive;
//    }
//
//    public void setDeactive(int deactive) {
//        this.deactive = deactive;
//    }

    public List<ReportEntity> getList() {
        return list;
    }

    public void setList(List<ReportEntity> list) {
        this.list = list;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

//    public int getTotalActive() {
//        return totalActive;
//    }
//
//    public void setTotalActive(int totalActive) {
//        this.totalActive = totalActive;
//    }
//
//    public int getTotalDeactive() {
//        return totalDeactive;
//    }
//
//    public void setTotalDeactive(int totalDeactive) {
//        this.totalDeactive = totalDeactive;
//    }

}
