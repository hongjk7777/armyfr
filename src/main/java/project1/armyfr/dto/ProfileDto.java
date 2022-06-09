package project1.armyfr.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;
import project1.armyfr.domain.member.Member;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProfileDto {

    @Length(max = 100, message = "100Byte이하만 가능합니다")
    private String introduce;

    @NotEmpty(message = "이름은 필수입니다.")
    @Length(min = 2, max = 8, message = "이름은 2글자 이상 8글자 이하로 입력해주세요")
    private String name;

    private Long fileId;
    private String actingArea;
    private String belong;
    private String regionStr;
    private MultipartFile files;

    public ProfileDto(Member member) {
        this.name = member.getName();
        this.regionStr = new RegionChange().regionToString(member.getRegion());
        this.fileId = member.getImageFileId();
        this.introduce = member.getIntroduce();
        this.belong = member.getBelong();
        this.actingArea = member.getActingArea();
    }
}
