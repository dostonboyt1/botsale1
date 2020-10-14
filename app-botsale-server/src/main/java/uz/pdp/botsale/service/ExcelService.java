package uz.pdp.botsale.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.pdp.botsale.entity.Product;
import uz.pdp.botsale.entity.ProductSize;
import uz.pdp.botsale.payload.ApiResponse;
import uz.pdp.botsale.repository.ProductRepository;
import uz.pdp.botsale.repository.ProductSizeRepository;
import uz.pdp.botsale.repository.ProductWithAmountRepository;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelService {
    private static String[] columns = {"Mahsulot nomi", "Kirish narxi", "Sotish narxi","Soni"};//excelni ustunlarini yaratib olish


    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductSizeRepository productSizeRepository;

    @Autowired
    ProductWithAmountRepository productWithAmountRepository;


    public ResponseEntity<byte[]> downloadProductAmountInfoByWarehouse(Integer warehouseId){
        Workbook workbook = new XSSFWorkbook();

        CreationHelper creationHelper = workbook.getCreationHelper(); //workbookni xamma formatlarini qo'llab quvvatlaydi

        Sheet sheet = workbook.createSheet("Employeelar ro'yxati");

        //Font stylelari
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.RED.getIndex());
        font.setFontHeightInPoints((short) 16);

        //bitta yacheyka stil ko'rinishi
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);


        //qator yasamoqchiman Create row
        Row row = sheet.createRow(0);

        //Create cell ustun yasayapti
        for (int i = 0; i < columns.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(columns[i]);
        }


        //date formatni qabul qildirish stil yozdik bitta yacheyka
        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd-MM-yyyy"));

        // Create Other rows and cells with employees data
        //Qolgan qatorlarni va ustunlarni employee royxatiga qarab chizish

        int rowNum =1;
        List<Product> productList=productRepository.findAll();

        for (Product product : productList) {
            Row row1 = sheet.createRow(rowNum++);

            //yasalayotgan qator birinchi yacheykaga employee nomi keladi
            row1.createCell(0).setCellValue(product.getNameUz());

            //yasalayotgan qator uchun emailni joyini ko'rsatish
            row1.createCell(1).setCellValue(product.getIncomePrice());
            row1.createCell(2).setCellValue(product.getSalePrice());

            List<ProductSize> productSizeList=product.getProductSizeList();
            List<String> sizeAmount=new ArrayList<>();
            for (ProductSize productSize : productSizeList){
                int amount=productWithAmountRepository.getProductAmountByWarehouseIdAndProductIdAndSizeId(warehouseId, product.getId(), productSize.getId());
               sizeAmount.add(productSize.getName()+" : "+amount);
            }
            String str="";
            for (String s : sizeAmount) {
            str+=s+"\n";
            }
            row1.createCell(3).setCellValue(str+"\n");
        }
        //ustun auto size ga ega bo'ladi
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            workbook.write(byteArrayOutputStream);
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\" Ombor boyicha barcha maxsulotlarning royxati.xlsx")
                .body(bytes);

    }
}
