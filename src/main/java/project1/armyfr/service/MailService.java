package project1.armyfr.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project1.armyfr.domain.member.Member;
import project1.armyfr.dto.MailDto;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private static final String FROM_ADDRESS = "hongjk777724@gmail.com";
    private final MemberService memberService;

    @Transactional
    public void signUpEmailSender(Member member) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(member.getEmail());
        mailMessage.setSubject("회원 가입 인증 이메일");
        mailMessage.setText("211.104.169.147:8080/mail/checkEmailToken?token=" + member.getEmailCheckToken()
                + "&email=" + member.getEmail());
        javaMailSender.send(mailMessage);

    }

    @Transactional
    public void changePwAndSend(Member member) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        PasswordGenerator passwordGenerator = new PasswordGenerator();
        String newPassword = passwordGenerator.generateStrongPassword();

        memberService.changePassword(member, newPassword);

        mailMessage.setTo(member.getEmail());
        mailMessage.setSubject("임시 비밀번호 생성");
        mailMessage.setText("회원님의 임시 비밀번호는 " + newPassword + "입니다.");
        javaMailSender.send(mailMessage);
    }


    public void emailSendByAlarm() {

    }


}
