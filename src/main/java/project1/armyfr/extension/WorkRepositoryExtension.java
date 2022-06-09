package project1.armyfr.extension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import project1.armyfr.domain.work.Work;

import java.util.List;

@Transactional(readOnly = true)
public interface WorkRepositoryExtension {

    Page<Work> findByKeyword(String keyword, Pageable pageable);
}
