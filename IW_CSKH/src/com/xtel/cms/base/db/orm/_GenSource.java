package com.xtel.cms.base.db.orm;

import com.ligerdev.appbase.utils.db.GenDbSource;

public class _GenSource {
 
	public static void main(String[] args) {
		String sql = "SELECT m.*, dvt.name vt_name, dvt.link vt_link, mb.name mb_name, mb.link mb_link,  " + 
						"vnp.name vnp_name, vnp.link vnp_link, vnm.name vnm_name, vnm.link vnm_link, " + 
					    "oth.name oth_name, oth.link oth_link " + 
					"FROM  link_map m  " + 
						"left join link_dest dvt on m.dest_viettel = dvt.id  " + 
						"left join link_dest mb on m.dest_mobi = mb.id  " + 
					    "left join link_dest vnp on m.dest_vina = vnp.id  " + 
						"left join link_dest vnm on m.dest_vnm = vnm.id  " + 
					    "left join link_dest oth on m.dest_other = oth.id " + 
					"where m.status = 1";
		
		sql = "select s.name servicename, s.desc1 servicedesc, l.* from xjsch_link l inner join xjsch_service s on s.id = l.service_id";
		GenDbSource.genDTO("main", "xjsch_link_ext", sql, "id", "com.xtel.cms.db.orm");
		System.exit(9);
	}
}
