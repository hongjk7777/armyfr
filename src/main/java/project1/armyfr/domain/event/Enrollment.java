package project1.armyfr.domain.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import project1.armyfr.domain.member.Member;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Getter @Setter
@Entity @EqualsAndHashCode(of = "id")
public class Enrollment {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Member member;

    @ManyToOne
    private Gathering gathering;

    private LocalDateTime enrolledAt;

    private boolean accepted;

    private boolean attended;
}
