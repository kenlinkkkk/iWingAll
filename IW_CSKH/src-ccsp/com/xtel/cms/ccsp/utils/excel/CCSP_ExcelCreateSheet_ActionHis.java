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
import com.xtel.cms.ccsp.db.orm.CCSPActionHis;
import com.xtel.cms.ccsp.db.orm.CCSPPkgPolicy;
import com.xtel.cms.ccsp.db.orm.CCSPSms;
import com.xtel.cms.ccsp.db.orm.CCSPSubscriber;

public class CCSP_ExcelCreateSheet_ActionHis {

	private Workbook workbook = null;
	private ArrayList<CCSPActionHis> listAction = null;
	protected static Logger logger = Log4jLoader.getLogger();
	
	public CCSP_ExcelCreateSheet_ActionHis(Workbook workbook, ArrayList<CCSPActionHis> listAction) {
		this.workbook = workbook;
		this.listAction = listAction;
	}
	 
	public void execute(){
		CreationHelper createHelper = workbook.getCreationHelper();
		// Create a Sheet
		Sheet sheet = workbook.createSheet("Lịch sử giao dịch"); 

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
		
		String[] columns = { "ID", "Thời gian" , "TYPE", "Giá tiền", "Mã Gói", "Kênh", "TRANSID"};
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
		
		if(listAction != null) {
			for (CCSPActionHis a : listAction) {
				// {"ID", "Thời gian" , "Loại", "Giá tiền", "Mã Gói", "TRANSID"};
				Row row = sheet.createRow(rowIndex++);
				int cellIndex = 1;
				Cell cell = null;
				
				cell = row.createCell(cellIndex ++);
				cell.setCellValue(a.getId());
				cell.setCellStyle(style);
				
				cell = row.createCell(cellIndex ++);
				cell.setCellValue(a.getCreatedTime());
				cell.setCellStyle(dateCellStyle);
				
				cell = row.createCell(cellIndex ++);
				String action = a.getAction();
				String channel = a.getChannel();
				
				if("UNREG".equalsIgnoreCase(action) 
						|| "DELETE".equalsIgnoreCase(action)) {
					action = "Hủy";
				} 
				else if("RENEW".equalsIgnoreCase(action)) { 
					action = "Gia hạn";
					channel = "SYSTEM";
				} 
				else if("FirstREG".equalsIgnoreCase(action)
						|| "ReREG".equalsIgnoreCase(action)) {
					action = "Đăng ký";
				}
				channel = "WCC".equalsIgnoreCase(channel) ? "CSKH" : channel;
				
				cell.setCellValue(action);
				cell.setCellStyle(style);
				
				cell = row.createCell(cellIndex ++);
				cell.setCellValue(a.getFee());
				cell.setCellStyle(style);
				
				cell = row.createCell(cellIndex ++);
				cell.setCellValue(a.getPkgCode());
				cell.setCellStyle(style);
				
				cell = row.createCell(cellIndex ++);
				cell.setCellValue(channel);
				cell.setCellStyle(style);
				
				cell = row.createCell(cellIndex ++);
				cell.setCellValue(a.getTransId());
				cell.setCellStyle(style);
			}
		}
		// Resize all columns to fit the content size
		for (int i = 0; i < columns.length; i++) {
			sheet.autoSizeColumn(i);
		}
	} 
}