package project1.armyfr.dto;

import project1.armyfr.domain.member.Region;

public class RegionChange {
    public Region findRegion(String region) {
        if (region.equals("seoul")) {
            return Region.SEOUL;
        } else if (region.equals("incheon")) {
            return Region.INCHEON;
        } else if (region.equals("daejeon")) {
            return Region.DAEJEON;
        } else if (region.equals("gwangju")) {
            return Region.GWANGJU;
        } else if (region.equals("daegu")) {
            return Region.DAEGU;
        } else if (region.equals("ulsan")) {
            return Region.ULSAN;
        } else if (region.equals("busan")) {
            return Region.BUSAN;
        } else if (region.equals("gyunggi")) {
            return Region.GYUNGGI;
        } else if (region.equals("gangwon")) {
            return Region.GANGWON;
        } else if (region.equals("chungcheong_s")) {
            return Region.CHUNGCHEONG_S;
        } else if (region.equals("chungcheong_n")) {
            return Region.CHUNGCHEONG_N;
        } else if (region.equals("jeonla_s")) {
            return Region.JEONLA_S;
        } else if (region.equals("jeonla_n")) {
            return Region.JEONLA_N;
        } else if (region.equals("gyungsang_s")) {
            return Region.GYUNGSANG_S;
        } else if (region.equals("gyungsang_n")) {
            return Region.GYUNGSANG_N;
        } else if (region.equals("jeju")) {
            return Region.JEJU;
        }
        else return Region.ERROR;
    }

    public String regionToString(Region region) {
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
