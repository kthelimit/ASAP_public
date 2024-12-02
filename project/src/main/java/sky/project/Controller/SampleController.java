package sky.project.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import sky.project.DTO.ProductDTO;
import sky.project.Service.ProductService;

import java.io.IOException;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/sample/")
public class SampleController {

    private final ProductService productService;


    @GetMapping("/all1")
    public void exAll1() {
        log.info("exAll..............");
    }

    @GetMapping("/all2")
    public void exAll2() {
        log.info("exAll..............");
    }

    @GetMapping("/all3")
    public void exAll3() {
        log.info("exAll..............");
    }

    @GetMapping("/member")
    public void exMember() {
        log.info("exMember...............");
    }

    @GetMapping("/admin")
    public void exAdmin() {
        log.info("exAdmin..............");
    }

    @GetMapping("/admin2")
    public void exAdmin2() {
        log.info("exAdmin..............");
    }

    @GetMapping("/admin3")
    public void exAdmin3() {
        log.info("exAdmin..............");
    }

    @GetMapping("/admin4")
    public void exAdmin4() {
        log.info("exAdmin..............");
    }

    @GetMapping("/admin5")
    public void exAdmin5() {
        log.info("exAdmin..............");
    }

    @GetMapping("/excelTest")
    public void exExcelTest() {
    }

    @PostMapping("/addExcel")
    public String readExcel(@RequestParam("file")MultipartFile file, Model model) throws IOException{

        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);

        for(int i=1; i<worksheet.getPhysicalNumberOfRows(); i++){
            ProductDTO product = new ProductDTO();
            DataFormatter formatter = new DataFormatter();
            XSSFRow row= worksheet.getRow(i);
            String productCode = formatter.formatCellValue(row.getCell(0));
            String productName = formatter.formatCellValue(row.getCell(1));

            product.setProductCode(productCode);
            product.setProductName(productName);
            log.info("product: "+product);
//            excelService.insertExcel(product);
            productService.register(product);
        }
        return "redirect:/sample/excelTest";
    }


    @GetMapping("/babo")
    public void exBabo() {log.info("exBabo..............");}

    @GetMapping("/invoicedetail")
    public String exInvoice() {
        log.info("exInvoice...............");
        return "Invoice/Invoice";
    }

    @GetMapping("/invoice")
    public String exInvoiceList() {
        return "Invoice/Invoicelist";
    }
}