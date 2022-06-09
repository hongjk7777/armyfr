package project1.armyfr.domain.member;

import lombok.Getter;
import lombok.NoArgsConstructor;
import project1.armyfr.domain.Comment;
import project1.armyfr.domain.Tag;
import project1.armyfr.domain.Zone;
import project1.armyfr.domain.work.Work;
import project1.armyfr.dto.NotificationDto;
import project1.armyfr.service.MemberRole;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@NoArgsConstructor
public abstract class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String email;       //this is real id
    private String password;
    private String name;

//    @OneToMany(mappedBy = "member")
//    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberTag> memberTags;

    private String belong;
    private String introduce;
    private String actingArea;
//    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinColumn(name = "file_id")
    private Long imageFileId;

    private String address;

    private boolean emailVerified = false;
    private String emailCheckToken;

    private boolean alarmE = false;

    private boolean alarmW = false;

    @Enumerated(EnumType.STRING)
    private Region region;

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole = MemberRole.MEMBER;

    private LocalDateTime signUpTime;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberZone> zoneList;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_temp_zone")
    private List<Zone> tempZones = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_temp_tag")
    private List<Tag> tempTags = new ArrayList<>();

    public void addMemberTag(MemberTag memberTag){
        memberTags.add(memberTag);
        memberTag.linkMember(this);
    }


    public void setUp(String name, String email, String password) {
        this.email = email;
        this.password = password;
        this.name = name;
//        this.region = region;
    }

    public void test(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public void change(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public void change(LocalDateTime time, Boolean emailVerified) {
        this.signUpTime = time;
        this.emailVerified = emailVerified;
    }

    public void changeAllProfile(String name, Long imageFileId,
                                 String introduce, String belong, String actingArea) {
        this.name = name;
        this.imageFileId = imageFileId;
        this.introduce = introduce;
        this.belong = belong;
        this.actingArea = actingArea;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void changeNotify(NotificationDto n_dto) {
        this.alarmE = n_dto.isAlarmE();
        this.alarmW = n_dto.isAlarmW();
    }

    public void becomeAdmin() {
        this.memberRole = MemberRole.ADMIN;
    }

    //임의의 이메일 확인을 위한 토큰 생성
    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
    }


    public MemberTag findMemberTag(Tag tag) {
        for (int i = 0; i < memberTags.size(); i++) {
            if (memberTags.get(i).getTag().getTitle().equals(tag.getTitle())) {
                return memberTags.get(i);
            }
        }
        return null;
    }

    public boolean isMyTag(Tag tag) {
        for (int i = 0; i < memberTags.size(); i++) {
            if (memberTags.get(i).getTag().getTitle().equals(tag.getTitle())) {
                return true;
            }
        }
        return false;
    }

    public void deleteTag(Tag tag) {
        for (int i = 0; i < memberTags.size(); i++) {
            if (memberTags.get(i).getTag().getTitle().equals(tag.getTitle())) {
                memberTags.remove(i);
            }
        }
    }

    public List<MemberTag> getTags() {
        return memberTags;
    }


    public void addMemberZone(MemberZone memberZone){
        zoneList.add(memberZone);
    }

    public MemberZone findMemberZone(Zone zone) {
        for (int i = 0; i < zoneList.size(); i++) {
            if (zoneList.get(i).getZone().getCityKrName().equals(zone.getCityKrName())) {
                return zoneList.get(i);
            }
        }
        return null;
    }

    public boolean isMyZone(Zone zone) {
        for (int i = 0; i < zoneList.size(); i++) {
            if (zoneList.get(i).getZone().getCityKrName().equals(zone.getCityKrName())) {
                return true;
            }
        }
        return false;
    }

    public void deleteZone(Zone zone) {
        for (int i = 0; i < zoneList.size(); i++) {
            if (zoneList.get(i).getZone().getCityKrName().equals(zone.getCityKrName())) {
                zoneList.remove(i);
            }
        }
    }

    public List<MemberZone> getZones() {
        return zoneList;
    }

    public void refreshTempZones() {
        tempZones.clear();
    }

    public void refreshTempTags() {
        tempTags.clear();
    }

    public boolean isManagerOf(Work work) {
        if (work.isManagerMember(this) == true) {
            return true;
        } else {
            return false;
        }

    }

    public void changeName(String newName) {
        this.name = newName;
    }
}
