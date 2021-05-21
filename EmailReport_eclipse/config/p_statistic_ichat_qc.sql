CREATE PROCEDURE `p_statistic_ichat_qc`(p_date datetime)
BEGIN
	set @t1 = concat('qc_',DATE_FORMAT(p_date,'%Y%m'));
	set @t2 = concat('his_',DATE_FORMAT(p_date,'%Y%m'));
	set @t3 = concat('mo_',DATE_FORMAT(p_date,'%Y%m'));
	set @crr_day = DATE_FORMAT(p_date,'%Y-%m-%d');
	set @crr_day_1 = concat(DATE_FORMAT(p_date,'%Y-%m-%d'),' 23:59:59');
	
delete from statistic_ichat_qc where time = @crr_day;

	set @total_sent = 0;
	set @total_reply = 0;
	set @total_registered = 0;
	set @mt = "";

	set @sqlt = concat('select content into @mt from ',@t1,' where created_time >= \'',@crr_day,'\' and created_time <= \'',@crr_day_1,'\' and (content like \'%GD den 1522%\' or content like \'%GD gui 1522%\') limit 1');

	Prepare stmt FROM @sqlt;
Execute stmt;
DEALLOCATE PREPARE stmt;

set @sqlt = concat('select sum(total_send),sum(total_reply),sum(total_registred) into @total_sent,@total_reply,@total_registered from (
select count(*) total_send,0 total_reply,0 total_registred from ',@t1,' where created_time >= \'',@crr_day,'\' and created_time <= \'',@crr_day_1,'\' and (content like \'%GD den 1522%\' or content like \'%GD gui 1522%\')
union
select 0 total_send,count(*) total_reply, 0  total_registred from ',@t1,' qc inner join ',@t3,' m on m.msisdn = qc.msisdn and m.created_time >= \'',@crr_day,'\' and m.created_time <= \'',@crr_day_1,'\' where qc.created_time >= \'',@crr_day,'\' and qc.created_time <= \'',@crr_day_1,'\' and (qc.content like \'%GD den 1522%\' or qc.content like \'%GD gui 1522%\')
union
select 0 total_send,0 total_reply, count(*) total_registred from ',@t1,' qc inner join ',@t2,' m on m.msisdn = qc.msisdn and m.created_time >= \'',@crr_day,'\' and m.created_time <= \'',@crr_day_1,'\' and m.action in (\'ReREG\',\'FirstREG\') where qc.created_time >= \'',@crr_day,'\' and qc.created_time <= \'',@crr_day_1,'\' and (qc.content like \'%GD den 1522%\' or qc.content like \'%GD gui 1522%\') and m.channel = \'SMS\' and pkg_code in (\'IC\',\'IC7\')
) a');

Prepare stmt FROM @sqlt;
Execute stmt;
DEALLOCATE PREPARE stmt;


	insert into statistic_ichat_qc values(@crr_day,@total_sent,@total_reply,@total_registered,@mt);

END