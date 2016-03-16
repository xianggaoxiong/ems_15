package com.atguigu.ems.test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

public class POITest {

	//边框相关. 
	@Test
	public void testBorder() throws IOException{
		Workbook wb = new HSSFWorkbook();
	    Sheet sheet = wb.createSheet("new sheet");

	    // Create a row and put some cells in it. Rows are 0 based.
	    Row row = sheet.createRow(1);

	    // Create a cell and put a value in it.
	    Cell cell = row.createCell(1);
	    cell.setCellValue(4);

	    // Style the cell with borders all around.
	    CellStyle style = wb.createCellStyle();
	    style.setBorderBottom(CellStyle.BORDER_THIN);
	    style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
	    style.setBorderLeft(CellStyle.BORDER_THIN);
	    style.setLeftBorderColor(IndexedColors.GREEN.getIndex());
	    style.setBorderRight(CellStyle.BORDER_THIN);
	    style.setRightBorderColor(IndexedColors.BLUE.getIndex());
	    style.setBorderTop(CellStyle.BORDER_MEDIUM_DASHED);
	    style.setTopBorderColor(IndexedColors.BLACK.getIndex());
	    cell.setCellStyle(style);

	    // Write the output to a file
	    FileOutputStream fileOut = new FileOutputStream("e:\\workbook.xls");
	    wb.write(fileOut);
	    fileOut.close();

	}
	
	//Cell 中内容的对齐方式
	@Test
	public void testAlignment() throws IOException {
		Workbook wb = new XSSFWorkbook(); // or new HSSFWorkbook();

		Sheet sheet = wb.createSheet();
		Row row = sheet.createRow((short) 2);
		row.setHeightInPoints(30);

		createCell(wb, row, (short) 0, XSSFCellStyle.ALIGN_CENTER,
				XSSFCellStyle.VERTICAL_BOTTOM);
		createCell(wb, row, (short) 1, XSSFCellStyle.ALIGN_CENTER_SELECTION,
				XSSFCellStyle.VERTICAL_BOTTOM);
		createCell(wb, row, (short) 2, XSSFCellStyle.ALIGN_FILL,
				XSSFCellStyle.VERTICAL_CENTER);
		createCell(wb, row, (short) 3, XSSFCellStyle.ALIGN_GENERAL,
				XSSFCellStyle.VERTICAL_CENTER);
		createCell(wb, row, (short) 4, XSSFCellStyle.ALIGN_JUSTIFY,
				XSSFCellStyle.VERTICAL_JUSTIFY);
		createCell(wb, row, (short) 5, XSSFCellStyle.ALIGN_LEFT,
				XSSFCellStyle.VERTICAL_TOP);
		createCell(wb, row, (short) 6, XSSFCellStyle.ALIGN_RIGHT,
				XSSFCellStyle.VERTICAL_TOP);

		// Write the output to a file
		FileOutputStream fileOut = new FileOutputStream("e:\\xssf-align.xlsx");
		wb.write(fileOut);
		fileOut.close();

	}

	private static void createCell(Workbook wb, Row row, short column,
			short halign, short valign) {
		Cell cell = row.createCell(column);
		cell.setCellValue(new XSSFRichTextString("Align It"));
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(halign);
		cellStyle.setVerticalAlignment(valign);
		cell.setCellStyle(cellStyle);
	}

	// 不能直接把 Date 类型设置为 Cell 的 value. 而需要借助于 CellStyle
	@Test
	public void testDateCell() throws IOException {
		Workbook wb = new HSSFWorkbook();
		// Workbook wb = new XSSFWorkbook();
		CreationHelper createHelper = wb.getCreationHelper();
		Sheet sheet = wb.createSheet("new sheet");

		// Create a row and put some cells in it. Rows are 0 based.
		Row row = sheet.createRow(0);

		// Create a cell and put a date value in it. The first cell is not
		// styled
		// as a date.
		Cell cell = row.createCell(0);
		cell.setCellValue(new Date());

		// we style the second cell as a date (and time). It is important to
		// create a new cell style from the workbook otherwise you can end up
		// modifying the built in style and effecting not only this cell but
		// other cells.
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setDataFormat(createHelper.createDataFormat().getFormat(
				"m/d/yy h:mm"));
		cell = row.createCell(1);
		cell.setCellValue(new Date());
		cell.setCellStyle(cellStyle);

		// you can also set date as java.util.Calendar
		cell = row.createCell(2);
		cell.setCellValue(Calendar.getInstance());
		cell.setCellStyle(cellStyle);

		// Write the output to a file
		FileOutputStream fileOut = new FileOutputStream("e:\\workbook.xls");
		wb.write(fileOut);
		fileOut.close();
	}

	// Row 代表 Excel 文档的一行.
	// Cell 代表一个单元格.
	@Test
	public void testCreateCell() throws IOException {
		Workbook wb = new HSSFWorkbook();
		// Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet("new sheet");

		// Create a row and put some cells in it. Rows are 0 based.
		Row row = sheet.createRow((short) 0);
		// Create a cell and put a value in it.
		Cell cell = row.createCell(0);
		cell.setCellValue(1);

		// Or do it on one line.
		row.createCell(1).setCellValue(1.2);
		row.createCell(2).setCellValue("This is a string");
		row.createCell(3).setCellValue(true);

		// Write the output to a file
		FileOutputStream fileOut = new FileOutputStream("e:\\workbook.xls");
		wb.write(fileOut);
		fileOut.close();

	}

	// Sheet 即为工作表
	@Test
	public void testNewSheet() throws IOException {
		Workbook wb = new HSSFWorkbook();
		// Workbook wb = new XSSFWorkbook();
		Sheet sheet1 = wb.createSheet("new sheet");
		Sheet sheet2 = wb.createSheet("second sheet");

		FileOutputStream fileOut = new FileOutputStream("e:\\workbook.xls");
		wb.write(fileOut);
		fileOut.close();
	}

	// Workbook 即为 Excel 文档.
	@Test
	public void testNewWorkbook() throws IOException {
		Workbook wb = new HSSFWorkbook();
		FileOutputStream fileOut = new FileOutputStream("e:\\workbook.xls");
		wb.write(fileOut);
		fileOut.close();
	}

}
