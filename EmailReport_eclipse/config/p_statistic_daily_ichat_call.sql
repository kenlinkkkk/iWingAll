CREATE PROCEDURE `p_statistic_daily_ichat_call`(p_date datetime)
BEGIN
  #Routine body goes here...
	set @table_name = concat('icall_cdr_call_',DATE_FORMAT(p_date,'%Y%m'));
	set @crr_day = DATE_FORMAT(p_date,'%Y-%m-%d');
	set @first_day = DATE_FORMAT(p_date,'%Y-%m-01');
	
delete from statistic_daily_ichat_call where time = @crr_day;

SET @rows = 0;

-- tinh so luong tb goi den va thue bao goi den co tinh phi
set @sqlt = concat('select count(*) into @rows from ',@table_name,' where date(calltime) = @crr_day and registered in (2,3)');
Prepare stmt FROM @sqlt;
Execute stmt;
DEALLOCATE PREPARE stmt;

if @rows <= 0 then 
	insert into statistic_daily_ichat_call(time) values (@crr_day);
else
set @sqlt = 'insert into statistic_daily_ichat_call
select  
date(calltime) time,
0 num_call_ichat,
0 revenue_sub,
0 num_msisdn_call_fee, 
count(*) total_call_in, 
sum(case when INSTR(note,\'[ichat-mobile-connect|\') > 0 then 1 else 0 end) total_call_out, 
sum(case when INSTR(note,\'[ichat-mobile-answer|\') > 0 then 1 else 0 end) total_call_out_connected, 
sum(duration) total_duration_call, 
sum(f_calc_call_out_time(note)) total_duration_call_out, 
sum(duration_calculated_ichat_fee) total_duration_call_answered, 
sum(duration_charge_ichat) total_duration_call_fee, 
sum(ceiling(duration_charge_ichat/60)) total_duration_call_fee_block, 
sum(charge_fee_ichat) total_fee,
0 num_ichat_reg,
0 num_ichat_unreg,
0 num_ichat_active,
0 total_revenue,
0 accumulated_month 
from ';
set @sqlt = concat(@sqlt,@table_name,' where registered in (2,3) and date(calltime) = \'',@crr_day,'\'');
Prepare stmt FROM @sqlt;
Execute stmt;
DEALLOCATE PREPARE stmt;
end if;


set @aa = 0;
set @a1 = 0;

-- tinh so luong tb goi den va thue bao goi den co tinh phi
set @sqlt = concat('select count(*), sum(case when fee > 0 then 1 else 0 end) a1 into @aa,@a1 from (select msisdn,sum(charge_fee_ichat) fee from ',@table_name,' where date(calltime) = @crr_day and registered in (2,3) group by msisdn) a');
Prepare stmt FROM @sqlt;
Execute stmt;
DEALLOCATE PREPARE stmt;

-- truong hop a1 bi null
if @a1 is null then
	set @a1 = 0;
end if;

update statistic_daily_ichat_call set num_call_ichat = @aa,num_msisdn_call_fee = @a1 where time = @crr_day;

-- thong ke so luong dang ky huy goi ichat
set @active = 0;
set @reg = 0;
set @unreg = 0;
set @revenue = 0;
set @total_revenue = 0;

select sum(case when reason in ('ReREG','REG','FirstREG') and rs_code = 'CPS-0000' then counter else 0 end ),
sum(case when reason in ('DELETE','UNREG') then counter else 0 end ), 
sum(case when reason != 'CALL_IVR' and rs_code = 'CPS-0000' then fee * counter else 0 end),
sum(case when rs_code = 'CPS-0000' then fee * counter else 0 end)
into @reg,@unreg,@revenue,@total_revenue  
from stats_rscode where date(created_time) = @crr_day and pkg_code in ('IC','IC7')  order by created_time desc;

select count(*) into @active from subscriber where status = 1 and package_id in ('IC','IC7');

update statistic_daily_ichat_call set num_ichat_reg = @reg,num_ichat_unreg = @unreg,revenue_sub = @revenue, num_ichat_active = @active, total_revenue = @total_revenue where time = @crr_day;

 -- doanh thu luy ke
set @rrr = 0;
select sum(total_revenue) into @rrr from statistic_daily_ichat_call where time >= @first_day and time <= @crr_day;
update statistic_daily_ichat_call set accumulated_month = @rrr where time = @crr_day;

READS SQL DATA
DETERMINISTI
END