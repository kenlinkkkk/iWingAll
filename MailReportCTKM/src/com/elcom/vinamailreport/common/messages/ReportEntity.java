/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.elcom.vinamailreport.common.messages;

/**
 *
 * @author Administrator
 */
public class ReportEntity {
    private String time;
    private int total_active;
    private int total_unregister_by_sub;
    private int total_unregister_by_sys;
    private int total_register;
    private int total_register_PSC;
    private double total_register_PSC_percent;
    private int total_recharge;
    private int total_recharge_PSC;
    private double total_recharge_PSC_percent;
    private double total_revenue;
    //========================================
    private double total_to_date = 0;
    private double total_of_last_month = 0;
    
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getTotal_active() {
        return total_active;
    }

    public void setTotal_active(int total_active) {
        this.total_active = total_active;
    }

    public int getTotal_recharge() {
        return total_recharge;
    }

    public void setTotal_recharge(int total_recharge) {
        this.total_recharge = total_recharge;
    }

    public int getTotal_recharge_PSC() {
        return total_recharge_PSC;
    }

    public void setTotal_recharge_PSC(int total_recharge_PSC) {
        this.total_recharge_PSC = total_recharge_PSC;
    }

    public double getTotal_recharge_PSC_percent() {
        return total_recharge_PSC_percent;
    }

    public void setTotal_recharge_PSC_percent(double total_recharge_PSC_percent) {
        this.total_recharge_PSC_percent = total_recharge_PSC_percent;
    }

    public int getTotal_register() {
        return total_register;
    }

    public void setTotal_register(int total_register) {
        this.total_register = total_register;
    }

    public int getTotal_register_PSC() {
        return total_register_PSC;
    }

    public void setTotal_register_PSC(int total_register_PSC) {
        this.total_register_PSC = total_register_PSC;
    }

    public double getTotal_register_PSC_percent() {
        return total_register_PSC_percent;
    }

    public void setTotal_register_PSC_percent(double total_register_PSC_percent) {
        this.total_register_PSC_percent = total_register_PSC_percent;
    }

//    public double getTotal_revenue() {
//        return total_revenue;
//    }
//
//    public void setTotal_revenue(double total_revenue) {
//        this.total_revenue = total_revenue;
//    }

    public double getTotal_revenue() {
        return total_revenue;
    }

    public void setTotal_revenue(double total_revenue) {
        this.total_revenue = total_revenue;
    }
    
    
    public int getTotal_unregister_by_sub() {
        return total_unregister_by_sub;
    }

    public void setTotal_unregister_by_sub(int total_unregister_by_sub) {
        this.total_unregister_by_sub = total_unregister_by_sub;
    }

    public int getTotal_unregister_by_sys() {
        return total_unregister_by_sys;
    }

    public void setTotal_unregister_by_sys(int total_unregister_by_sys) {
        this.total_unregister_by_sys = total_unregister_by_sys;
    }

    public double getTotal_of_last_month() {
        return total_of_last_month;
    }

    public void setTotal_of_last_month(double total_of_last_month) {
        this.total_of_last_month = total_of_last_month;
    }

    public double getTotal_to_date() {
        return total_to_date;
    }

    public void setTotal_to_date(double total_to_date) {
        this.total_to_date = total_to_date;
    }
    
    
    @Override
    public String toString(){
        return "DATE: " + time + " /ACTIVE: " + total_active + " /REG: " + total_register + " /REG_PSC: " + total_register_PSC + " /REG_PSC_PERCENT: " + total_register_PSC_percent 
                + " /RENEW: " + total_recharge + " /RENEW_PSC: " + total_recharge_PSC + " /RENEW_PSC_PERCENT: " + total_recharge_PSC_percent + " /UNREG_BY_SUB: " + total_unregister_by_sub
                + " /UNREG_BY_SYS: " + total_unregister_by_sys + " /REVENUE: " + total_revenue  + " /UP_TO_DATE: " + total_to_date  + " /LAST_MONTH: " + total_of_last_month;
    }
}
