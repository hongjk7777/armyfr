package project1.armyfr.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project1.armyfr.domain.Comment;
import project1.armyfr.domain.QComment;
import project1.armyfr.domain.member.Member;
import project1.armyfr.domain.member.QMember;
import project1.armyfr.domain.work.QWork;
import project1.armyfr.domain.work.Work;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentRepository {

    private final EntityManager em;
    private final JPAQueryFactory query;

    public Long save(Comment comment) {
        em.persist(comment);
        return comment.getId();
    }


    public List<Comment> findByMemberId(Member inputMember) {
        QComment comment = QComment.comment;
        QMember member = QMember.member;
        Long id = inputMember.getId();

        return query.select(comment)
                .from(comment)
                .join(comment.member, member)
                .where(member.id.eq(id))
                .limit(100)
                .fetch();
//        return em.createQuery("select c from Comment c where c.member_id = :id", Comment.class)
//                .setParameter("id", id)
//                .getResultList();
    }

    public List<Comment> findByWorkId(Work inputWork) {
        QComment comment = QComment.comment;
        QWork work = QWork.work;
        Long id = inputWork.getId();

        return query.select(comment)
                .from(comment)
                .join(comment.work, work)
                .where(work.id.eq(id))
                .limit(100)
                .fetch();
    }

    public Comment findById(Long id) {
        return em.find(Comment.class, id);
    }
}
