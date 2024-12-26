package sky.project.Controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import sky.project.DTO.*;
import sky.project.Entity.*;
import sky.project.Repository.SupplierRepository;
import sky.project.Repository.UserRepository;
import sky.project.Service.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
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
    private final AssyService assyService;
    private final StockService stockService;
    private final SupplierStockService supplierStockService;
    private final SupplierService supplierService;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final ProductionPerDayService productionPerDayService;
    private final PasswordEncoder passwordEncoder; // PasswordEncoder 주입

    //상품 등록
    @PostMapping("/addProduct")
    public String uploadProduct(@RequestParam("file") MultipartFile file, String where, Model model) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        registerProduct(worksheet);
        if (where.equals("dataUpload")) {
            return "redirect:/dashboard/dataUpload";
        } else {
            return "redirect:/plan/bomRegister";
        }
    }

    private void registerProduct(XSSFSheet worksheet) {
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
    }

    //상품 시트 다운로드
    @RequestMapping(value = "/downloadProduct/{template}")
    public void downloadProduct(HttpServletResponse response, @PathVariable boolean template, Model model) throws IOException {

        //엑셀 파일 생성
        XSSFWorkbook workbook = new XSSFWorkbook();

        makeProductSheet(workbook, template);

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

    private void makeProductSheet(XSSFWorkbook workbook, boolean template) {
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
    }

    //자재 등록
    @PostMapping("/addMaterial")
    public String uploadMaterial(@RequestParam("file") MultipartFile file, String where, Model model) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        registerMaterial(worksheet);
        if (where.equals("dataUpload")) {
            return "redirect:/dashboard/dataUpload";
        } else {
            return "redirect:/material/list";
        }
    }

    private void registerMaterial(XSSFSheet worksheet) {
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
    }

    //자재 시트 다운로드
    @RequestMapping(value = "/downloadMaterial/{template}")
    public void downloadMaterial(HttpServletResponse response, @PathVariable boolean template, Model model) throws IOException {

        //엑셀 파일 생성
        XSSFWorkbook workbook = new XSSFWorkbook();

        makeMaterialSheet(workbook, template);

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

    private void makeMaterialSheet(XSSFWorkbook workbook, boolean template) {
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

    }

    //생산계획 등록
    @PostMapping("/addProductPlan")
    public String uploadProductPlan(@RequestParam("file") MultipartFile file, String where, Model model) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        registerProductPlan(worksheet);

        if (where.equals("dataUpload")) {
            return "redirect:/dashboard/dataUpload";
        } else {
            return "redirect:/plan/list";
        }
    }

    private void registerProductPlan(XSSFSheet worksheet) {
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

            String productionPlanCode = productionPlanService.registerProductionPlan(entity);

            //6에서부터 10까지는 일별 생산량이다.
            for (int j = 0; j < 5; j++) {
                if (row.getCell(6 + j, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL) != null) {
                    int quantity = Integer.parseInt(formatter.formatCellValue(row.getCell(6 + j)));
                    ProductionPerDayDTO productionPerDayDTO = new ProductionPerDayDTO();
                    productionPerDayDTO.setProductionQuantity(quantity);
                    productionPerDayDTO.setProductionDate(productionStartDate.plusDays(j));
                    productionPerDayDTO.setProductionPlanCode(productionPlanCode);
                    productionPerDayService.register(productionPerDayDTO);
                }
            }


        }
    }

    //생산계획 다운로드
    @RequestMapping(value = "/downloadProductPlan/{template}")
    public void downloadProductPlan(HttpServletResponse response, @PathVariable boolean template, Model model) throws IOException {

        //엑셀 파일 생성
        XSSFWorkbook workbook = new XSSFWorkbook();

        makeProductPlanSheet(workbook, template);

        //Excel 파일 다운로드
        //컨텐츠 타입 및 파일명 지정
        String fileName = "productionPlan" + "_ASAP";
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

    private void makeProductPlanSheet(XSSFWorkbook workbook, boolean template) {
        //엑셀 파일 내 시트 생성
        Sheet sheet = workbook.createSheet("productionPlan");

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

        //최대 5일자까지 등록이 된다.
        headerRow.createCell(6).setCellValue("※1일자 생산수량");
        headerRow.createCell(7).setCellValue("※2일자 생산수량");
        headerRow.createCell(8).setCellValue("※3일자 생산수량");
        headerRow.createCell(9).setCellValue("※4일자 생산수량");
        headerRow.createCell(10).setCellValue("※5일자 생산수량");

        if (!template) {
            //Body 설정
            List<ProductionPlan> planList = productionPlanService.getProductionPlans();
            for (int i = 0; i < planList.size(); i++) {
                Row bodyRow = sheet.createRow(rowCount++);
                bodyRow.createCell(0).setCellValue(planList.get(i).getProductionPlanCode());
                bodyRow.createCell(1).setCellValue(planList.get(i).getProductCode());
                bodyRow.createCell(2).setCellValue(planList.get(i).getProductName());

                CellStyle cellStyle = workbook.createCellStyle();
                CreationHelper creationHelper = workbook.getCreationHelper();
                cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd"));
                //날짜 형식이라 포맷을 해줘야함
                bodyRow.createCell(3).setCellValue(Date.valueOf(planList.get(i).getProductionStartDate()));
                bodyRow.getCell(3).setCellStyle(cellStyle);
                bodyRow.createCell(4).setCellValue(Date.valueOf(planList.get(i).getProductionEndDate()));
                bodyRow.getCell(4).setCellStyle(cellStyle);
                bodyRow.createCell(5).setCellValue(planList.get(i).getProductionQuantity());

                //생산 수량 출력
                List<ProductionPerDayDTO> perDays = productionPerDayService.findbyPlanId(planList.get(i).getPlanId());
                for (int j = 0; j < perDays.size(); j++) {
                    bodyRow.createCell(6 + j).setCellValue(perDays.get(j).getProductionQuantity());
                }


            }
        }
    }

    //조립구조 등록
    @PostMapping("/addAssy")
    public String uploadAssy(@RequestParam("file") MultipartFile file, String where, Model model) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        registerAssy(worksheet);
        if (where.equals("dataUpload")) {
            return "redirect:/dashboard/dataUpload";
        } else {
            return "redirect:/plan/bomRegister";
        }
    }

    private void registerAssy(XSSFSheet worksheet) {
        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
            AssyDTO entity = new AssyDTO();
            DataFormatter formatter = new DataFormatter();
            XSSFRow row = worksheet.getRow(i);
            String assyMaterialCode = formatter.formatCellValue(row.getCell(1));
            String materialCode = formatter.formatCellValue(row.getCell(3));
            int quantity = Integer.parseInt(formatter.formatCellValue(row.getCell(6)));
            String productCode = formatter.formatCellValue(row.getCell(7));

            entity.setAssyMaterialCode(assyMaterialCode);
            entity.setMaterialCode(materialCode);
            entity.setQuantity(quantity);
            entity.setProductCode(productCode);

            assyService.register(entity);
        }
    }

    //조립구조 다운로드
    @RequestMapping(value = "/downloadAssy/{template}")
    public void downloadAssy(HttpServletResponse response, @PathVariable boolean template, Model model) throws IOException {

        //엑셀 파일 생성
        XSSFWorkbook workbook = new XSSFWorkbook();

        makeAssySheet(workbook, template);

        //Excel 파일 다운로드
        //컨텐츠 타입 및 파일명 지정
        String fileName = "assy" + "_ASAP";
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

    private void makeAssySheet(XSSFWorkbook workbook, boolean template) {
        //엑셀 파일 내 시트 생성
        Sheet sheet = workbook.createSheet("assy");

        //Row 순서 / Cell 순서 변수 선언 및 초기화
        int rowCount = 0;
        int cellCount = 0;

        //Header 설정
        Row headerRow = sheet.createRow(rowCount++);
        headerRow.createCell(0).setCellValue("인덱스");
        headerRow.createCell(1).setCellValue("※조립품 코드");
        headerRow.createCell(2).setCellValue("조립품 이름");
        headerRow.createCell(3).setCellValue("※재료 자재 코드");
        headerRow.createCell(4).setCellValue("재료 자재 이름");
        headerRow.createCell(5).setCellValue("재료 자재 부품 종류");
        headerRow.createCell(6).setCellValue("※재료필요수량");
        headerRow.createCell(7).setCellValue("※상품(최종 완성품) 코드");
        headerRow.createCell(8).setCellValue("상품 이름");

        if (!template) {
            //Body 설정
            List<Assy> assyList = assyService.getAssys();
            for (int i = 0; i < assyList.size(); i++) {
                Row bodyRow = sheet.createRow(rowCount);
                bodyRow.createCell(0).setCellValue(rowCount++);
                bodyRow.createCell(1).setCellValue(assyList.get(i).getAssemblyMaterial().getMaterialCode());
                bodyRow.createCell(2).setCellValue(assyList.get(i).getAssemblyMaterial().getMaterialName());
                bodyRow.createCell(3).setCellValue(assyList.get(i).getMaterial().getMaterialCode());
                bodyRow.createCell(4).setCellValue(assyList.get(i).getMaterial().getMaterialName());
                bodyRow.createCell(5).setCellValue(assyList.get(i).getMaterial().getComponentType());
                bodyRow.createCell(6).setCellValue(assyList.get(i).getQuantity());
                bodyRow.createCell(7).setCellValue(assyList.get(i).getProduct().getProductCode());
                bodyRow.createCell(8).setCellValue(assyList.get(i).getProduct().getProductName());
            }
        }
    }

    //BOM 등록
    @PostMapping("/addBOM")
    public String uploadBOM(@RequestParam("file") MultipartFile file, String where, Model model) throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        registerBOM(worksheet);

        if (where.equals("dataUpload")) {
            return "redirect:/dashboard/dataUpload";
        } else {
            return "redirect:/plan/bomRegister";
        }
    }

    private void registerBOM(XSSFSheet worksheet) {
        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
            BomDTO entity = new BomDTO();
            DataFormatter formatter = new DataFormatter();
            XSSFRow row = worksheet.getRow(i);

            String productCode = formatter.formatCellValue(row.getCell(0));
            String materialCode = formatter.formatCellValue(row.getCell(2));
            String componentType = formatter.formatCellValue(row.getCell(4));
            int requireQuantity = Integer.parseInt(formatter.formatCellValue(row.getCell(5)));

            entity.setProductCode(productCode);
            entity.setMaterialCode(materialCode);
            entity.setComponentType(componentType);
            entity.setRequireQuantity(requireQuantity);

            bomService.register(entity);
        }
    }

    //BOM 다운로드
    @RequestMapping(value = "/downloadBOM/{template}")
    public void downloadBOM(HttpServletResponse response, @PathVariable boolean template, Model model) throws IOException {

        //엑셀 파일 생성
        XSSFWorkbook workbook = new XSSFWorkbook();

        makeBOMSheet(workbook, template);

        //Excel 파일 다운로드
        //컨텐츠 타입 및 파일명 지정
        String fileName = "bom" + "_ASAP";
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

    private void makeBOMSheet(XSSFWorkbook workbook, boolean template) {
        //엑셀 파일 내 시트 생성
        Sheet sheet = workbook.createSheet("bom");

        //Row 순서 / Cell 순서 변수 선언 및 초기화
        int rowCount = 0;
        int cellCount = 0;

        //Header 설정
        Row headerRow = sheet.createRow(rowCount++);
        headerRow.createCell(0).setCellValue("※상품 코드");
        headerRow.createCell(1).setCellValue("상품 이름");
        headerRow.createCell(2).setCellValue("※자재 코드");
        headerRow.createCell(3).setCellValue("자재 이름");
        headerRow.createCell(4).setCellValue("※부품 타입");
        headerRow.createCell(5).setCellValue("※사용 수량");

        if (!template) {
            //Body 설정
            List<Bom> bomList = bomService.getbomList();
            for (int i = 0; i < bomList.size(); i++) {
                Row bodyRow = sheet.createRow(rowCount++);
                bodyRow.createCell(0).setCellValue(bomList.get(i).getProduct().getProductCode());
                bodyRow.createCell(1).setCellValue(bomList.get(i).getProduct().getProductName());
                bodyRow.createCell(2).setCellValue(bomList.get(i).getMaterial().getMaterialCode());
                bodyRow.createCell(3).setCellValue(bomList.get(i).getMaterial().getMaterialName());
                bodyRow.createCell(4).setCellValue(bomList.get(i).getComponentType());
                bodyRow.createCell(5).setCellValue(bomList.get(i).getRequireQuantity());
            }
        }

    }

    //창고 자재 등록
    @PostMapping("/addStock")
    public String uploadStock(@RequestParam("file") MultipartFile file, String where, Model model) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        registerStock(worksheet);
        if (where.equals("dataUpload")) {
            return "redirect:/dashboard/dataUpload";
        } else {
            return "redirect:/material/stocklist";
        }
    }

    private void registerStock(XSSFSheet worksheet) {
        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
            StockDTO entity = new StockDTO();
            DataFormatter formatter = new DataFormatter();
            XSSFRow row = worksheet.getRow(i);
            String materialCode = formatter.formatCellValue(row.getCell(3));
            int quantity = Integer.parseInt(formatter.formatCellValue(row.getCell(5)));

            entity.setMaterialCode(materialCode);
            entity.setQuantity(quantity);

            stockService.register(entity);
        }
    }

    //창고 자재 다운로드
    @RequestMapping(value = "/downloadStock/{template}")
    public void downloadStock(HttpServletResponse response, @PathVariable boolean template, Model model) throws IOException {

        //엑셀 파일 생성
        XSSFWorkbook workbook = new XSSFWorkbook();

        makeStockSheet(workbook, template);

        //Excel 파일 다운로드
        //컨텐츠 타입 및 파일명 지정
        String fileName = "stock" + "_ASAP";
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

    private void makeStockSheet(XSSFWorkbook workbook, boolean template) {
        //엑셀 파일 내 시트 생성
        Sheet sheet = workbook.createSheet("stock");

        //Row 순서 / Cell 순서 변수 선언 및 초기화
        int rowCount = 0;
        int cellCount = 0;

        //Header 설정
        Row headerRow = sheet.createRow(rowCount++);
        headerRow.createCell(0).setCellValue("인덱스");
        headerRow.createCell(1).setCellValue("자재 유형");
        headerRow.createCell(2).setCellValue("부품 종류");
        headerRow.createCell(3).setCellValue("※자재 코드");
        headerRow.createCell(4).setCellValue("자재명");
        headerRow.createCell(5).setCellValue("※창고 내 재고 수량");

        if (!template) {
            //Body 설정
            List<Stock> stockList = stockService.getStocks();
            for (int i = 0; i < stockList.size(); i++) {
                Row bodyRow = sheet.createRow(rowCount);
                bodyRow.createCell(0).setCellValue(rowCount++);
                bodyRow.createCell(1).setCellValue(stockList.get(i).getMaterial().getMaterialType());
                bodyRow.createCell(2).setCellValue(stockList.get(i).getMaterial().getComponentType());
                bodyRow.createCell(3).setCellValue(stockList.get(i).getMaterial().getMaterialCode());
                bodyRow.createCell(4).setCellValue(stockList.get(i).getMaterial().getMaterialName());
                bodyRow.createCell(5).setCellValue(stockList.get(i).getQuantity());
            }
        }
    }

    //업체 창고 자재등록
    @PostMapping("/addSupplierStock")
    public String uploadSupplierStock(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        registerSupplierStock(worksheet);
        return "redirect:/dashboard/dataUpload";
    }

    private void registerSupplierStock(XSSFSheet worksheet) {
        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
            SupplierStockDTO entity = new SupplierStockDTO();
            DataFormatter formatter = new DataFormatter();
            XSSFRow row = worksheet.getRow(i);

            String supplierName = formatter.formatCellValue(row.getCell(0));
            String materialCode = formatter.formatCellValue(row.getCell(3));
            int stock = Integer.parseInt(formatter.formatCellValue(row.getCell(5)));

            entity.setSupplierName(supplierName);
            entity.setMaterialCode(materialCode);
            entity.setStock(stock);


            supplierStockService.register(entity);
        }
    }

    //업체 창고 자재 다운로드
    @RequestMapping(value = "/downloadSupplierStock/{template}")
    public void downloadSupplierStock(HttpServletResponse response, @PathVariable boolean template, Model model) throws IOException {

        //엑셀 파일 생성
        XSSFWorkbook workbook = new XSSFWorkbook();

        makeSupplierStockSheet(workbook, template);

        //Excel 파일 다운로드
        //컨텐츠 타입 및 파일명 지정
        String fileName = "supplierStock" + "_ASAP";
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

    private void makeSupplierStockSheet(XSSFWorkbook workbook, boolean template) {
        //엑셀 파일 내 시트 생성
        Sheet sheet = workbook.createSheet("supplierStock");

        //Row 순서 / Cell 순서 변수 선언 및 초기화
        int rowCount = 0;
        int cellCount = 0;

        //Header 설정
        Row headerRow = sheet.createRow(rowCount++);
        headerRow.createCell(0).setCellValue("※공급업체");
        headerRow.createCell(1).setCellValue("자재 유형");
        headerRow.createCell(2).setCellValue("부품 종류");
        headerRow.createCell(3).setCellValue("※자재 코드");
        headerRow.createCell(4).setCellValue("자재명");
        headerRow.createCell(5).setCellValue("※창고 내 재고 수량");

        if (!template) {
            //Body 설정
            List<SupplierStock> stockList = supplierStockService.getStocks();
            for (int i = 0; i < stockList.size(); i++) {
                Row bodyRow = sheet.createRow(rowCount++);
                bodyRow.createCell(0).setCellValue(stockList.get(i).getSupplier().getSupplierName());
                bodyRow.createCell(1).setCellValue(stockList.get(i).getMaterial().getMaterialType());
                bodyRow.createCell(2).setCellValue(stockList.get(i).getMaterial().getComponentType());
                bodyRow.createCell(3).setCellValue(stockList.get(i).getMaterial().getMaterialCode());
                bodyRow.createCell(4).setCellValue(stockList.get(i).getMaterial().getMaterialName());
                bodyRow.createCell(5).setCellValue(stockList.get(i).getStock());
            }
        }
    }

    //업체 등록
    @PostMapping("/addSupplier")
    public String uploadSupplier(@RequestParam("file") MultipartFile file, Model model) throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        registerSupplier(worksheet);
        return "redirect:/dashboard/dataUpload";
    }

    private void registerSupplier(XSSFSheet worksheet) {
        String password = "1234"; //이 기능을 사용해서 가입하는 이들의 패스워드를 여기서 지정(추후 각자가 로그인해서 변경할 것)
        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
            User user = new User();
            Supplier supplier = new Supplier();

            DataFormatter formatter = new DataFormatter();
            XSSFRow row = worksheet.getRow(i);

            String supplierId = formatter.formatCellValue(row.getCell(0));
            String supplierName = formatter.formatCellValue(row.getCell(1));
            String businessType = formatter.formatCellValue(row.getCell(2));
            String businessItem = formatter.formatCellValue(row.getCell(3));
            String businessRegistrationNumber = formatter.formatCellValue(row.getCell(4));
            String contactInfo = formatter.formatCellValue(row.getCell(5));
            String address = formatter.formatCellValue(row.getCell(6));

            //유저 정보 세팅
            user.setUserId(supplierId);
            user.setUsername(supplierName);
            user.setUserAddress(address);
            user.setPassword(passwordEncoder.encode(password));
            user.setPhone(contactInfo);
            user.setUserType(UserType.SUPPLIER);

            //유저 등록을 먼저 한다.
            userRepository.save(user);


            //공급업체 정보 세팅
            supplier.setSupplierId(supplierId);
            supplier.setSupplierName(supplierName);
            supplier.setBusinessType(businessType);
            supplier.setBusinessItem(businessItem);
            supplier.setBusinessRegistrationNumber(businessRegistrationNumber);
            supplier.setContactInfo(contactInfo);
            supplier.setAddress(address);
            supplier.setApproved(true);
            supplier.setUser(user);

            //업체 등록
            supplierRepository.save(supplier);
        }
    }

    //업체 다운로드
    @RequestMapping(value = "/downloadSupplier/{template}")
    public void downloadSupplier(HttpServletResponse response, @PathVariable boolean template, Model model) throws IOException {

        //엑셀 파일 생성
        XSSFWorkbook workbook = new XSSFWorkbook();

        makeSupplierSheet(workbook, template);

        //Excel 파일 다운로드
        //컨텐츠 타입 및 파일명 지정
        String fileName = "supplier" + "_ASAP";
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

    private void makeSupplierSheet(XSSFWorkbook workbook, boolean template) {
        //엑셀 파일 내 시트 생성
        Sheet sheet = workbook.createSheet("supplier");

        //Row 순서 / Cell 순서 변수 선언 및 초기화
        int rowCount = 0;
        int cellCount = 0;

        //Header 설정
        Row headerRow = sheet.createRow(rowCount++);
        headerRow.createCell(0).setCellValue("※ID");
        headerRow.createCell(1).setCellValue("※공급업체명");
        headerRow.createCell(2).setCellValue("※업종");
        headerRow.createCell(3).setCellValue("※업태");
        headerRow.createCell(4).setCellValue("※사업자번호");
        headerRow.createCell(5).setCellValue("※연락처");
        headerRow.createCell(6).setCellValue("※주소");

        if (!template) {
            //Body 설정
            List<Supplier> supplierList = supplierService.getSuppliers();
            for (int i = 0; i < supplierList.size(); i++) {
                Row bodyRow = sheet.createRow(rowCount++);
                bodyRow.createCell(0).setCellValue(supplierList.get(i).getSupplierId());
                bodyRow.createCell(1).setCellValue(supplierList.get(i).getSupplierName());
                bodyRow.createCell(2).setCellValue(supplierList.get(i).getBusinessType());
                bodyRow.createCell(3).setCellValue(supplierList.get(i).getBusinessItem());
                bodyRow.createCell(4).setCellValue(supplierList.get(i).getBusinessRegistrationNumber());
                bodyRow.createCell(5).setCellValue(supplierList.get(i).getContactInfo());
                bodyRow.createCell(6).setCellValue(supplierList.get(i).getAddress());
            }
        }
    }

    //통합 등록
    @PostMapping("/addTotal")
    public String uploadTotal(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet productSheet = workbook.getSheet("product");
        XSSFSheet supplierSheet = workbook.getSheet("supplier");
        XSSFSheet productionPlanSheet = workbook.getSheet("productionPlan");
        XSSFSheet materialSheet = workbook.getSheet("material");
        XSSFSheet supplierStockSheet = workbook.getSheet("supplierStock");
        XSSFSheet assySheet = workbook.getSheet("assy");
        XSSFSheet stockSheet = workbook.getSheet("stock");
        XSSFSheet bomSheet = workbook.getSheet("bom");

        //상품 등록
        if (productSheet != null) registerProduct(productSheet);
        //생산 계획 등록
        if (productionPlanSheet != null) registerProductPlan(productionPlanSheet);
        //업체 등록
        if (supplierSheet != null) registerSupplier(supplierSheet);
        //자재 등록
        if (materialSheet != null) registerMaterial(materialSheet);
        //업체 재고 등록
        if (supplierStockSheet != null) registerSupplierStock(supplierStockSheet);
        //조립 구조 등록
        if (assySheet != null) registerAssy(assySheet);
        //BOM 등록
        if (bomSheet != null) registerBOM(bomSheet);
        //창고 재고 등록
        if (stockSheet != null) registerStock(stockSheet);


        return "redirect:/dashboard/dataUpload";
    }

    //통합 다운로드
    @RequestMapping(value = "/downloadTotal/{template}")
    public void downloadTotal(HttpServletResponse response, @PathVariable boolean template, Model model) throws IOException {

        //엑셀 파일 생성
        XSSFWorkbook workbook = new XSSFWorkbook();

        makeProductSheet(workbook, template);
        makeProductPlanSheet(workbook, template);
        makeSupplierSheet(workbook, template);
        makeMaterialSheet(workbook, template);
        makeSupplierStockSheet(workbook, template);
        makeAssySheet(workbook, template);
        makeBOMSheet(workbook, template);
        makeStockSheet(workbook, template);

        //Excel 파일 다운로드
        //컨텐츠 타입 및 파일명 지정
        String fileName = "total" + "_ASAP";
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
}
