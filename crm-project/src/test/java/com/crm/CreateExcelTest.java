package com.crm;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.io.*;

/**
 * 使用apache-poi生成excel文件
 *
 *  文件---------HSSFWorkbook
 * 	 页-----------HSSFSheet
 * 	 行-----------HSSFRow  从0开始
 *   列-----------HSSFCell
 * 	 样式---------HSSFCellStyle
 */
public class CreateExcelTest {
    public static void main(String[] args) {
        // 创建HSSFWorkbook对象，对应一个excel文件
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        // 使用HSSFWorkbook对象，创建HSSFSheet对象，对应hssfWorkbook文件中的一个工作簿
        HSSFSheet sheet = hssfWorkbook.createSheet("学生列表");
        // 行号,列号从0开始
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("学号");
        cell=row.createCell(1);
        cell.setCellValue("姓名");
        cell = row.createCell(2);
        cell.setCellValue("性别");

        // 生成央视对象
        HSSFCellStyle cellStyle = hssfWorkbook.createCellStyle();
        // 居中
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        // 使用sheet创建10个HSSFRow对象，对应sheet中的10行
        for (int i = 1;i<=10;i++){
             row = sheet.createRow(i);
            cell = row.createCell(0);
            cell.setCellValue("学号"+i);
            cell.setCellStyle(cellStyle);
            cell=row.createCell(1);
            cell.setCellValue("姓名"+i);
            cell.setCellStyle(cellStyle);
            cell = row.createCell(2);
            cell.setCellValue("性别"+i);
            cell.setCellStyle(cellStyle);
        }
        // 调用工具生成excel文件
        // 以文件输出流的方式
        OutputStream out = null;
        try {
             out = new FileOutputStream("D:/desktop/CRM/student.xls");
            hssfWorkbook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (out!= null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (hssfWorkbook!= null){
                try {
                    hssfWorkbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("====================");
    }
}
