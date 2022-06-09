package project1.armyfr.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag {

    @Id @GeneratedValue
    @Column(name = "tag_id")
    private Long id;

    private String title;

    @Builder
    public Tag(String title) {
        this.title = title;
    }

    //    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "parent_id")
//    private Tag parent;
//
//    @OneToMany(mappedBy = "parent")
//    private List<Tag> child;

}
