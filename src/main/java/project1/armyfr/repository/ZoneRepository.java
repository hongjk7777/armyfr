package project1.armyfr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project1.armyfr.domain.Zone;

import java.util.Optional;

public interface ZoneRepository extends JpaRepository<Zone, Long> {

    Optional<Zone> findByCityKrName(String cityKrName);
}
