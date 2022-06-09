package project1.armyfr.domain.work;

import lombok.Getter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@DiscriminatorValue("2")
public class SubWork2 extends Work{
}
