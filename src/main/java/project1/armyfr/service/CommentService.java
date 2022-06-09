package project1.armyfr.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project1.armyfr.domain.Comment;
import project1.armyfr.domain.member.Member;
import project1.armyfr.domain.work.Work;
import project1.armyfr.repository.CommentRepository;
import project1.armyfr.repository.MemberRepository;
import project1.armyfr.repository.WorkRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final WorkRepository workRepository;

    @Transactional
    public Long save(Comment comment) {
        commentRepository.save(comment);
        return comment.getId();
    }

    public Comment findOneById(Long id) {
        return commentRepository.findById(id);
    }

    public List<Comment> findByMember(Member member) {
        return commentRepository.findByMemberId(member);
    }

    public List<Comment> findByWork(Work work) {
        return commentRepository.findByWorkId(work);
    }
}
