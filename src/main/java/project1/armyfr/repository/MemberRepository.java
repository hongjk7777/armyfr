package project1.armyfr.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project1.armyfr.domain.member.Member;
import project1.armyfr.dto.NotificationDto;
import project1.armyfr.dto.ProfileDto;
import project1.armyfr.dto.RegionChange;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }

    public List<Member> findByEmail(String email) {
        return em.createQuery("select m from Member m where m.email = :email", Member.class)
                .setParameter("email", email)
                .getResultList();
    }

    public Member findOneByEmail(String email) {
        return em.createQuery("select m from Member m where m.email = :email", Member.class)
                .setParameter("email", email)
                .getSingleResult();
    }

    public void deleteById(Long id) {
        Member member = findOne(id);
        em.remove(member);
    }

    public void deleteByEmail(String email) {
        Member member = findOneByEmail(email);
        em.remove(member);
    }


    public void flush() {
        em.flush();
    }

    public void changeProfile(Member member, ProfileDto profileDto) {
        member.changeAllProfile(profileDto.getName(), profileDto.getFileId(),
                profileDto.getIntroduce(), profileDto.getBelong(), profileDto.getActingArea());
        em.flush();
    }

    public void changePW(Member member, String password) {
        member.changePassword(password);
    }

    public void changeNotify(Member member, NotificationDto n_dto) {
        member.changeNotify(n_dto);
    }
}
