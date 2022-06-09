package project1.armyfr.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project1.armyfr.domain.Zone;
import project1.armyfr.repository.ZoneRepository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ZoneService {

    private final ZoneRepository zoneRepository;

    @PostConstruct      //execute after ZoneService Bean
    public void initZoneData() throws IOException {
        if (zoneRepository.count() == 0) {
            Resource resource = new ClassPathResource("zones_kr.csv");
            List<Zone> zoneList = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8)
                    .stream().map(line -> {
                        String[] split = line.split(",");
                        return Zone.builder()
                                .cityName(split[0])
                                .cityKrName(split[1])
                                .province(split[2])
                                .build();

                    }).collect(Collectors.toList());

            zoneRepository.saveAll(zoneList);
        }
    }
}
