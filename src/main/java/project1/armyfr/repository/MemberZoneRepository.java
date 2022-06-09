package project1.armyfr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project1.armyfr.domain.member.MemberZone;

public interface MemberZoneRepository extends JpaRepository<MemberZone, Long> {
}
