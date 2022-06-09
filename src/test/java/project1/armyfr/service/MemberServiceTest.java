package project1.armyfr.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import project1.armyfr.domain.member.Member;
import project1.armyfr.domain.member.NormalMember;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    MemberService memberService;

    @Test
    public void joinTest() {
        Member member = new NormalMember();
        member.test("hong", "asdf");

        Long id = memberService.join(member);

        assertThat(member).isEqualTo(memberService.findOne(id));

    }

    @Test
    public void sameMemberTest() {
        Member member1 = new NormalMember();
        member1.test("hong", "asdf");
        Member member2 = new NormalMember();
        member2.test("hong", "as");

        memberService.join(member1);

        assertThrows(IllegalStateException.class, () ->
                memberService.join(member2));
    }
}