package project1.armyfr.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import project1.armyfr.domain.member.Member;
import project1.armyfr.service.MailService;
import project1.armyfr.service.MemberService;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;
    private final MemberService memberService;

    @Transactional
    @GetMapping("/mail/checkEmailToken")
    public String checkEmailToken(String token, String email, Model model) {
        Member member = memberService.findByEmail(email).get(0);
        if (member == null) {
            model.addAttribute("error", "wrong.email");
            return "mail/authFail";
        }

        if (!member.getEmailCheckToken().equals(token)) {
            model.addAttribute("error", "wrong.token");
            return "mail/authFail";
        }

        member.change(LocalDateTime.now(), true);
        return "mail/authSuccess";
    }
}
