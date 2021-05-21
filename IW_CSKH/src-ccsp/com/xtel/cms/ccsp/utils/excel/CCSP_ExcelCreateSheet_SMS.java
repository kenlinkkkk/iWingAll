package com.xtel.cms.ccsp.utils.excel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

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
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.textbase.Log4jLoader;
import com.xtel.cms.ccsp.db.orm.CCSPPkgPolicy;
import com.xtel.cms.ccsp.db.orm.CCSPSms;
import com.xtel.cms.ccsp.db.orm.CCSPSubscriber;

public class CCSP_ExcelCreateSheet_SMS {

	private Workbook workbook = null;
	private ArrayList<CCSPSms> listSMS = null;
	private String transid;
	protected static Logger logger = Log4jLoader.getLogger();
	
	public CCSP_ExcelCreateSheet_SMS(String transid, Workbook workbook, ArrayList<CCSPSms> listSMS) {
		this.workbook = workbook;
		this.listSMS = listSMS;
		this.transid = transid;
	}
	 
	public void execute(){
		CreationHelper createHelper = workbook.getCreationHelper();
		// Create a Sheet
		Sheet sheet = workbook.createSheet("Lịch sử SMS"); 

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
		
		String[] columns = { "ID", "Thời gian" , "MO/MT", "Nội dung", "CMD", "TRANSID"};
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
		int rowIndex = 2;
		CellStyle style = workbook.createCellStyle();
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		
		if(listSMS != null) {
			logger.info(transid + ", write SMS: " + listSMS.size());
			int count = 0;
			
			for (CCSPSms sms : listSMS) {
				count ++;
				try {
					Row row = sheet.createRow(rowIndex++);
					int cellIndex = 1;
					Cell cell = null;
					
					cell = row.createCell(cellIndex ++);
					cell.setCellValue(sms.getId());
					cell.setCellStyle(style);
					
					cell = row.createCell(cellIndex ++);
					cell.setCellValue(sms.getCreatedTime());
					cell.setCellStyle(dateCellStyle);
					
					cell = row.createCell(cellIndex ++);
					cell.setCellValue(sms.getType());
					cell.setCellStyle(style);
					
					cell = row.createCell(cellIndex ++);
					cell.setCellValue(sms.getContent());
					cell.setCellStyle(style);
					
					cell = row.createCell(cellIndex ++);
					cell.setCellValue(sms.getCommand());
					cell.setCellStyle(style);
					
					cell = row.createCell(cellIndex ++);
					cell.setCellValue(sms.getTransid());
					cell.setCellStyle(style);
					
				} catch (Exception e) {
					logger.info(transid + ", Exception: " + e.getMessage() + ", index: " + count + ", sms: " + String.valueOf(sms)); 
				}
			}
		}
		// Resize all columns to fit the content size
		for (int i = 0; i < columns.length; i++) {
			sheet.autoSizeColumn(i);
		}
	} 
}