package project1.armyfr.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project1.armyfr.domain.Comment;
import project1.armyfr.domain.member.Member;
import project1.armyfr.domain.member.NormalMember;
import project1.armyfr.domain.work.SubWork1;
import project1.armyfr.domain.work.Work;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CommentServiceTest {

    @Autowired
    CommentService commentService;
    @Autowired
    MemberService memberService;
    @Autowired
    WorkService workService;

    @Test
    public void saveComment() {
        Comment comment = new Comment();
        Long id = commentService.save(comment);

        assertThat(comment).isEqualTo(commentService.findOneById(id));
    }

    @Test
    void findByMemberId() {
        Member member = makeMember();
        Work work = makeWork();
        Comment comment = new Comment(member, work);
        commentService.save(comment);

        assertThat(commentService.findByMember(member).size()).isEqualTo(1);
    }

    @Test
    void findByWorkId() {
        Member member = makeMember();
        Work work = makeWork();
        Comment comment = new Comment(member, work);
        commentService.save(comment);

        assertThat(commentService.findByWork(work).size()).isEqualTo(1);
    }

    private Work makeWork() {
        Work work = new SubWork1();
        workService.save(work);
        return work;
    }

    private Member makeMember() {
        Member member = new NormalMember();
        member.test("hong", "asdf");
        memberService.join(member);
        return member;
    }
}