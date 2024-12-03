package sky.project.Controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import sky.project.DTO.BomDTO;
import sky.project.DTO.MaterialDTO;
import sky.project.DTO.ProductDTO;
import sky.project.DTO.ProductionPlanDTO;
import sky.project.Entity.Material;
import sky.project.Entity.Product;
import sky.project.Entity.ProductionPlan;
import sky.project.Service.BomService;
import sky.project.Service.MaterialService;
import sky.project.Service.ProductService;
import sky.project.Service.ProductionPlanService;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/excel/")
public class ExcelController {

    private final ProductService productService;
    private final BomService bomService;
    private final MaterialService materialService;
    private final ProductionPlanService productionPlanService;

    //상품 등록
    @PostMapping("/addProduct")
    public String uploadProduct(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
            ProductDTO entity = new ProductDTO();
            DataFormatter formatter = new DataFormatter();
            XSSFRow row = worksheet.getRow(i);
            String productCode = formatter.formatCellValue(row.getCell(1));
            String productName = formatter.formatCellValue(row.getCell(2));

            entity.setProductCode(productCode);
            entity.setProductName(productName);

            productService.register(entity);
        }
        return "redirect:/plan/bomRegister";
    }

    //상품 시트 다운로드

    @RequestMapping(value = "/downloadProduct/{template}")
    public void downloadProduct(HttpServletResponse response, @PathVariable boolean template, Model model) throws IOException {

        //엑셀 파일 생성
        XSSFWorkbook workbook = new XSSFWorkbook();

        //엑셀 파일 내 시트 생성
        Sheet sheet = workbook.createSheet("product");

        //Row 순서 / Cell 순서 변수 선언 및 초기화
        int rowCount = 0;
        int cellCount = 0;

        //Header 설정
        Row headerRow = sheet.createRow(rowCount++);
        headerRow.createCell(0).setCellValue("인덱스");
        headerRow.createCell(1).setCellValue("※상품 코드");
        headerRow.createCell(2).setCellValue("※상품 이름");

        if (!template) {
            //Body 설정
            List<Product> productList = productService.getProductList();
            for (int i = 0; i < productList.size(); i++) {
                Row bodyRow = sheet.createRow(rowCount);
                bodyRow.createCell(0).setCellValue(rowCount++);
                bodyRow.createCell(1).setCellValue(productList.get(i).getProductCode());
                bodyRow.createCell(2).setCellValue(productList.get(i).getProductName());
            }
        }

        //Excel 파일 다운로드
        //컨텐츠 타입 및 파일명 지정
        String fileName = "product" + "_ASAP";
        if (template) {
            fileName += "_template";
        } else {
            fileName = fileName + "_" + LocalDate.now();
        }
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");

        try {
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //자재 등록
    @PostMapping("/addMaterial")
    public String uploadMaterial(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
            MaterialDTO entity = new MaterialDTO();
            DataFormatter formatter = new DataFormatter();
            XSSFRow row = worksheet.getRow(i);
            String materialName = formatter.formatCellValue(row.getCell(1));
            String materialCode = formatter.formatCellValue(row.getCell(2));
            String materialType = formatter.formatCellValue(row.getCell(3));
            String componentType = formatter.formatCellValue(row.getCell(4));
            String supplierName = formatter.formatCellValue(row.getCell(5));
            int leadTime = Integer.parseInt(formatter.formatCellValue(row.getCell(6)));
            int width = Integer.parseInt(formatter.formatCellValue(row.getCell(7)));
            int height = Integer.parseInt(formatter.formatCellValue(row.getCell(8)));
            int depth = Integer.parseInt(formatter.formatCellValue(row.getCell(9)));
            if (row.getCell(10, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL) != null) {
                BigDecimal weight = new BigDecimal(formatter.formatCellValue(row.getCell(10)));
                entity.setWeight(weight);
            }
            Double unitPrice = Double.parseDouble(formatter.formatCellValue(row.getCell(11)));
            Integer quantity = Integer.parseInt(formatter.formatCellValue(row.getCell(12)));

            entity.setMaterialName(materialName);
            entity.setMaterialCode(materialCode);
            entity.setMaterialType(materialType);
            entity.setComponentType(componentType);
            entity.setSupplierName(supplierName);
            entity.setLeadTime(leadTime);
            entity.setWidth(width);
            entity.setHeight(height);
            entity.setDepth(depth);
            entity.setUnitPrice(unitPrice);
            entity.setQuantity(quantity);


            materialService.registerMaterial(entity, null);
        }
        return "redirect:/plan/bomRegister";
    }

    //자재 시트 다운로드
    @RequestMapping(value = "/downloadMaterial/{template}")
    public void downloadMaterial(HttpServletResponse response, @PathVariable boolean template, Model model) throws IOException {

        //엑셀 파일 생성
        XSSFWorkbook workbook = new XSSFWorkbook();

        //엑셀 파일 내 시트 생성
        Sheet sheet = workbook.createSheet("material");

        //Row 순서 / Cell 순서 변수 선언 및 초기화
        int rowCount = 0;
        int cellCount = 0;

        //Header 설정
        Row headerRow = sheet.createRow(rowCount++);
        headerRow.createCell(0).setCellValue("인덱스");
        headerRow.createCell(1).setCellValue("※자재 이름");
        headerRow.createCell(2).setCellValue("※자재 코드");
        headerRow.createCell(3).setCellValue("※자재 유형");
        headerRow.createCell(4).setCellValue("※부품 종류");
        headerRow.createCell(5).setCellValue("※공급사");
        headerRow.createCell(6).setCellValue("※리드 타임");
        headerRow.createCell(7).setCellValue("길이");
        headerRow.createCell(8).setCellValue("너비");
        headerRow.createCell(9).setCellValue("높이");
        headerRow.createCell(10).setCellValue("무게");
        headerRow.createCell(11).setCellValue("※단위당 가격");
        headerRow.createCell(12).setCellValue("※최소 공급 수량");

        if (!template) {
            //Body 설정
            List<Material> materialList = materialService.getMaterials();
            for (int i = 0; i < materialList.size(); i++) {
                Row bodyRow = sheet.createRow(rowCount);
                bodyRow.createCell(0).setCellValue(rowCount++);
                bodyRow.createCell(1).setCellValue(materialList.get(i).getMaterialName());
                bodyRow.createCell(2).setCellValue(materialList.get(i).getMaterialCode());
                bodyRow.createCell(3).setCellValue(materialList.get(i).getMaterialType());
                bodyRow.createCell(4).setCellValue(materialList.get(i).getComponentType());
                bodyRow.createCell(5).setCellValue(materialList.get(i).getSupplier().getSupplierName());
                bodyRow.createCell(6).setCellValue(materialList.get(i).getLeadTime());
                bodyRow.createCell(7).setCellValue(materialList.get(i).getWidth());
                bodyRow.createCell(8).setCellValue(materialList.get(i).getHeight());
                bodyRow.createCell(9).setCellValue(materialList.get(i).getDepth());
                if (materialList.get(i).getWeight() != null) {
                    bodyRow.createCell(10).setCellValue(materialList.get(i).getWeight().doubleValue());
                }
                bodyRow.createCell(11).setCellValue(materialList.get(i).getUnitPrice());
                bodyRow.createCell(12).setCellValue(materialList.get(i).getQuantity());
            }
        }

        //Excel 파일 다운로드
        //컨텐츠 타입 및 파일명 지정
        String fileName = "material" + "_ASAP";
        if (template) {
            fileName += "_template";
        } else {
            fileName = fileName + "_" + LocalDate.now();
        }
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");

        try {
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //생산계획 등록
    @PostMapping("/addProductPlan")
    public String uploadProductPlan(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
            ProductionPlanDTO entity = new ProductionPlanDTO();
            DataFormatter formatter = new DataFormatter();
            XSSFRow row = worksheet.getRow(i);
            if (row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL) != null) {
                String productionPlanCode = formatter.formatCellValue(row.getCell(0));
                entity.setProductionPlanCode(productionPlanCode);
            }
            String productCode = formatter.formatCellValue(row.getCell(1));
            String productName = formatter.formatCellValue(row.getCell(2));
            //날짜 형식이라 포맷을 해줘야함
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            LocalDate productionStartDate = LocalDate.parse(sdf.format(row.getCell(3).getDateCellValue()));
            LocalDate productionEndDate = LocalDate.parse(sdf.format(row.getCell(4).getDateCellValue()));
            Integer productionQuantity = Integer.parseInt(formatter.formatCellValue(row.getCell(5)));

            entity.setProductCode(productCode);
            entity.setProductName(productName);
            entity.setProductionStartDate(productionStartDate);
            entity.setProductionEndDate(productionEndDate);
            entity.setProductionQuantity(productionQuantity);

            productionPlanService.registerProductionPlan(entity);
        }
        return "redirect:/plan/list";
    }

    //생산계획 다운로드

    @RequestMapping(value = "/downloadProductPlan/{template}")
    public void downloadProductPlan(HttpServletResponse response, @PathVariable boolean template, Model model) throws IOException {

        //엑셀 파일 생성
        XSSFWorkbook workbook = new XSSFWorkbook();

        //엑셀 파일 내 시트 생성
        Sheet sheet = workbook.createSheet("productPlan");

        //Row 순서 / Cell 순서 변수 선언 및 초기화
        int rowCount = 0;
        int cellCount = 0;

        //Header 설정
        Row headerRow = sheet.createRow(rowCount++);
        headerRow.createCell(0).setCellValue("생산계획코드");
        headerRow.createCell(1).setCellValue("※생산제품코드");
        headerRow.createCell(2).setCellValue("※생산제품명");
        headerRow.createCell(3).setCellValue("※생산시작일");
        headerRow.createCell(4).setCellValue("※생산종료일");
        headerRow.createCell(5).setCellValue("※생산수량");

        if (!template) {
            //Body 설정
            List<ProductionPlan> planList = productionPlanService.getProductionPlans();
            for (int i = 0; i < planList.size(); i++) {
                Row bodyRow = sheet.createRow(rowCount++);
                bodyRow.createCell(0).setCellValue(planList.get(i).getProductionPlanCode());
                bodyRow.createCell(1).setCellValue(planList.get(i).getProductCode());
                bodyRow.createCell(2).setCellValue(planList.get(i).getProductName());

                //날짜 형식이라 포맷을 해줘야함
                bodyRow.createCell(3).setCellValue(String.valueOf(planList.get(i).getProductionStartDate()));
                bodyRow.createCell(4).setCellValue(String.valueOf(planList.get(i).getProductionEndDate()));

                bodyRow.createCell(5).setCellValue(planList.get(i).getProductionQuantity());
            }
        }

        //Excel 파일 다운로드
        //컨텐츠 타입 및 파일명 지정
        String fileName = "productPlan" + "_ASAP";
        if (template) {
            fileName += "_template";
        } else {
            fileName = fileName + "_" + LocalDate.now();
        }
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");

        try {
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //BOM 등록
    @PostMapping("/addBOM")
    public String uploadBOM(@RequestParam("file") MultipartFile file, Model model) throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);

        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
            BomDTO entity = new BomDTO();
            DataFormatter formatter = new DataFormatter();
            XSSFRow row = worksheet.getRow(i);

            //insertDataTest를 참고해서 BOM입력에 필요한 데이터를 넣어두자.

            bomService.register(entity);
        }
        return "redirect:/plan/bomRegister";
    }

}
