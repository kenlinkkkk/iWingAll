package com.xtel.cms.ccsp.form.subs_info;

import com.ligerdev.appbase.utils.BaseUtils;

public class _TestMain {

	public static void main(String[] args) {
		String rs = "CTS_HUY_C30_NOT_SUCC: Yêu cầu không thành công do Quý khách chưa đăng ký gói tháng dịch vụ Gameshow CatTienSaPay. Để đăng ký, soạn DK C30 gửi 9556 (30.000đ/30 ngày). Để hủy dịch vụ, soạn HUY C30 gửi 9556. Chi tiết soạn HD gửi 9556 , truy cập http://CatTienSaPlay.vn  hoặc liên hệ 9090 (200đ/phút). Trân trọng cảm ơn!";
		rs = " CTS_HUY_C30_NOT_SUCC: Y&#234;u c&#7847;u kh&#244;ng th&#224;nh c&#244;ng do Qu&#253; kh&#225;ch ch&#432;a &#273;&#259;ng k&#253; g&#243;i th&#225;ng d&#7883;ch v&#7909; Gameshow CatTienSaPay. &#272;&#7875; &#273;&#259;ng k&#253;, so&#7841;n DK C30 g&#7917;i 9556 (30.000&#273;/30 ng&#224;y). &#272;&#7875; h&#7911;y d&#7883;ch v&#7909;, so&#7841;n HUY C30 g&#7917;i 9556. Chi ti&#7871;t so&#7841;n HD g&#7917;i 9556 , truy c&#7853;p http://CatTienSaPlay.vn  ho&#7863;c li&#234;n h&#7879; 9090 (200&#273;/ph&#250;t). Tr&#226;n tr&#7885;ng c&#7843;m &#417;n!";
		rs = BaseUtils.unescapeHtml(rs);
		System.out.println(rs);
		
		if(		(rs.contains("_SUCC") && rs.contains("NOT_SUCC") == false) 		// call tới ccsp, hủy thành công
				|| rs.startsWith("0|")  // call tới api, hủy thành công
				|| rs.startsWith("1|")  // call tới api, api báo chưa dk
				|| (rs.contains("NOT_SUCC") && rs.contains("chưa đăng ký"))
		 ){  
			System.out.println("OK");
		} else {
			System.out.println("NOK");
		}
	}
}
