package sky.project.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sky.project.DTO.BomDTO;
import sky.project.DTO.ProductDTO;
import sky.project.Service.BomService;
import sky.project.Service.ProductService;

import java.io.IOException;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/excel/")
public class ExcelController {

    private final ProductService productService;
    private final BomService bomService;


    //상품 등록
    @PostMapping("/addProduct")
    public String uploadProduct(@RequestParam("file") MultipartFile file, Model model) throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);

        for(int i=1; i<worksheet.getPhysicalNumberOfRows(); i++){
            ProductDTO entity = new ProductDTO();
            DataFormatter formatter = new DataFormatter();
            XSSFRow row= worksheet.getRow(i);
            String productCode = formatter.formatCellValue(row.getCell(0));
            String productName = formatter.formatCellValue(row.getCell(1));

            entity.setProductCode(productCode);
            entity.setProductName(productName);

            //현재는 Mapper를 사용해 바로 DB에 입력하는 것이 아니라 시스템을 통해 입력하게 되어있다(입력받은 시간과 같은 자동으로 입력되는 데이터가 있기 때문에)
            //추후 엑셀 다운로드를 구현하면 mapper를 이용해서 바로 DB에 입력하도록 변경할 것
            productService.register(entity);
        }
        return "redirect:/plan/bomRegister";
    }
    
    //상품 시트 다운로드

    //BOM 등록
    @PostMapping("/addBOM")
    public String uploadBOM(@RequestParam("file") MultipartFile file, Model model) throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);

        for(int i=1; i<worksheet.getPhysicalNumberOfRows(); i++){
            BomDTO entity = new BomDTO();
            DataFormatter formatter = new DataFormatter();
            XSSFRow row= worksheet.getRow(i);

            //insertDataTest를 참고해서 BOM입력에 필요한 데이터를 넣어두자.

            bomService.register(entity);
        }
        return "redirect:/plan/bomRegister";
    }

}
