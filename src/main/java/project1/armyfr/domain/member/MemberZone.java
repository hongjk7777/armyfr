package project1.armyfr.domain.member;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project1.armyfr.domain.Zone;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberZone {

    @Id @GeneratedValue
    @Column(name = "member_zone_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Zone zone;

    @Builder
    public MemberZone(Member member, Zone zone) {
        this.member = member;
        this.zone = zone;
    }
}
