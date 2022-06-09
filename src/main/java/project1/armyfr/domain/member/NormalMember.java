package project1.armyfr.domain.member;

import lombok.Getter;
import lombok.Setter;
import project1.armyfr.domain.Address;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@DiscriminatorValue("N")
public class NormalMember extends Member {


}

