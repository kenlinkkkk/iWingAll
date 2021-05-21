package com.xtel.cms.ccsp.utils.excel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.entities.PairStringInt;
import com.ligerdev.appbase.utils.entities.PairStringLong;
import com.ligerdev.appbase.utils.textbase.Log4jLoader;
import com.xtel.cms.ccsp.db.orm.CCSPActionHis;
import com.xtel.cms.ccsp.db.orm.CCSPPkgPolicy;
import com.xtel.cms.ccsp.db.orm.CCSPSms;
import com.xtel.cms.ccsp.db.orm.CCSPSubscriber;

public class CCSP_ExcelCreateSheet_SubInfo {

	private Workbook workbook = null;
	private ArrayList<CCSPSubscriber> listSubs = null;
	private ArrayList<CCSPPkgPolicy> listPolicy = null;
	private String service;
	private long totalFee;
	private ArrayList<PairStringLong> totalFeeByPack;
	protected static Logger logger = Log4jLoader.getLogger();
	
	public CCSP_ExcelCreateSheet_SubInfo(Workbook workbook, ArrayList<CCSPSubscriber> listSubs,
			ArrayList<CCSPPkgPolicy> listPolicy, String service, long totalFee,  ArrayList<PairStringLong> totalFeeByPack) {
		
		this.workbook = workbook;
		this.listSubs = listSubs;
		this.listPolicy = listPolicy;
		this.service = service;
		this.totalFee = totalFee;
		this.totalFeeByPack = totalFeeByPack;
	}
	 
	public void execute(){
		CreationHelper createHelper = workbook.getCreationHelper();
		// Create a Sheet
		Sheet sheet = workbook.createSheet("Thông tin chung"); 

		// Create a Font for styling header cells
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 12);
		headerFont.setColor(IndexedColors.BLUE.getIndex());
		
