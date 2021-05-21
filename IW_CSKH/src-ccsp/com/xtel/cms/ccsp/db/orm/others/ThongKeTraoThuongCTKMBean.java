package com.xtel.cms.ccsp.db.orm.others;

import java.io.Serializable;

import com.ligerdev.appbase.utils.db.AntTable;
import com.ligerdev.appbase.utils.db.AntColumn;

import java.util.Date;


/*
    - 07/05/2019 14:50:39: Generated by sql
	select d.id,d.name,d.begin_time,d.end_time,d.noidung,'' time_ctkm , d.count_dk, d.count_dudkcongthuong, d.count_daduoccongthuong,
	 round(1/3,4) tyle_dccongthuong, d.count_chuadapung, d.count_huytruoc24h, e.count_psc from (select  a.id, sum(1) count_dk,
	  sum(case when (b.deactive24h_time is null or b.deactive24h_time<b.created_time+interval 1 day) and (b.renew24h_time is not null) then 1 else 0 end) count_dudkcongthuong,
	   sum(case when (b.deactive24h_time is null or b.deactive24h_time<b.created_time+interval 1 day) and (b.renew24h_time is not null) and b.kmtq_id is not null and c.status=2 then 1 else 0 end)
	    count_daduoccongthuong, sum(case when (b.deactive24h_time is null or b.deactive24h_time<b.created_time+interval 1 day) and (b.renew24h_time is null) then 1 else 0 end) count_chuadapung,
	     sum(case when b.deactive24h_time<b.created_time+interval 1 day then 1 else 0 end) count_huytruoc24h, a.name,a.begin_time,a.end_time,a.noidung from dm_ctkm a join stats_kmtq b
	      on a.category=b.category left join kmtq c  on b.created_time=c.created_time and b.msisdn=c.msisdn where a.status=1 group by a.id) d join (select c.id,count(c.msisdn) count_psc
	      from (select  distinct a.id,b.msisdn from dm_ctkm a  join his_renew_kmtq b on a.category=b.category where 1=1)c group by c.id) e on d.id=e.id
*/
@AntTable(catalog = "", name = "ThongKeTraoThuongCTKMBean", label = "ThongKeTraoThuongCTKMBean", key = "id")
public class ThongKeTraoThuongCTKMBean implements Serializable, Cloneable {

    private int id;
    private String name;
    private Date beginTime;
    private Date endTime;
    private String noidung;
    private String timeCtkm;
    private String countDk;
    private String countDudkcongthuong;
    private String countDaduoccongthuong;
    private String tyleDccongthuong;
    private String countChuadapung;
    private String countHuytruoc24h;
    private String countPsc;

    public ThongKeTraoThuongCTKMBean() {
    }

    public ThongKeTraoThuongCTKMBean(int id, String name, Date beginTime, Date endTime, String noidung, String timeCtkm, String countDk, String countDudkcongthuong, String countDaduoccongthuong, String tyleDccongthuong, String countChuadapung, String countHuytruoc24h, String countPsc) {
        this();
        this.id = id;
        this.name = name;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.noidung = noidung;
        this.timeCtkm = timeCtkm;
        this.countDk = countDk;
        this.countDudkcongthuong = countDudkcongthuong;
        this.countDaduoccongthuong = countDaduoccongthuong;
        this.tyleDccongthuong = tyleDccongthuong;
        this.countChuadapung = countChuadapung;
        this.countHuytruoc24h = countHuytruoc24h;
        this.countPsc = countPsc;
    }

    @AntColumn(name = "id", size = 11, label = "id")
    public void setId(int id) {
        this.id = id;
    }

    @AntColumn(name = "id", size = 11, label = "id")
    public int getId() {
        return this.id;
    }

    @AntColumn(name = "name", size = 100, label = "name")
    public void setName(String name) {
        this.name = name;
    }

    @AntColumn(name = "name", size = 100, label = "name")
    public String getName() {
        return this.name;
    }

    @AntColumn(name = "begin_time", size = 19, label = "begin_time")
    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    @AntColumn(name = "begin_time", size = 19, label = "begin_time")
    public Date getBeginTime() {
        return this.beginTime;
    }

