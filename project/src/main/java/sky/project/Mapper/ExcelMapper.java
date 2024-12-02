package sky.project.Mapper;

import org.apache.ibatis.annotations.Mapper;
import sky.project.DTO.ProductDTO;

@Mapper
public interface ExcelMapper {
    public int insertExcel(ProductDTO dto);
}
