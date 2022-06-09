package project1.armyfr.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import project1.armyfr.domain.work.Work;
import project1.armyfr.extension.WorkRepositoryExtension;

import javax.persistence.EntityManager;
import java.util.List;


@Transactional
public interface WorkRepository extends JpaRepository<Work, Long>, WorkRepositoryExtension {
    boolean existsByPath(String path);

//    @EntityGraph(value = "Study.withAll", type= EntityGraph.EntityGraphType.LOAD)
    Work findByPath(String path);

    //@EntityGraph(value = "Study.withTagsAndManagers", type = EntityGraph.EntityGraphType.FETCH)
    Work findMemberWithTagsByPath(String path);

    //@EntityGraph(value = "Study.withZonesAndManagers", type = EntityGraph.EntityGraphType.FETCH)//이건 좀더 공부
    Work findMemberWithZonesByPath(String path);

//    @EntityGraph(value = "Study.withManagers", type = EntityGraph.EntityGraphType.FETCH)
    Work findMemberWithManagersByPath(String path);

//    @EntityGraph(value = "Study.withMembers", type = EntityGraph.EntityGraphType.FETCH)
    Work findStudyWithMembersByPath(String path);

    @EntityGraph(attributePaths = {"zones", "tags"})
    List<Work> findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(boolean published, boolean closed);
}
