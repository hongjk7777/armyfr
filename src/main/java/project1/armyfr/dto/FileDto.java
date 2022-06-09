package project1.armyfr.dto;

import lombok.*;
import project1.armyfr.domain.file.File;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class FileDto {
    private Long id;
    private String origFileName;
    private String fileName;
    private String filePath;

    public File toEntity() {
        File build = File.builder()
                .id(id)
                .origFileName(origFileName)
                .fileName(fileName)
                .filePath(filePath)
                .build();
        return build;
    }

    @Builder
    public FileDto(Long id, String origFileName, String fileName, String filePath) {
        this.id = id;
        this.origFileName = origFileName;
        this.fileName = fileName;
        this.filePath = filePath;
    }
}
