package project1.armyfr.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;
import project1.armyfr.domain.Tag;
import project1.armyfr.domain.Zone;
import project1.armyfr.domain.member.Region;
import project1.armyfr.domain.work.SubWork1;
import project1.armyfr.domain.work.Work;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@ToString
public class WorkDto {

    @NotEmpty(message = "path는 필수 입니다.")
    @Length(min = 2, max = 20)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{2,20}$")
    private String path;

    @NotEmpty(message = "이름은 필수입니다")
    private String title;

    @NotEmpty
    @Length(max = 100)
    private String content;

    @NotEmpty
    private String fullDescription;

    private String tags;
    private String zones;
    private String dueDate;
    private String region;
    private String address;
    private String phoneNum;
    private MultipartFile files;
//    private String city;
//    private String street;
//    private String zipcode;

    @Builder
    public void change(String title, String content, String tag, String dueDate,
                       String address, String region, String phoneNum) {
        this.title = title;
        this.content = content;
        this.tags = tag;
        this.dueDate = dueDate;
        this.address = address;
        this.region = region;
        this.phoneNum = phoneNum;
//        this.city = city;
//        this.street = street;
//        this.zipcode = zipcode;
    }


    public Work toEntity() {
        //transfer to region constant
        //Region region = new RegionChange().findRegion(this.region);
        //transfer string to LocalDate
        //LocalDate time = LocalDate.parse(dueDate, DateTimeFormatter.ISO_DATE);
        //create Adrees with 3 components
        Work work = new SubWork1().setup(path, title, content, address, phoneNum, fullDescription);

        return work;
    }



}
