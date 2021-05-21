CREATE PROCEDURE `DAILY_SUMMARY_REPORT`(in p_date date)
BEGIN
	DECLARE v_data_exists INT;
    SET v_data_exists := 0;
    
    SET @sql := CONCAT('his_', DATE_FORMAT(p_date, '%Y%m'));
    SET @sql_mt := CONCAT('mt_', DATE_FORMAT(p_date, '%Y%m'));
	SEt @DF :=  CONCAT('DATE_FORMAT("',p_date,'", "%Y-%m-%d")');
        
	SET SQL_SAFE_UPDATES = 0;
    
    select count(*) into v_data_exists from `daily_summary` where DATE_FORMAT(date_report, '%Y-%m-%d') = DATE_FORMAT(p_date, '%Y-%m-%d');
    if v_data_exists <= 0 then
		delete from `daily_summary` where DATE_FORMAT(date_report, '%Y-%m-%d') = DATE_FORMAT(p_date, '%Y-%m-%d');
		
		
		SET @sql_ins = CONCAT(
		'insert into `daily_summary` (
			`date_report`,
			`sub_active`,
			`total_register`,
			`total_unregister`,
			`revenue_total`,
			`total_ivr_charge`,
			`revenue_renew`)
		select "', DATE_FORMAT(p_date, '%Y-%m-%d') ,'" date_report,
		(select count(*) from subscriber xx where xx.status = 1) as sub_active,  
		COALESCE(sum(case when action in ("FirstREG", "ReREG") and result = 0 then 1 else 0 end), 0) total_register,
		COALESCE(sum(case when action in ("UNREG","DELETE") and result = 0 then 1 else 0 end), 0) total_unregister,
		COALESCE(sum(case when action in ("RENEW","FirstREG","ReREG") and result = 0 then fee else 0 end), 0) revenue_total, 
		COALESCE(sum(case when action in ("CALL_IVR") and result = 0 then fee else 0 end), 0) total_ivr_charge, 
		COALESCE(sum(case when action in ("RENEW") and result = 0 then fee else 0 end), 0) revenue_renew 
		from ',  @sql , ' a
		where action in ("FirstREG", "ReREG", "UNREG", "RENEW","DELETE","CALL_IVR") 
		and DATE_FORMAT(created_time, "%Y-%m-%d") = ', @DF);
		
		insert into omap_log(action, description) values ('REPORT', @sql_ins);
		commit;

		Prepare stmt FROM @sql_ins;
		Execute stmt;
		DEALLOCATE PREPARE stmt;

		commit;
	else 
		SET @sql_ins = CONCAT(
		'UPDATE `daily_summary`  SET 
			`sub_active` = (select count(*) from subscriber xx where xx.status = 1),
			`total_register` = (select COALESCE(sum(case when action in ("FirstREG", "ReREG") and result = 0 then 1 else 0 end), 0) from ',  @sql , ' a
				where action in ("FirstREG", "ReREG", "UNREG", "RENEW") 
				and DATE_FORMAT(created_time, "%Y-%m-%d") = ', @DF ,'),
                `total_register_psc` = (select COALESCE(sum(case when action in ("FirstREG", "ReREG") and fee > 0 then 1 else 0 end), 0) from ',  @sql , ' a
				where action in ("FirstREG", "ReREG", "UNREG", "RENEW") 
				and DATE_FORMAT(created_time, "%Y-%m-%d") = ', @DF ,'),
                `total_register_new` = (select COALESCE(sum(case when action in ("FirstREG") and result = 0 then 1 else 0 end), 0) from ',  @sql , ' a
				where action in ("FirstREG", "ReREG", "UNREG", "RENEW") 
				and DATE_FORMAT(created_time, "%Y-%m-%d") = ', @DF ,'),
                `total_register_again` = (select COALESCE(sum(case when action in ("ReREG") and result = 0 then 1 else 0 end), 0) from ',  @sql , ' a
				where action in ("FirstREG", "ReREG", "UNREG", "RENEW") 
				and DATE_FORMAT(created_time, "%Y-%m-%d") = ', @DF ,'),
                
                `total_unregister` = (select COALESCE(sum(case when action in ("UNREG","DELETE") and result = 0 then 1 else 0 end), 0) from ',  @sql , ' a
				where action in ("FirstREG", "ReREG", "UNREG", "RENEW","DELETE") 
				and DATE_FORMAT(created_time, "%Y-%m-%d") = ', @DF ,'),
                `unreg_by_sys` = (select COALESCE(sum(case when action = "DELETE" and result = 0  then 1 else 0 end), 0) from ',  @sql , ' a
				where action in ("FirstREG", "ReREG", "UNREG", "RENEW","DELETE") 
				and DATE_FORMAT(created_time, "%Y-%m-%d") = ', @DF ,'),
                 `unreg_by_sub` = (select COALESCE(sum(case when action in ("UNREG") and result = 0 and channel <> "SYS" then 1 else 0 end), 0) from ',  @sql , ' a
				where action in ("FirstREG", "ReREG", "UNREG", "RENEW") 
				and DATE_FORMAT(created_time, "%Y-%m-%d") = ', @DF ,'),
                `revenue_total` = (select COALESCE(sum(case when action in ("RENEW","FirstREG", "ReREG") and result = 0 then fee else 0 end), 0) from ',  @sql , ' a
				where action in ("FirstREG", "ReREG", "UNREG", "RENEW") 
				and DATE_FORMAT(created_time, "%Y-%m-%d") = ', @DF ,'),
				 `total_ivr_charge` = (select COALESCE(sum(case when action in ("CALL_IVR") and result = 0 then fee else 0 end), 0) from ',  @sql , ' a
				where action in ("CALL_IVR") 
				and DATE_FORMAT(created_time, "%Y-%m-%d") = ', @DF ,'),
                `revenue_register` = (select COALESCE(sum(case when action in ("FirstREG", "ReREG") and result = 0 then fee else 0 end), 0) from ',  @sql , ' a
				where action in ("FirstREG", "ReREG", "UNREG", "RENEW") 
				and DATE_FORMAT(created_time, "%Y-%m-%d") = ', @DF ,'),
                `revenue_renew` = (select COALESCE(sum(case when action in ("RENEW") and result = 0 then fee else 0 end), 0) from ',  @sql , ' a
				where action in ("FirstREG", "ReREG", "UNREG", "RENEW") 
				and DATE_FORMAT(created_time, "%Y-%m-%d") = ', @DF ,')
                
		WHERE DATE_FORMAT(date_report, "%Y-%m-%d") = ', @DF);
		
		insert into omap_log(action, description) values ('REPORT', @sql_ins);
		commit;

		Prepare stmt FROM @sql_ins;
		Execute stmt;
		DEALLOCATE PREPARE stmt;
        
		insert into omap_log(action, description) values ('REPORT', 'Data exists');
		commit;
    end if;
    
    SET @sql_ins = CONCAT(
		'UPDATE `daily_summary`  SET `REVENUE_CUMULATIVE` = (select sum(fee) from ',  @sql , ' a where action in ("FirstREG", "ReREG", "UNREG", "RENEW") 
				and DATE_FORMAT(created_time, "%Y-%m-%d") <= ', @DF ,') WHERE DATE_FORMAT(date_report, "%Y-%m-%d") = ', @DF);
		Prepare stmt FROM @sql_ins;
		Execute stmt;
		DEALLOCATE PREPARE stmt;     
        
        SET @sql_ins = CONCAT(
		'UPDATE `daily_summary`  SET 
			`total_mt_submit` = (select count(*) from ',  @sql_mt , ' a where DATE_FORMAT(created_time, "%Y-%m-%d") = ', @DF ,'),
			`total_mt_success` = (select count(*) from ',  @sql_mt , ' a where DATE_FORMAT(created_time, "%Y-%m-%d") = ', @DF ,'),
            `total_mt_ratio` = 100,
            `total_mt_renew` = (select count(*) from ',  @sql_mt , ' a where DATE_FORMAT(created_time, "%Y-%m-%d") = ', @DF ,' and command in ("1.9.1", "1.9.3")), 
            `total_mt_expire_promo` = (select count(*) from ',  @sql_mt , ' a where DATE_FORMAT(created_time, "%Y-%m-%d") = ', @DF ,' and command in ("1.9.2"))
            WHERE DATE_FORMAT(date_report, "%Y-%m-%d") = ', @DF);
		Prepare stmt FROM @sql_ins;
		Execute stmt;
		DEALLOCATE PREPARE stmt;  
        
        SET @sql_ins = CONCAT(
		'UPDATE `daily_summary`  SET 
			`total_charge_req` = (select COALESCE(sum(case when action in ("RENEW", "FirstREG", "ReREG") and result = 0 then 1 else 1 end), 0) from ',  @sql , ' a
				where fee > 0 
				and DATE_FORMAT(created_time, "%Y-%m-%d") = ', @DF ,'),
             `total_charge_success` = (select COALESCE(sum(case when action in ("RENEW", "FirstREG", "ReREG") and result = 0 then 1 else 0 end), 0) from ',  @sql , ' a
				where fee > 0 
				and DATE_FORMAT(created_time, "%Y-%m-%d") = ', @DF ,')    
            WHERE DATE_FORMAT(date_report, "%Y-%m-%d") = ', @DF);
		Prepare stmt FROM @sql_ins;
		Execute stmt;
		DEALLOCATE PREPARE stmt;  
        
        SET @sql_ins = CONCAT(
		'UPDATE daily_summary set total_charge_success_ratio = total_charge_success / total_charge_req where DATE_FORMAT(date_report, "%Y-%m-%d") = ', @DF);
        Prepare stmt FROM @sql_ins;
		Execute stmt;
		DEALLOCATE PREPARE stmt; 
        
        
        delete from `kpi_charging` where DATE_FORMAT(date_report, '%Y-%m-%d') = DATE_FORMAT(p_date, '%Y-%m-%d');
        SET @sql_ins = CONCAT(
		'insert into `kpi_charging` (
			`date_report`,
			`error_code`,
			`total_resp`,
            `error_description`) select "', DATE_FORMAT(p_date, '%Y-%m-%d') ,'" date_report, result, count(*), "" from ',  @sql , ' 
            where DATE_FORMAT(created_time, "%Y-%m-%d") = ', @DF ,' AND fee > 0 group by result');
            
		Prepare stmt FROM @sql_ins;
		Execute stmt;
		DEALLOCATE PREPARE stmt; 
        SET SQL_SAFE_UPDATES = 0;
        update kpi_charging set error_code = 'CPS-0000' where error_code = '0' and DATE_FORMAT(date_report, '%Y-%m-%d') = DATE_FORMAT(p_date, '%Y-%m-%d');
        update kpi_charging set error_code = 'CPS-1001' where error_code = '1001' and DATE_FORMAT(date_report, '%Y-%m-%d') = DATE_FORMAT(p_date, '%Y-%m-%d');
        update kpi_charging set error_code = 'CPS-2011' where error_code = '2011' and DATE_FORMAT(date_report, '%Y-%m-%d') = DATE_FORMAT(p_date, '%Y-%m-%d');
        update kpi_charging set error_code = 'CPS-1007' where error_code = '1007' and DATE_FORMAT(date_report, '%Y-%m-%d') = DATE_FORMAT(p_date, '%Y-%m-%d');
        update kpi_charging set error_code = 'CPS-0101' where error_code = '101' and DATE_FORMAT(date_report, '%Y-%m-%d') = DATE_FORMAT(p_date, '%Y-%m-%d');
        
		
        
        delete from `kpi_subscriber` where DATE_FORMAT(date_report, '%Y-%m-%d') = DATE_FORMAT(p_date, '%Y-%m-%d');
       
        SET @sql_ins = CONCAT(
        'insert into `kpi_subscriber` (
			`date_report`,
			`total_active`,
			`total_reg`,
            `total_unreg`,
            `reg_wap`,
            `reg_web`,
            `reg_vasgate`,
            `reg_sms`,
            `reg_avb`,
            `reg_other`,
            `unreg_wap`,
            `unreg_web`,
            `unreg_vasgate`,
            `unreg_sms`,
            `unreg_sys`,
            `unreg_other`) select "', DATE_FORMAT(p_date, '%Y-%m-%d') ,'" date_report, (select count(*) from subscriber where status = 1) total_active,
            COALESCE(sum(case when action in ("FirstREG", "ReREG") and result = 0 then 1 else 0 end), 0) total_reg,
             COALESCE(sum(case when action in ("UNREG","DELETE") and result = 0 then 1 else 0 end), 0) total_unreg,
            COALESCE(sum(case when action in ("FirstREG", "ReREG") and result = 0 and channel = "WAP" then 1 else 0 end), 0) reg_wap,
            COALESCE(sum(case when action in ("FirstREG", "ReREG") and result = 0 and channel = "WEB" then 1 else 0 end), 0) reg_web,
            COALESCE(sum(case when action in ("FirstREG", "ReREG") and result = 0 and channel = "VASGATE" then 1 else 0 end), 0) reg_vas,
            COALESCE(sum(case when action in ("FirstREG", "ReREG") and result = 0 and channel = "SMS" then 1 else 0 end), 0) reg_sms,
            COALESCE(sum(case when action in ("FirstREG", "ReREG") and result = 0 and channel = "VBC" then 1 else 0 end), 0) reg_avb,
            COALESCE(sum(case when action in ("FirstREG", "ReREG") and result = 0 and channel in ("?","WCC") then 1 else 0 end), 0) reg_other,
            COALESCE(sum(case when action in ("UNREG") and result = 0 and channel = "WAP" then 1 else 0 end), 0) unreg_wap,
			COALESCE(sum(case when action in ("UNREG") and result = 0 and channel = "WEB" then 1 else 0 end), 0) unreg_web,
            COALESCE(sum(case when action in ("UNREG") and result = 0 and channel = "VASGATE" then 1 else 0 end), 0) unreg_vas,
            COALESCE(sum(case when action in ("UNREG") and result = 0 and channel = "SMS" then 1 else 0 end), 0) unreg_sms,
            COALESCE(sum(case when action in ("DELETE") and result = 0 then 1 else 0 end), 0) unreg_sys,
            COALESCE(sum(case when action in ("UNREG") and result = 0 and channel in ("?","WCC") then 1 else 0 end), 0) unreg_other
            from ',  @sql , ' a
		where action in ("FirstREG", "ReREG", "UNREG", "RENEW","DELETE") 
		and DATE_FORMAT(created_time, "%Y-%m-%d") = ', @DF);
        insert into omap_log(action, description) values ('REPORT SUB', @sql_ins);
		commit;
        
        Prepare stmt FROM @sql_ins;
		Execute stmt;
		DEALLOCATE PREPARE stmt; 
        SET SQL_SAFE_UPDATES = 1;   
        
END