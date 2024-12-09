package sky.project.ServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sky.project.DTO.ReturnDTO;
import sky.project.Entity.CurrentStatus;
import sky.project.Entity.Import;
import sky.project.Entity.Returns;
import sky.project.Repository.ImportRepository;
import sky.project.Repository.ReturnRepository;
import sky.project.Service.ReturnService;

@Slf4j
@Service
public class ReturnServiceImpl implements ReturnService {

    @Autowired
    private ReturnRepository returnRepository;
    @Autowired
    private ImportRepository importRepository;

    @Override
    public Long register(ReturnDTO dto) {

        Returns entity = dtoToEntity(dto);
        returnRepository.save(entity);
        return entity.getReturnId();
    }

    @Override
    public ReturnDTO findByImportId(Long importId){
        return entityToDto(returnRepository.findByImportId(importId));
    }

    private Returns dtoToEntity(ReturnDTO dto) {

        Import imp = importRepository.findById(dto.getImportId()).orElse(null);

        return Returns.builder()
                .returnId(dto.getReturnId())
                .myImport(imp)
                .quantity(dto.getQuantity())
                .status(CurrentStatus.ON_HOLD)
                .build();
    }

    private ReturnDTO entityToDto(Returns entity) {
        ReturnDTO dto = ReturnDTO.builder()
                .returnId(entity.getReturnId())
                .importId(entity.getMyImport().getImportId())
                .quantity(entity.getQuantity())
                .status(entity.getStatus().name())
                .build();
        return dto;
    }
}
