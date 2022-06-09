package project1.armyfr.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Zone {


    @Id @GeneratedValue
    @Column(name = "zone_id")
    private Long id;

    private String cityName;
    private String cityKrName;
    private String province;

    @Builder
    public Zone(String cityName, String cityKrName, String province) {
        this.cityName = cityName;
        this.cityKrName = cityKrName;
        this.province = province;
    }
}
