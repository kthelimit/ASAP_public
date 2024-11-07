package sky.project.Repository;

import sky.project.Entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByRoadNameContainingAndMainBuildingNumberAndSubBuildingNumber(
            String roadName, Integer mainBuildingNumber, Integer subBuildingNumber);

    List<Address> findByRoadNameContaining(String roadName);

    List<Address> findByRoadNameContainingAndMainBuildingNumber(String roadName, Integer mainBuildingNumber);
}
