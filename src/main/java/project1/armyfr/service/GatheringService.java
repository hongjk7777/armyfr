package project1.armyfr.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project1.armyfr.domain.event.Enrollment;
import project1.armyfr.domain.event.Gathering;
import project1.armyfr.domain.member.Member;
import project1.armyfr.domain.work.Work;
import project1.armyfr.dto.GatheringDto;
import project1.armyfr.repository.EnrollmentRepository;
import project1.armyfr.repository.GatheringRepository;

import java.time.LocalDateTime;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class GatheringService {

    private final GatheringRepository gatheringRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public Gathering createGathering(Gathering gathering, Member member, Work work) {
        gathering.setup(member, work);
        return gatheringRepository.save(gathering);
    }

    public Gathering findGatheringById(Long id) {
        return gatheringRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("찾는 모임이 없습니다."));
    }

    @Transactional
    public void updateGathering(Gathering gathering, GatheringDto gatheringDto) {
        modelMapper.map(gatheringDto, gathering);
        gathering.acceptWaitingList();
    }

    @Transactional
    public void removeGathering(Gathering gathering) {
        gatheringRepository.delete(gathering);
    }

    @Transactional
    public void newEnrollment(Gathering gathering, Member member) {
        if (!enrollmentRepository.existsByMemberAndGathering(member, gathering)) {
            Enrollment enrollment = new Enrollment();
            enrollment.setEnrolledAt(LocalDateTime.now());
            enrollment.setAccepted(gathering.isAbleToAcceptNow());
            enrollment.setMember(member);
            gathering.addEnrollment(enrollment);
            enrollmentRepository.save(enrollment);
        }
    }

    @Transactional
    public void cancelEnrollment(Gathering gathering, Member member) {
        Enrollment enrollment = enrollmentRepository.findByMemberAndGathering(member, gathering);
        if (!enrollment.isAttended()) {
            gathering.removeEnrollment(enrollment);
            enrollmentRepository.delete(enrollment);
            gathering.acceptNextWaitingEnrollment();
        }

    }
}
