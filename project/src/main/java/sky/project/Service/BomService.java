package sky.project.Service;

import sky.project.DTO.BomDTO;

import java.util.List;

public interface BomService {

    //등록
    Long register(BomDTO dto);

    //수정
    void modify(BomDTO dto);

    //삭제
    void remove(Long BomId);

    //상품코드로 불러오기
    List<BomDTO> findWithProductCode(String productCode);

}
