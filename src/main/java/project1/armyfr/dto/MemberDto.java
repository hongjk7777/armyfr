package project1.armyfr.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import project1.armyfr.domain.member.Member;
import project1.armyfr.domain.member.NormalMember;
import project1.armyfr.domain.member.Region;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MemberDto {

    private Long id;

    @NotEmpty(message = "이름은 필수입니다.")
    @Length(min = 2, max = 8, message = "이름은 2글자 이상 8글자 이하로 입력해주세요")
    private String name;

    @Email(message = "이메일 형식으로 입력하세요")
    @NotEmpty(message = "email은 필수입니다.")
    private String email;

    @NotEmpty(message = "password는 필수입니다")
    @Length(min = 8, max = 15, message = "비밀번호는 8글자 이상 15글자 이하로 입력해주세요")
    private String password;

    private Long fileId;

    //    private String regionStr;


//    private String phoneNum;

//    private String city;
//    private String street;
//    private String zipcode;

    @Builder
    public MemberDto(Long id, String name, String email, String password, Long fileId, String regionStr, String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.fileId = fileId;
//        this.phoneNum = phoneNum;
//        this.regionStr = regionStr;

//        this.city = city;
//        this.street = street;
//        this.zipcode = zipcode;
    }

    public MemberDto(Member member) {
        this.email = member.getEmail();
        this.name = member.getName();
//        this.regionStr = new RegionChange().regionToString(member.getRegion());
        this.fileId = member.getImageFileId();
    }



    public Member toEntity() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        password = passwordEncoder.encode(password);
        Member member = new NormalMember();
//        Region region = new RegionChange().findRegion(this.regionStr);
        member.setUp(name, email, password);
        member.generateEmailCheckToken();

        return member;
    }

    public boolean checkPWPattern(String password) {
        if (password.length() < 8 || password.length() > 15) {
            return false;
        } else return true;
    }
}