		// Create header
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);
		headerCellStyle.setBorderBottom(BorderStyle.THIN);
		headerCellStyle.setBorderTop(BorderStyle.THIN);
		headerCellStyle.setBorderRight(BorderStyle.THIN);
		headerCellStyle.setBorderLeft(BorderStyle.THIN);
		String[] columns = {"MSISDN", "Mã gói" , "Mô tả gói", "Giá gói", "Trạng thái", "Ngày ĐK", "Kênh ĐK"};
		
		Row headerRow = sheet.createRow(1);
		for (int i = 1; i <= columns.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(columns[i-1]);
			cell.setCellStyle(headerCellStyle);
		}
		// Create Cell Style for formatting Date
		CellStyle dateCellStyle = workbook.createCellStyle();
		dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("HH:mm dd/MM/yyyy"));
		dateCellStyle.setBorderTop(BorderStyle.THIN);
		dateCellStyle.setBorderBottom(BorderStyle.THIN);
		dateCellStyle.setBorderLeft(BorderStyle.THIN);
		dateCellStyle.setBorderRight(BorderStyle.THIN);
		
		// Create Other rows and cells with employees data
		CellStyle style = workbook.createCellStyle();
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		
		int rowIndex = 2;
		Row row = null;
		Cell cell = null;
		
		if(listSubs != null) {
			for (CCSPSubscriber sub : listSubs) {
				CCSPPkgPolicy p = getPolicy(sub.getPackageId());
				int cellIndex = 1;
				row = sheet.createRow(rowIndex++);
				
				/*cell = row.createCell(cellIndex ++);
				cell.setCellValue(service);
				cell.setCellStyle(style);*/
				
				cell = row.createCell(cellIndex ++);
				cell.setCellValue(BaseUtils.formatMsisdn_0(sub.getMsisdn()));
				cell.setCellStyle(style);
				
				cell = row.createCell(cellIndex ++);
				cell.setCellValue(sub.getPackageId());
				cell.setCellStyle(style);
				
				cell = row.createCell(cellIndex ++);
				cell.setCellValue(p == null ? "" : p.getDesc());
				cell.setCellStyle(style);
				
				cell = row.createCell(cellIndex ++);
				cell.setCellValue(p == null ? "" : p.getFeeDesc());
				cell.setCellStyle(style);
				
				cell = row.createCell(cellIndex ++);
				cell.setCellValue(sub.getStatus() == 1 ? "Đang active" : "Đã hủy");
				cell.setCellStyle(style);
				
				cell = row.createCell(cellIndex ++);
				cell.setCellValue(sub.getActiveTime()); 
				cell.setCellStyle(dateCellStyle);
				
				cell = row.createCell(cellIndex ++);
				cell.setCellValue(sub.getActiveChannel());
				cell.setCellStyle(style);
			}
		}
		row = sheet.createRow(rowIndex++);
		//---------
		
		row = sheet.createRow(rowIndex++);
		cell = row.createCell(1);
		cell.setCellValue("Tổng cước " + service);
		cell.setCellStyle(style);
		
		cell = row.createCell(2);
		cell.setCellValue(BaseUtils.cashFormat(totalFee)); 
		cell.setCellStyle(style);
		
		if(totalFeeByPack != null && totalFeeByPack.size() >= 2) {
			for(PairStringLong p : totalFeeByPack) {
				row = sheet.createRow(rowIndex++);
				cell = row.createCell(1);
				cell.setCellValue("Tổng cước gói " + p.getName());
				cell.setCellStyle(style);
				
				cell = row.createCell(2);
				cell.setCellValue(BaseUtils.cashFormat(p.getValue()));  
				cell.setCellStyle(style);
			}
		}
		// Resize all columns to fit the content size
		for (int i = 0; i < columns.length; i++) {
			sheet.autoSizeColumn(i);
		}
	}
	
	public static void main(String[] args) throws IOException { 
		// String[] columns = { "MSISDN", "Mã gói" , "Mô tả gói", "Giá gói", "Trạng thái", "Ngày ĐK", "Kênh ĐK"};
		
		org.apache.xmlbeans.XmlObject checkLib;
		org.apache.commons.collections4.ListValuedMap checkLib2;
		org.apache.commons.compress.archivers.zip.ZipFile checkLib3;
		
		ArrayList<CCSPSubscriber> listSub = new ArrayList<CCSPSubscriber>();
		ArrayList<CCSPPkgPolicy> listPkg = new ArrayList<CCSPPkgPolicy>();
		
		{
			CCSPSubscriber sub = new CCSPSubscriber();
			sub.setMsisdn("84909090529");
			sub.setPackageId("NGAY");
			sub.setStatus(1);
			sub.setActiveChannel("SMS");
			sub.setActiveTime(new Date());
			listSub.add(sub);
		}
		{
			CCSPSubscriber sub = new CCSPSubscriber();
			sub.setMsisdn("84909090529");
			sub.setPackageId("THANG");
			sub.setActiveChannel("SMS");
			sub.setStatus(1);
			sub.setActiveTime(new Date());
			listSub.add(sub);
		}
		{
			CCSPPkgPolicy p = new CCSPPkgPolicy();
			p.setCode("NGAY");
			p.setDesc("Gói ngày");
			p.setFeeDesc("2.000đ/ngày");
			listPkg.add(p);
		}
		{
			CCSPPkgPolicy p = new CCSPPkgPolicy();
			p.setCode("THANG");
			p.setDesc("Gói tháng");
			p.setFeeDesc("10.000đ/ngày");
			listPkg.add(p);
		}
		Workbook workbook = new XSSFWorkbook();
		CCSP_ExcelCreateSheet_SubInfo excel1 = new CCSP_ExcelCreateSheet_SubInfo(workbook, listSub, listPkg, "", 0, null);
		excel1.execute();
		
		
		ArrayList<CCSPSms> listCCSPSms = new ArrayList<CCSPSms>();
		listCCSPSms.add(new CCSPSms(1, "MO", "84909090529", new Date(), "INV", "DK", "1231"));
		listCCSPSms.add(new CCSPSms(2, "MT", "84909090529", new Date(), "INV", "Chúc mừng QK đã đăng ký thành công dịch vụ abc", "345"));
		listCCSPSms.add(new CCSPSms(3, "MO", "84909090529", new Date(), "INV", "HUY NGAY", "234"));
		listCCSPSms.add(new CCSPSms(4, "MT", "84909090529", new Date(), "INV", "QK đã hủy thành công dv ABC", "5675"));
		CCSP_ExcelCreateSheet_SMS excel2 = new CCSP_ExcelCreateSheet_SMS("test", workbook, listCCSPSms);
		excel2.execute();
		
		ArrayList<CCSPActionHis> listCCSPAction = new ArrayList<CCSPActionHis>();
		listCCSPAction.add(new CCSPActionHis(1, "84909090529", "FirstREG", new Date(), 0, "123123", null, "SMS", 0, "NGAY", "SMS", null, null));
		listCCSPAction.add(new CCSPActionHis(2, "84909090529", "RENEW", new Date(), 2000, "123123", null, "SMS", 0, "NGAY", "SMS", null, null));
		listCCSPAction.add(new CCSPActionHis(3, "84909090529", "RENEW", new Date(), 2000, "123123", null, "SMS", 0, "NGAY", "SMS", null, null));
		listCCSPAction.add(new CCSPActionHis(4, "84909090529", "UNREG", new Date(), 0, "123123", null, "SMS", 0, "NGAY", "SMS", null, null));
		listCCSPAction.add(new CCSPActionHis(4, "84909090529", "ReREG", new Date(), 2000, "123123", null, "SMS", 0, "THANG", "SMS", null, null));
		CCSP_ExcelCreateSheet_ActionHis excel3 = new CCSP_ExcelCreateSheet_ActionHis(workbook, listCCSPAction);
		excel3.execute();
		
		// Write the output to a file
		FileOutputStream fileOut = new FileOutputStream("poi-generated-file.xlsx");
		workbook.write(fileOut);
		try {
			fileOut.close();
		} catch (Exception e) {}
		try {
			workbook.close();
		} catch (Exception e) {}
	}
	
	private CCSPPkgPolicy getPolicy(String pkgCode) {
		if(listPolicy == null) {
			return null;
		}
		for(CCSPPkgPolicy p : listPolicy) {
			if(p.getCode().equalsIgnoreCase(pkgCode)) {
				return p;
			}
		}
		return null;
	}
}