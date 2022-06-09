package project1.armyfr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project1.armyfr.domain.member.MemberTag;

public interface MemberTagRepository extends JpaRepository<MemberTag, Long> {
}
