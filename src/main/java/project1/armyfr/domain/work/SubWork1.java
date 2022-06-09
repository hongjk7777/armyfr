package project1.armyfr.domain.work;

import lombok.Getter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@DiscriminatorValue("1")
public class SubWork1 extends Work{
}
