package sky.project.Service;

import sky.project.DTO.BomDTO;

public interface BomService {

    //등록
    Long register(BomDTO dto);

    //수정
    void modify(BomDTO dto);

    //삭제
    void remove(Long BomId);

}