    @AntColumn(name = "end_time", size = 19, label = "end_time")
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @AntColumn(name = "end_time", size = 19, label = "end_time")
    public Date getEndTime() {
        return this.endTime;
    }

    @AntColumn(name = "noidung", size = 100, label = "noidung")
    public void setNoidung(String noidung) {
        this.noidung = noidung;
    }

    @AntColumn(name = "noidung", size = 100, label = "noidung")
    public String getNoidung() {
        return this.noidung;
    }

    @AntColumn(name = "time_ctkm", size = 0, label = "time_ctkm")
    public void setTimeCtkm(String timeCtkm) {
        this.timeCtkm = timeCtkm;
    }

    @AntColumn(name = "time_ctkm", size = 0, label = "time_ctkm")
    public String getTimeCtkm() {
        return this.timeCtkm;
    }

    @AntColumn(name = "count_dk", size = 24, label = "count_dk")
    public void setCountDk(String countDk) {
        this.countDk = countDk;
    }

    @AntColumn(name = "count_dk", size = 24, label = "count_dk")
    public String getCountDk() {
        return this.countDk;
    }

    @AntColumn(name = "count_dudkcongthuong", size = 24, label = "count_dudkcongthuong")
    public void setCountDudkcongthuong(String countDudkcongthuong) {
        this.countDudkcongthuong = countDudkcongthuong;
    }

    @AntColumn(name = "count_dudkcongthuong", size = 24, label = "count_dudkcongthuong")
    public String getCountDudkcongthuong() {
        return this.countDudkcongthuong;
    }

    @AntColumn(name = "count_daduoccongthuong", size = 24, label = "count_daduoccongthuong")
    public void setCountDaduoccongthuong(String countDaduoccongthuong) {
        this.countDaduoccongthuong = countDaduoccongthuong;
    }

    @AntColumn(name = "count_daduoccongthuong", size = 24, label = "count_daduoccongthuong")
    public String getCountDaduoccongthuong() {
        return this.countDaduoccongthuong;
    }

    @AntColumn(name = "tyle_dccongthuong", size = 7, label = "tyle_dccongthuong")
    public void setTyleDccongthuong(String tyleDccongthuong) {
        this.tyleDccongthuong = tyleDccongthuong;
    }

    @AntColumn(name = "tyle_dccongthuong", size = 7, label = "tyle_dccongthuong")
    public String getTyleDccongthuong() {
        return this.tyleDccongthuong;
    }

    @AntColumn(name = "count_chuadapung", size = 24, label = "count_chuadapung")
    public void setCountChuadapung(String countChuadapung) {
        this.countChuadapung = countChuadapung;
    }

    @AntColumn(name = "count_chuadapung", size = 24, label = "count_chuadapung")
    public String getCountChuadapung() {
        return this.countChuadapung;
    }

    @AntColumn(name = "count_huytruoc24h", size = 24, label = "count_huytruoc24h")
    public void setCountHuytruoc24h(String countHuytruoc24h) {
        this.countHuytruoc24h = countHuytruoc24h;
    }

    @AntColumn(name = "count_huytruoc24h", size = 24, label = "count_huytruoc24h")
    public String getCountHuytruoc24h() {
        return this.countHuytruoc24h;
    }

    @AntColumn(name = "count_psc", size = 21, label = "count_psc")
    public void setCountPsc(String countPsc) {
        this.countPsc = countPsc;
    }

    @AntColumn(name = "count_psc", size = 21, label = "count_psc")
    public String getCountPsc() {
        return this.countPsc;
    }

    @Override
    public String toString() {
        return "["
                + "id=" + id
                + ", name=" + name
                + ", beginTime=" + beginTime
                + ", endTime=" + endTime
                + ", noidung=" + noidung
                + ", timeCtkm=" + timeCtkm
                + ", countDk=" + countDk
                + ", countDudkcongthuong=" + countDudkcongthuong
                + ", countDaduoccongthuong=" + countDaduoccongthuong
                + ", tyleDccongthuong=" + tyleDccongthuong
                + ", countChuadapung=" + countChuadapung
                + ", countHuytruoc24h=" + countHuytruoc24h
                + ", countPsc=" + countPsc
                + "]";
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}