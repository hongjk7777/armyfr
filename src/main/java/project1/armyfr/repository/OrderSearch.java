package project1.armyfr.repository;

import lombok.Getter;
import project1.armyfr.domain.OrderStatus;

@Getter
public class OrderSearch {

    private String memberName;
    private OrderStatus orderStatus;
}
