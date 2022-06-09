package project1.armyfr.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project1.armyfr.domain.file.File;
import project1.armyfr.dto.FileDto;
import project1.armyfr.repository.FileRepository;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;

    @Transactional
    public Long saveFile(FileDto fileDto) {
        return fileRepository.save(fileDto.toEntity()).getId();
    }

    public FileDto getFile(Long id) {
        if (id != null) {
            File file = fileRepository.findById(id).get();

            FileDto fileDto = FileDto.builder()
                    .id(file.getId())
                    .origFileName(file.getOrigFileName())
                    .fileName(file.getFileName())
                    .filePath(file.getFilePath())
                    .build();
            return fileDto;
        } else {
            return new FileDto();
        }
    }

//    public String getImgSrc(Long imageId) {
//        Optional<File> file = fileRepository.findById(imageId);
//        if (!file.isEmpty()) {
//            return file.get().getFilePath();
//        }
//        else return "";
//    }
}
