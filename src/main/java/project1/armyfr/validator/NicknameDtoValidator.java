package project1.armyfr.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import project1.armyfr.domain.member.Member;
import project1.armyfr.dto.NicknameDto;
import project1.armyfr.repository.MemberRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NicknameDtoValidator implements Validator {

    private final MemberRepository memberRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return NicknameDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        NicknameDto nicknameDto = (NicknameDto) target;
        List<Member> memberList = memberRepository.findByName(nicknameDto.getName());
        if (memberList.size() != 0) {
            errors.rejectValue("name", "wrong.value", "이 닉네임은 사용할 수 없습니다.");
        }
    }
}
