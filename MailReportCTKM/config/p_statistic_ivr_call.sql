CREATE PROCEDURE `p_statistic_ivr_call`(p_date datetime)
BEGIN
  #Routine body goes here...
	set @table_name = concat('icall_cdr_call_',DATE_FORMAT(p_date,'%Y%m'));
	set @crr_day = DATE_FORMAT(p_date,'%Y-%m-%d');
	
delete from statistic_ivr_call where time = @crr_day;

SET @rows = 0;

-- tinh so luong tb goi den va thue bao goi den co tinh phi
set @sqlt = concat('select count(*) into @rows from ',@table_name,' where date(calltime) = @crr_day');
Prepare stmt FROM @sqlt;
Execute stmt;
DEALLOCATE PREPARE stmt;

if @rows <= 0 then 
	insert into statistic_ivr_call(time) values (@crr_day);
else
set @sqlt = 'insert into statistic_ivr_call
select date(calltime) time,sum(duration) duration_call,(sum(duration-duration_charge)) duration_call_free, sum(duration_charge) duration_call_fee from ';
set @sqlt = concat(@sqlt,@table_name,' where date(calltime) = \'',@crr_day,'\'');
Prepare stmt FROM @sqlt;
Execute stmt;
DEALLOCATE PREPARE stmt;
end if;
END