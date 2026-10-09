
package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import entity.AadhaarCard;

public interface AadhaarRepo
        extends JpaRepository<AadhaarCard, Integer> {
}
