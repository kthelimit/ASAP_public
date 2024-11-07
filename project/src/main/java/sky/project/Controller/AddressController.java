package sky.project.Controller;

import sky.project.Entity.Address;
import sky.project.Repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/addresses")
public class AddressController {

    @Autowired
    private AddressRepository addressRepository;

    @GetMapping("/search")
    public String searchAddresses(@RequestParam String query, Model model) {
        List<Address> results = new ArrayList<>();

        // 검색 문자열을 공백 기준으로 나눕니다.
        String[] parts = query.split(" ");
        String roadName = null;
        Integer mainBuildingNumber = null;
        Integer subBuildingNumber = null;

        // 도로명, 건물번호 본번, 부번을 구분
        if (parts.length > 0) {
            roadName = parts[0]; // 도로명

            if (parts.length > 1) {
                String[] buildingParts = parts[1].split("-");
                mainBuildingNumber = Integer.parseInt(buildingParts[0]); // 본번
                if (buildingParts.length > 1) {
                    subBuildingNumber = Integer.parseInt(buildingParts[1]); // 부번
                }
            }
        }

        if(subBuildingNumber == null) {
            subBuildingNumber = 0;
        }

        // 도로명 검색
        if (roadName != null) {
            // 도로명 + 건물번호 본번 + 부번 검색
            if (mainBuildingNumber != null && subBuildingNumber != null) {
                results.addAll(addressRepository.findByRoadNameContainingAndMainBuildingNumberAndSubBuildingNumber(
                        roadName, mainBuildingNumber, subBuildingNumber));
            }else{
                results.addAll(addressRepository.findByRoadNameContaining(roadName));
            }
        }

        model.addAttribute("addressList", results); // 결과를 모델에 추가
        return "/users/Address"; // 템플릿 이름
    }

}
