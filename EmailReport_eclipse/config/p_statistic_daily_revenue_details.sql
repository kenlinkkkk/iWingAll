CREATE PROCEDURE `p_statistic_daily_revenue_details`(p_date datetime)
BEGIN

	set @crr_day = date_format(p_date,'%Y%m%d');
	
	delete from statistic_daily_revenue_details where time = @crr_day;
	set @sqlt = 'insert into statistic_daily_revenue_details select 
time,
pkg_code_01,
sum(total_active),
sum(total_register),
sum(reg_sms),
sum(reg_ivr),
sum(reg_wap),
sum(reg_avb),
sum(total_unregister),
sum(total_charge_call_success),
sum(revenue_call_success),
sum(total_charge_subs),
sum(total_charge_subs_success),
sum(ration),
sum(revenue_subs),
sum(revenue_total) from (
select @crr_day time,
(case when package_id not in (\'TKMT\',\'TKMB\',\'TKMN\',\'TKMT7\',\'TKMB7\',\'TKMN7\',\'XSMB\',\'XSMT\',\'XSMN\',\'XSMB7\',\'XSMT7\',\'XSMN7\',\'IC\',\'IC7\',\'ICALL\',\'ICALL7\') then \'XSTN\' 
	when package_id in (\'TKMT\',\'TKMB\',\'TKMN\') then \'TKN\'
	when package_id in (\'TKMT7\',\'TKMB7\',\'TKMN7\') then \'TKT\'
	when package_id in (\'XSMB\',\'XSMT\',\'XSMN\') then \'XSN\'
	when package_id in (\'XSMB7\',\'XSMT7\',\'XSMN7\') then \'XST\'
	else package_id end) 
pkg_code_01,
count(*) total_active,
0 total_register,
0 reg_sms,
0 reg_ivr,
0 reg_avb,
0 reg_wap,
0 total_unregister,
0 total_charge_call_success,
0 revenue_call_success,
0 total_charge_subs,
0 total_charge_subs_success,
0 ration,
0 revenue_subs,
0 revenue_total
from subscriber where status = 1 group by pkg_code_01
union
select @crr_day time,
(case when code not in (\'TKMT\',\'TKMB\',\'TKMN\',\'TKMT7\',\'TKMB7\',\'TKMN7\',\'XSMB\',\'XSMT\',\'XSMN\',\'XSMB7\',\'XSMT7\',\'XSMN7\',\'IC\',\'IC7\',\'ICALL\',\'ICALL7\') then \'XSTN\' 
	when code in (\'TKMT\',\'TKMB\',\'TKMN\') then \'TKN\'
	when code in (\'TKMT7\',\'TKMB7\',\'TKMN7\') then \'TKT\'
	when code in (\'XSMB\',\'XSMT\',\'XSMN\') then \'XSN\'
	when code in (\'XSMB7\',\'XSMT7\',\'XSMN7\') then \'XST\'
	else code end) 
pkg_code_01,
0 total_active,
0 total_register,
0 reg_sms,
0 reg_ivr,
0 reg_avb,
0 reg_wap,
0 total_unregister,
0 total_charge_call_success,
0 revenue_call_success,
0 total_charge_subs,
0 total_charge_subs_success,
0 ration,
0 revenue_subs,
0 revenue_total
from pkg_policy where status = 1 group by pkg_code_01
union
select @crr_day time,
(case when pkg_code not in (\'TKMT\',\'TKMB\',\'TKMN\',\'TKMT7\',\'TKMB7\',\'TKMN7\',\'XSMB\',\'XSMT\',\'XSMN\',\'XSMB7\',\'XSMT7\',\'XSMN7\',\'IC\',\'IC7\',\'ICALL\',\'ICALL7\') then \'XSTN\' 
	when pkg_code in (\'TKMT\',\'TKMB\',\'TKMN\') then \'TKN\'
	when pkg_code in (\'TKMT7\',\'TKMB7\',\'TKMN7\') then \'TKT\'
	when pkg_code in (\'XSMB\',\'XSMT\',\'XSMN\') then \'XSN\'
	when pkg_code in (\'XSMB7\',\'XSMT7\',\'XSMN7\') then \'XST\'
	else pkg_code end) 
pkg_code_01,
0 total_active,
sum(case when rs_code = \'CPS-0000\' and reason in (\'FirstREG\',\'ReREG\') then counter  else 0 end) total_register,
sum(case when rs_code = \'CPS-0000\' and reason in (\'FirstREG\',\'ReREG\') and group_cpid in (\'SMS\') then counter  else 0 end) reg_sms,
sum(case when rs_code = \'CPS-0000\' and reason in (\'FirstREG\',\'ReREG\') and group_cpid in (\'IVR\') then counter  else 0 end) reg_ivr,
sum(case when rs_code = \'CPS-0000\' and reason in (\'FirstREG\',\'ReREG\') and group_cpid in (\'VBC\') then counter  else 0 end) reg_avb,
sum(case when rs_code = \'CPS-0000\' and reason in (\'FirstREG\',\'ReREG\') and group_cpid not in (\'SMS\',\'IVR\',\'USSD\',\'WCC\',\'VBC\') then counter  else 0 end) reg_wap,
sum(case when reason in (\'UNREG\',\'DELETE\') then counter  else 0 end) total_unregister,
sum(case when rs_code = \'CPS-0000\' and reason in (\'CALL_IVR\') then counter  else 0 end) total_charge_call_success,
sum(case when rs_code = \'CPS-0000\' and reason in (\'CALL_IVR\') then counter * fee  else 0 end) revenue_call_success,
sum(case when load_number < 6 and fee > 0 and reason not in (\'CALL_IVR\') then counter  else 0 end) total_charge_subs,
sum(case when rs_code = \'CPS-0000\' and fee > 0 and reason not in (\'CALL_IVR\') then counter else 0 end) total_charge_subs_success,
0 ration,
sum(case when rs_code = \'CPS-0000\' and reason not in (\'CALL_IVR\') then counter * fee else 0 end) revenue_subs,
sum(case when rs_code = \'CPS-0000\' then counter * fee  else 0 end) revenue_total  
from stats_rscode where @crr_day = created_time group by pkg_code_01
) a group by time,pkg_code_01';
Prepare stmt FROM @sqlt;
Execute stmt;
DEALLOCATE PREPARE stmt;
update statistic_daily_revenue_details set ratio = (case when total_charge_sub > 0 then  round((charge_sub_success/total_charge_sub)*100,2) else 0 end) where date(time) = date(p_date);
END