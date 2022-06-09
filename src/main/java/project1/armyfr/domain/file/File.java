package project1.armyfr.domain.file;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project1.armyfr.domain.member.Member;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File {

    @Id
    @GeneratedValue
    @Column(name = "file_id")
    private Long id;

    @NotEmpty(message = "origFileName이 있어야 합니다.")
    private String origFileName;

    @NotEmpty(message = "fileName이 있어야 합니다.")
    private String fileName;

    @NotEmpty(message = "filePath가 있어야 합니다.")
    private String filePath;

//    @OneToOne(mappedBy = "imageFile")
//    private Member member;

    @Builder
    public File(Long id, String origFileName, String fileName, String filePath) {
        this.id = id;
        this.origFileName = origFileName;
        this.fileName = fileName;
        this.filePath = filePath;
    }
}
