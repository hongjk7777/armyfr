package project1.armyfr.domain.member;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project1.armyfr.domain.Tag;

import javax.persistence.*;

@Entity
@Table(name = "member_tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberTag {

    @Id
    @GeneratedValue
    @Column(name = "member_tag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    public void linkMember(Member member) {
        this.member = member;
    }

    @Builder
    public MemberTag(Member member, Tag tag) {
        this.member = member;
        this.tag = tag;
    }
}
