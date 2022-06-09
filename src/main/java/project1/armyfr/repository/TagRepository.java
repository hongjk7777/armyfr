package project1.armyfr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import project1.armyfr.domain.Tag;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Tag findByTitle(String title);
}
