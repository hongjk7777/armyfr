package project1.armyfr.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import project1.armyfr.domain.event.Gathering;
import project1.armyfr.dto.GatheringDto;

import java.time.LocalDateTime;

@Component
public class GatheringValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return GatheringDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        GatheringDto gatheringDto = (GatheringDto) target;

        if (!isValidStartDateTime(gatheringDto)) {
            errors.rejectValue("startDateTime", "올바른 정모 시작 일시를 입력해주세요");
        }

        if (!isValidEndEnrollmentDateTime(gatheringDto)) {
            errors.rejectValue("startDateTime", "올바른 정모 시작 일시를 입력해주세요");
        }

        if (!isValidEndDateTime(gatheringDto)) {
            errors.rejectValue("startDateTime", "올바른 정모 시작 일시를 입력해주세요");
        }
    }

    private boolean isValidStartDateTime(GatheringDto gatheringDto) {
        return !gatheringDto.getStartDateTime().isBefore(gatheringDto.getEndEnrollmentDateTime());
    }

    private boolean isValidEndEnrollmentDateTime(GatheringDto gatheringDto) {
        return !gatheringDto.getEndEnrollmentDateTime().isBefore(LocalDateTime.now());
    }

    private boolean isValidEndDateTime(GatheringDto gatheringDto) {
        return gatheringDto.getEndDateTime().isAfter(gatheringDto.getEndEnrollmentDateTime())
                && gatheringDto.getEndDateTime().isAfter(gatheringDto.getStartDateTime());
    }

    public void validateUpdateForm(GatheringDto gatheringDto, Gathering gathering, Errors error) {
        if (gatheringDto.getLimitOfEnrollments() < gathering.getNumberOfAcceptedEnrollments()) {
            error.rejectValue("limitOfEnrollments", "wrong.value", "이미 참석 확정이 된 사람보다 커야합니다.");
        }
    }
}
