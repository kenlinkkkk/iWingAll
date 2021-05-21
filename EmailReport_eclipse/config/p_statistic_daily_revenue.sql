CREATE PROCEDURE `p_statistic_daily_revenue`(p_date datetime)
BEGIN
  #Routine body goes here...
	set @table_name = concat('his_',DATE_FORMAT(p_date,'%Y%m'));
	set @crr_day = DATE_FORMAT(p_date,'%Y-%m-%d');
	set @first_day = DATE_FORMAT(p_date,'%Y-%m-01');
	set @same_day = DATE_SUB(@crr_day, INTERVAL 1 MONTH);
	
	delete from statistic_daily_revenue where time = @crr_day;
	set @sqlt = 'insert into statistic_daily_revenue select date(created_time) time,
sum(case when pkg_code = \'IC\' then fee else 0 end) ichat_day,
sum(case when pkg_code = \'IC7\' then fee else 0 end) ichat_week,
sum(case when pkg_code = \'ICALL\' then fee else 0 end) icall_day,
sum(case when pkg_code = \'ICALL7\' then fee else 0 end) icall_week,
sum(case when pkg_code in (\'XSMB\',\'XSMT\',\'XSMN\') then fee else 0 end) xs_day,
sum(case when pkg_code in (\'XSMB7\',\'XSMT7\',\'XSMN7\') then fee else 0 end) xs_week,
sum(case when pkg_code in (\'TKMT\',\'TKMB\',\'TKMN\') then fee else 0 end) tk_day,
sum(case when pkg_code in (\'TKMT7\',\'TKMB7\',\'TKMN7\') then fee else 0 end) tk_week,
sum(case when pkg_code not in (\'TKMT\',\'TKMB\',\'TKMN\',\'TKMT7\',\'TKMB7\',\'TKMN7\',\'XSMB\',\'XSMT\',\'XSMN\',\'XSMB7\',\'XSMT7\',\'XSMN7\',\'IC\',\'IC7\',\'ICALL\',\'ICALL7\') and action not in (\'CALL_IVR\') then fee else 0 end) xst_week,
sum(case when action in (\'CALL_IVR\') then fee else 0 end) call_ivr,
sum(fee) total_fee,
0 accumulated_month,
0 num_charge_sub,
0 same_revenue
from ';
set @sqlt = concat(@sqlt,@table_name,' where result = 0 and fee > 0 and  date(created_time) = \'',@crr_day,'\'');
Prepare stmt FROM @sqlt;
Execute stmt;
DEALLOCATE PREPARE stmt;
set @rrr = 0;

select sum(total_fee) into @rrr from statistic_daily_revenue where time >= @first_day and time <= @crr_day;
update statistic_daily_revenue set accumulated_month = @rrr where time = @crr_day;

select accumulated_month into @rrr from statistic_daily_revenue where time = @same_day;
update statistic_daily_revenue set same_revenue = @rrr where time = @crr_day;

set @sqlt = concat('select count(distinct msisdn) into @rrr from ', @table_name,' where result = 0 and fee > 0 and date(created_time) = @crr_day');
Prepare stmt FROM @sqlt;
Execute stmt;
DEALLOCATE PREPARE stmt;
update statistic_daily_revenue set num_charge_sub = @rrr where time = @crr_day;

END