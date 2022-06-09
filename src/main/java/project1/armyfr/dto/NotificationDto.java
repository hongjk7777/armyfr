package project1.armyfr.dto;

import lombok.*;
import project1.armyfr.domain.member.Member;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class NotificationDto {

    private boolean alarmE;
    private boolean alarmW;

    public NotificationDto(Member member) {
        this.alarmE = member.isAlarmE();
        this.alarmW = member.isAlarmW();
    }
}
