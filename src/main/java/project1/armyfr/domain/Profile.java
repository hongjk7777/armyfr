package project1.armyfr.domain;

import lombok.Data;
import project1.armyfr.domain.member.Member;
import project1.armyfr.domain.member.Region;

@Data
public class Profile {

    private String email;
    private String name;
    private String region;
    private Long imageId;

    public Profile(Member member) {
        this.email = member.getEmail();
        this.name = member.getName();
        this.region = regionToString(member.getRegion());
        this.imageId = member.getImageFileId();
    }

    private String regionToString(Region region) {
        if (region == Region.SEOUL) return "seoul";
        else if (region == Region.INCHEON) return "incheon";
        else if (region == Region.DAEJEON) return "daejeon";
        else if (region == Region.GWANGJU) return "gwangju";
        else if (region == Region.DAEGU) return "daegu";
        else if (region == Region.ULSAN) return "ulsan";
        else if (region == Region.BUSAN) return "busan";
        else if (region == Region.GYUNGGI) return "gyunggi";
        else if (region == Region.GANGWON) return "gangwon";
        else if (region == Region.CHUNGCHEONG_S) return "chungcheong_s";
        else if (region == Region.CHUNGCHEONG_N) return "chungcheong_n";
        else if (region == Region.JEONLA_S) return "jeonla_s";
        else if (region == Region.JEONLA_N) return "jeonla_n";
        else if (region == Region.GYUNGSANG_S) return "gyungsang_s";
        else if (region == Region.GYUNGSANG_N) return "gyungsang_n";
        else if (region == Region.JEJU) return "jeju";
        else return "error";
    }
}
