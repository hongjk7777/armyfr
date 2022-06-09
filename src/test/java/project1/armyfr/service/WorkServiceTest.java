package project1.armyfr.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project1.armyfr.domain.work.Work;
import project1.armyfr.domain.work.SubWork1;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class WorkServiceTest {

    @Autowired
    WorkService workService;

//    @Test
//    public void saveTest() {
//        Work work = new SubWork1();
//        work.change("잡일");
//
//        workService.save(work);
//
//        assertThat(workService.findAll().size()).isEqualTo(1);
//
//    }
}