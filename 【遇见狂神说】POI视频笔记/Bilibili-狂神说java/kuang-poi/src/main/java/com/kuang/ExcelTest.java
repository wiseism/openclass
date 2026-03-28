package com.kuang;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class ExcelTest {

	String path = "D:\\狂神说Java\\【狂神】小专题\\POI-EasyExcel\\Bilibili-狂神说java\\kuang-poi\\";

	@Test
	public void testFormula() throws Exception{

		InputStream is = new FileInputStream(path + "计算公式.xls");

		Workbook workbook = new HSSFWorkbook(is);
		Sheet sheet = workbook.getSheetAt(0);

		// 读取第五行第一列
		Row row = sheet.getRow(4);
		Cell cell = row.getCell(0);

		//公式计算器
		FormulaEvaluator formulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) workbook);

		// 输出单元内容
		int cellType = cell.getCellType();
		switch (cellType) {
			case Cell.CELL_TYPE_FORMULA://2

				//得到公式
				String formula = cell.getCellFormula();
				System.out.println(formula);

				CellValue evaluate = formulaEvaluator.evaluate(cell);
				//String cellValue = String.valueOf(evaluate.getNumberValue());
				String cellValue = evaluate.formatAsString();
				System.out.println(cellValue);

				break;
		}
	}

	@Test
	public void testRead03() throws Exception{
		InputStream is = new FileInputStream(path+"狂神观众统计表03.xls");

		Workbook workbook = new HSSFWorkbook(is);
		Sheet sheet = workbook.getSheetAt(0);

		// 读取第一行第一列
		Row row = sheet.getRow(0);
		Cell cell = row.getCell(0);

		// 输出单元内容
		System.out.println(cell.getStringCellValue());

		// 操作结束，关闭文件
		is.close();
	}

	@Test
	public void testRead07() throws Exception{
		InputStream is = new FileInputStream(path+"/狂神观众统计表07.xlsx");

		Workbook workbook = new XSSFWorkbook(is);
		Sheet sheet = workbook.getSheetAt(0);

		// 读取第一行第一列
		Row row = sheet.getRow(0);
		Cell cell = row.getCell(0);

		// 输出单元内容
		System.out.println(cell.getStringCellValue());

		// 操作结束，关闭文件
		is.close();
	}

	@Test
	public void testCellType() throws Exception {

		InputStream is = new FileInputStream(path+"/会员消费商品明细表.xls");
		Workbook workbook = new HSSFWorkbook(is);
		Sheet sheet = workbook.getSheetAt(0);

		// 读取标题所有内容
		Row rowTitle = sheet.getRow(0);
		if (rowTitle != null) {// 行不为空
			// 读取cell
			int cellCount = rowTitle.getPhysicalNumberOfCells();
			for (int cellNum = 0; cellNum < cellCount; cellNum++) {
				Cell cell = rowTitle.getCell(cellNum);
				if (cell != null) {
					int cellType = cell.getCellType();
					String cellValue = cell.getStringCellValue();
					System.out.print(cellValue + "|");
				}
			}
			System.out.println();
		}

		// 读取商品列表数据
		int rowCount = sheet.getPhysicalNumberOfRows();
		for (int rowNum = 1; rowNum < rowCount; rowNum++) {

			Row rowData = sheet.getRow(rowNum);
			if (rowData != null) {// 行不为空

				// 读取cell
				int cellCount = rowTitle.getPhysicalNumberOfCells();
				for (int cellNum = 0; cellNum < cellCount; cellNum++) {

					System.out.print("【" + (rowNum + 1) + "-" + (cellNum + 1) + "】");

					Cell cell = rowData.getCell(cellNum);
					if (cell != null) {

						int cellType = cell.getCellType();

						//判断单元格数据类型
						String cellValue = "";
						switch (cellType) {
							case HSSFCell.CELL_TYPE_STRING://字符串
								System.out.print("【STRING】");
								cellValue = cell.getStringCellValue();
								break;

							case HSSFCell.CELL_TYPE_BOOLEAN://布尔
								System.out.print("【BOOLEAN】");
								cellValue = String.valueOf(cell.getBooleanCellValue());
								break;

							case HSSFCell.CELL_TYPE_BLANK://空
								System.out.print("【BLANK】");
								break;

							case HSSFCell.CELL_TYPE_NUMERIC:
								System.out.print("【NUMERIC】");
								//cellValue = String.valueOf(cell.getNumericCellValue());

								if (HSSFDateUtil.isCellDateFormatted(cell)) {//日期
									System.out.print("【日期】");
									Date date = cell.getDateCellValue();
									cellValue = new DateTime(date).toString("yyyy-MM-dd");
								} else {
									// 不是日期格式，则防止当数字过长时以科学计数法显示
									System.out.print("【转换成字符串】");
									cell.setCellType(HSSFCell.CELL_TYPE_STRING);
									cellValue = cell.toString();
								}
								break;

							case Cell.CELL_TYPE_ERROR:
								System.out.print("【数据类型错误】");
								break;
						}

						System.out.println(cellValue);
					}
				}
			}
		}

		is.close();
	}

	@Test
	public void testWrite03() throws IOException {

		// 创建新的Excel 工作簿
		Workbook workbook = new HSSFWorkbook();

		// 在Excel工作簿中建一工作表，其名为缺省值 Sheet0
		//Sheet sheet = workbook.createSheet();

		// 如要新建一名为"会员登录统计"的工作表，其语句为：
		Sheet sheet = workbook.createSheet("狂神观众统计表");

		// 创建行（row 1）
		Row row1 = sheet.createRow(0);

		// 创建单元格（col 1-1）
		Cell cell11 = row1.createCell(0);
		cell11.setCellValue("今日新增关注");

		// 创建单元格（col 1-2）
		Cell cell12 = row1.createCell(1);
		cell12.setCellValue(999);

		// 创建行（row 2）
		Row row2 = sheet.createRow(1);

		// 创建单元格（col 2-1）
		Cell cell21 = row2.createCell(0);
		cell21.setCellValue("统计时间");

		//创建单元格（第三列）
		Cell cell22 = row2.createCell(1);
		String dateTime = new DateTime().toString("yyyy-MM-dd HH:mm:ss");
		cell22.setCellValue(dateTime);

		// 新建一输出文件流（注意：要先创建文件夹）
		FileOutputStream out = new FileOutputStream(path+"狂神观众统计表03.xls");
		// 把相应的Excel 工作簿存盘
		workbook.write(out);
		// 操作结束，关闭文件
		out.close();
        
		System.out.println("文件生成成功");
	}

	@Test
	public void testWrite07() throws IOException {

		// 创建新的Excel 工作簿, 只有对象变了
		Workbook workbook = new XSSFWorkbook();

		// 如要新建一名为"会员登录统计"的工作表，其语句为：
		Sheet sheet = workbook.createSheet("狂神观众统计表");

		// 创建行（row 1）
		Row row1 = sheet.createRow(0);

		// 创建单元格（col 1-1）
		Cell cell11 = row1.createCell(0);
		cell11.setCellValue("今日新增关注");

		// 创建单元格（col 1-2）
		Cell cell12 = row1.createCell(1);
		cell12.setCellValue(666);

		// 创建行（row 2）
		Row row2 = sheet.createRow(1);

		// 创建单元格（col 2-1）
		Cell cell21 = row2.createCell(0);
		cell21.setCellValue("统计时间");

		//创建单元格（第三列）
		Cell cell22 = row2.createCell(1);
		String dateTime = new DateTime().toString("yyyy-MM-dd HH:mm:ss");
		cell22.setCellValue(dateTime);

		// 新建一输出文件流（注意：要先创建文件夹）
		FileOutputStream out = new FileOutputStream(path+"狂神观众统计表07.xlsx");
		// 把相应的Excel 工作簿存盘
		workbook.write(out);
		// 操作结束，关闭文件
		out.close();

		System.out.println("文件生成成功");
	}

	@Test
	public void testWrite03BigData() throws IOException {
		//记录开始时间
		long begin = System.currentTimeMillis();

		//创建一个SXSSFWorkbook
		Workbook workbook = new HSSFWorkbook();

		//创建一个sheet
		Sheet sheet = workbook.createSheet();

		//xls文件最大支持65536行
		for (int rowNum = 0; rowNum < 65536; rowNum++) {
			//创建一个行
			Row row = sheet.createRow(rowNum);
			for (int cellNum = 0; cellNum < 10; cellNum++) {//创建单元格
				Cell cell = row.createCell(cellNum);
				cell.setCellValue(cellNum);
			}
		}

		System.out.println("done");
		FileOutputStream out = new FileOutputStream(path+"bigdata03.xls");
		workbook.write(out);
		// 操作结束，关闭文件
		out.close();

		//记录结束时间
		long end = System.currentTimeMillis();
		System.out.println((double)(end - begin)/1000);
	}

	@Test
	public void testWrite07BigData() throws IOException {
		//记录开始时间
		long begin = System.currentTimeMillis();

		//创建一个XSSFWorkbook
		Workbook workbook = new XSSFWorkbook();

		//创建一个sheet
		Sheet sheet = workbook.createSheet();

		//xls文件最大支持65536行
		for (int rowNum = 0; rowNum < 100000; rowNum++) {
			//创建一个行
			Row row = sheet.createRow(rowNum);
			for (int cellNum = 0; cellNum < 10; cellNum++) {//创建单元格
				Cell cell = row.createCell(cellNum);
				cell.setCellValue(cellNum);
			}
		}

		System.out.println("done");
		FileOutputStream out = new FileOutputStream(path+"bigdata07.xlsx");
		workbook.write(out);
		// 操作结束，关闭文件
		out.close();

		//记录结束时间
		long end = System.currentTimeMillis();
		System.out.println((double)(end - begin)/1000);

	}

	@Test
	public void testWrite07BigDataFast() throws IOException {
		//记录开始时间
		long begin = System.currentTimeMillis();

		//创建一个SXSSFWorkbook
		Workbook workbook = new SXSSFWorkbook();

		//创建一个sheet
		Sheet sheet = workbook.createSheet();

		//xls文件最大支持65536行
		for (int rowNum = 0; rowNum < 100000; rowNum++) {
			//创建一个行
			Row row = sheet.createRow(rowNum);
			for (int cellNum = 0; cellNum < 10; cellNum++) {//创建单元格
				Cell cell = row.createCell(cellNum);
				cell.setCellValue(cellNum);
			}
		}

		System.out.println("done");
		FileOutputStream out = new FileOutputStream(path+"bigdata07-fast.xlsx");
		workbook.write(out);
		// 操作结束，关闭文件
		out.close();

		//清除临时文件
		((SXSSFWorkbook)workbook).dispose();

		//记录结束时间
		long end = System.currentTimeMillis();
		System.out.println((double)(end - begin)/1000);
	}

}