package project1.armyfr.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import project1.armyfr.domain.event.Gathering;
import project1.armyfr.domain.work.Work;

import java.util.List;

public interface GatheringRepository extends JpaRepository<Gathering, Long> {

    @EntityGraph(value = "Event.withEnrollments", type = EntityGraph.EntityGraphType.LOAD)
    List<Gathering> findByWorkOrderByStartDateTime(Work work);
}
