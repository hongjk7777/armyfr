package project1.armyfr.domain.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project1.armyfr.domain.UserAccount;
import project1.armyfr.domain.member.Member;
import project1.armyfr.domain.work.Work;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter @Setter
@NoArgsConstructor @EqualsAndHashCode(of = "id")
@NamedEntityGraph(
        name = "Event.withEnrollments",
        attributeNodes = @NamedAttributeNode("enrollments")
)
public class Gathering {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Member member;

    @ManyToOne
    private Work work;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    @Column(nullable = false)
    private LocalDateTime endEnrollmentDateTime;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Column
    private Integer limitOfEnrollments;

    @OneToMany(mappedBy = "gathering")
    @OrderBy("enrolledAt")
    private List<Enrollment> enrollments = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    public void setup(Member member, Work work) {
        this.member = member;
        this.work = work;
        this.createdDateTime = LocalDateTime.now();
    }

    public boolean isEnrollableFor(UserAccount userAccount) {
        return isNotClosed() && !this.isAttended(userAccount) && !isAlreadyEnrolled(userAccount);
    }

    private boolean isNotClosed() {
        return this.endEnrollmentDateTime.isAfter(LocalDateTime.now());
    }

    public boolean isDisenrollableFor(UserAccount userAccount) {
        return isNotClosed() && !this.isAttended(userAccount) && isAlreadyEnrolled(userAccount);
    }

    public boolean isAttended(UserAccount userAccount) {
        Member member = userAccount.getMember();
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getMember().getId().equals(member.getId())&&
                    enrollment.isAttended()) {
                return true;
            }
        }
        return false;
    }

    private boolean isAlreadyEnrolled(UserAccount userAccount) {
        Member member = userAccount.getMember();
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getMember().getId().equals(member.getId())) {
                return true;
            }
        }
        return false;
    }

    public int numberOfRemainSpots() {
        return this.limitOfEnrollments - (int)this.enrollments.stream().filter(Enrollment::isAccepted).count();
    }

    public long getNumberOfAcceptedEnrollments() {
        return enrollments.stream().filter(Enrollment::isAccepted).count();
    }

    public boolean canAccept(Enrollment enrollment) {
        return this.eventType == EventType.CONFIRMATIVE
                && this.enrollments.contains(enrollment)
                && !enrollment.isAttended()
                && !enrollment.isAccepted();
    }

    public boolean canReject(Enrollment enrollment) {
        return this.eventType == EventType.CONFIRMATIVE
                && this.enrollments.contains(enrollment)
                && !enrollment.isAttended()
                && enrollment.isAccepted();
    }

    public boolean isAbleToAcceptNow() {
        return eventType == EventType.FCFS
                && limitOfEnrollments > getNumberOfAcceptedEnrollments();
    }

    public void addEnrollment(Enrollment enrollment) {
        enrollment.setGathering(this);
        this.enrollments.add(enrollment);
    }

    public void removeEnrollment(Enrollment enrollment) {
        enrollments.remove(enrollment);
        enrollment.setGathering(null);
    }

    public void acceptNextWaitingEnrollment() {
        if (this.isAbleToAcceptNow()) {
            Enrollment nextWaiting = getNextWaiting();
            if (nextWaiting != null) {
                nextWaiting.setAccepted(true);
            }
        }
    }

    private Enrollment getNextWaiting() {
        for (Enrollment enrollment : enrollments) {
            if (!enrollment.isAccepted())
                return enrollment;
        }
        return null;
    }

    public void acceptWaitingList() {
        if (this.isAbleToAcceptNow()) {
            List<Enrollment> waitingList = getWaitingList();
            int numToAccept = (int) Math.min(limitOfEnrollments - getNumberOfAcceptedEnrollments(),
                    waitingList.size());
            waitingList.subList(0, numToAccept).forEach(enrollment -> enrollment.setAccepted(true));
        }
    }

    private List<Enrollment> getWaitingList() {
        return this.enrollments.stream().filter(enrollment -> !enrollment.isAccepted())
                .collect(Collectors.toList());
    }

    public void accept(Enrollment enrollment) {
        if (this.eventType == EventType.CONFIRMATIVE
                && this.limitOfEnrollments > this.getNumberOfAcceptedEnrollments()) {
            enrollment.setAccepted(true);
        }
    }

    public void reject(Enrollment enrollment) {
        if (this.eventType == EventType.CONFIRMATIVE) {
            enrollment.setAccepted(false);
        }
    }
}
