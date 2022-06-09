package project1.armyfr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project1.armyfr.domain.file.File;

public interface FileRepository extends JpaRepository<File, Long> {
}
