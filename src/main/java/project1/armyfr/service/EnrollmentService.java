package project1.armyfr.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project1.armyfr.domain.event.Enrollment;
import project1.armyfr.domain.event.Gathering;
import project1.armyfr.repository.EnrollmentRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    public Enrollment findById(Long id) {
        return enrollmentRepository.findById(id).orElseThrow(()->
        new IllegalArgumentException("찾는 참가자가 없습니다."));
    }

    public void acceptEnrollment(Gathering gathering, Enrollment enrollment) {
        gathering.accept(enrollment);
    }

    public void rejectEnrollment(Gathering gathering, Enrollment enrollment) {
        gathering.reject(enrollment);
    }

    public void checkInEnrollment(Enrollment enrollment) {
        enrollment.setAttended(true);
    }

    public void cancelCheckInEnrollment(Enrollment enrollment) {
        enrollment.setAttended(false);
    }
}
