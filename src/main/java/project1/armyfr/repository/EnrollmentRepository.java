package project1.armyfr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project1.armyfr.domain.event.Enrollment;
import project1.armyfr.domain.event.Gathering;
import project1.armyfr.domain.member.Member;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByMemberAndGathering(Member member, Gathering gathering);

    Enrollment findByMemberAndGathering(Member member, Gathering gathering);
}
