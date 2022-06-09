package project1.armyfr.domain.work;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import project1.armyfr.domain.Tag;
import project1.armyfr.domain.Comment;
import project1.armyfr.domain.UserAccount;
import project1.armyfr.domain.Zone;
import project1.armyfr.domain.member.Member;
import project1.armyfr.domain.member.Region;

import javax.persistence.*;
import java.awt.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NamedEntityGraph(name = "Study.withAll", attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("zones"),
        @NamedAttributeNode("managers"),
        @NamedAttributeNode("members")})
@NamedEntityGraph(name = "Study.withTagsAndManagers", attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("managers")
})
@NamedEntityGraph(name = "Study.withZonesAndManagers", attributeNodes = {
        @NamedAttributeNode("zones"),
        @NamedAttributeNode("managers")
})
@NamedEntityGraph(name = "Study.withManagers", attributeNodes = {
        @NamedAttributeNode("managers")
})
//@NamedEntityGraph(name = "Study.withMembers", attributeNodes = {
//        @NamedAttributeNode("members")
//})
@Entity
@Getter @Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@EqualsAndHashCode(of = "id")
@DiscriminatorColumn(name = "dtype")
public abstract class Work {

    @Id @GeneratedValue
    @Column(name = "work_id")
    private Long id;

    @ManyToMany
    private List<Member> managers = new ArrayList<>();

    @ManyToMany
    private List<Member> members = new ArrayList<>();

    @Column(unique = true)
    private String path;

    private String title;
    private String content;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String fullDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String image;

    @ManyToMany
    @JoinColumn(name = "work_tag")
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    @JoinColumn(name = "work_zone")
    private Set<Zone> zones = new HashSet<>();

    private Region region;
    private String address;

    private String phoneNum;

    private LocalDateTime publishedDateTime;

    private LocalDateTime closedDateTime;

    private LocalDateTime recruitingUpdatedDateTime;        // 인원 모집 기간 제한

    private boolean recruiting;      // 모집 중인지 여부

    private boolean published;

    private boolean closed;

    private boolean useBanner = false;
//    private List<File> files;
//    private File file;

    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private LocalDate dueDate;

//    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL)
//    private List<Tag> categories = new ArrayList<>();

    @OneToMany(mappedBy = "work")
    private List<Comment> comments = new ArrayList<>();

//    private int memberCount = 0;

    public void change(String name) {
        this.title = name;
    }

    public void change(String name, String explain, LocalDate workDate) {
        this.title = name;
        this.content = explain;
        this.dueDate = workDate;
    }

    public Work setup(String path, String title, String content, String address, String phoneNum, String fullDescription) {
        this.path = path;
        this.title = title;
        this.content = content;
//        this.tag = tag;
//        this.region = region;
//        this.dueDate = dueDate;
        this.address = address;
        this.phoneNum = phoneNum;
        this.fullDescription = fullDescription;
        return this;
    }

    public void changeAll(String title, String content, Tag tag, Region region, String address, LocalDate dueDate) {
        this.title = title;
        this.content = content;
//        this.tag = tag;
//        this.region = region;
        this.address = address;
        this.dueDate = dueDate;
    }

    public void changeTags(List<Tag> tags, List<Zone> zones) {
        this.tags = tags.stream().collect(Collectors.toSet());
        this.zones = zones.stream().collect(Collectors.toSet());
    }

    public void addManager(Member member) {
        this.managers.add(member);
    }

    public boolean isJoinable(UserAccount userAccount) {
        Member member = userAccount.getMember();
        return this.isPublished() && this.isRecruiting()
                && !this.members.contains(member)
                && !this.managers.contains(member);
    }

    public boolean isMember(UserAccount userAccount) {
        for (int i = 0; i < members.size(); i++) {
            if (this.members.get(i).getId().equals(userAccount.getMember().getId())) {
                return true;
            }
        }
        return false;
    }

    public boolean isManager(UserAccount userAccount) {
        for (int i = 0; i < managers.size(); i++) {
            if (this.managers.get(i).getId().equals(userAccount.getMember().getId())) {
                return true;
            }
        }
        return false;
    }

    public boolean isManagerMember(Member member) {
        for (int i = 0; i < managers.size(); i++) {
            if (this.managers.get(i).getId().equals(member.getId())) {
                return true;
            }
        }
        return false;
    }


    public void publish(){
        if (this.closed || this.published) {
            throw new RuntimeException("모임을 공개할 수 없는 상태입니다. 모임을 이미 공개했거나 이미 종료했습니다.");
        } else {
            this.published = true;
            this.publishedDateTime = LocalDateTime.now();
        }
    }

    public void close(){
        if (this.closed || !this.published) {
            throw new RuntimeException("모임을 종료할 수 없는 상태입니다. 모임을 이미 종료했거나 공개하지 않았습니다.");
        }
        else{
            this.closed = true;
            this.closedDateTime = LocalDateTime.now();
        }


    }

    public boolean canUpdateRecruiting() {
        if (this.published && this.recruitingUpdatedDateTime == null
            || this.recruitingUpdatedDateTime.isBefore(LocalDateTime.now().minusHours(1))) {
            return true;
        }
        return false;
    }

    public void startRecruit() {
        if (canUpdateRecruiting()) {
            this.recruiting = true;
            this.recruitingUpdatedDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("인원 모집시작이 불가합니다. 설정 변경시점으로 부터 1시간 뒤 다시 시도해주세요");
        }

    }

    public void stopRecruit() {
        if (canUpdateRecruiting()) {
            this.recruiting = false;
            this.recruitingUpdatedDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("인원 모집중지가 불가합니다. 설정 변경시점으로 부터 1시간 뒤 다시 시도해주세요");
        }
    }

    public void updatePath(String newPath) {
        this.path = newPath;
    }

    public void updateTitle(String newTitle) {
        this.title = newTitle;
    }

    public boolean isRemovable() {
        return !this.published; //모집을 했던 모임은 삭제 불능(다른 분들도 잇음)
    }

    public String getEncodedPath() {
        return URLEncoder.encode(this.path, StandardCharsets.UTF_8);
    }

    public void addMember(Member member) {
        this.getMembers().add(member);
//        this.memberCount++;
    }

    public void removeMember(Member member) {
        this.getMembers().remove(member);
//        this.memberCount--;
    }
}
