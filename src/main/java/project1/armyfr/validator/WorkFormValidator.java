package project1.armyfr.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import project1.armyfr.domain.work.Work;
import project1.armyfr.dto.WorkDto;
import project1.armyfr.repository.WorkRepository;

@RequiredArgsConstructor
@Component
public class WorkFormValidator implements Validator {

    private final WorkRepository workRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return WorkDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        WorkDto workDto = (WorkDto) target;
        if (workRepository.existsByPath(workDto.getPath())) {
            errors.rejectValue("path", "wrong.path", "불가능한 경로입니다.");
        }
    }
}
