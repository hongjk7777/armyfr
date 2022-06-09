package project1.armyfr.controller;

import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import project1.armyfr.annotation.CurrentUser;
import project1.armyfr.domain.Address;
import project1.armyfr.domain.file.MD5Generator;
import project1.armyfr.domain.member.Member;
import project1.armyfr.domain.member.NormalMember;
import project1.armyfr.dto.FileDto;
import project1.armyfr.dto.MemberDto;
import project1.armyfr.service.FileService;
import project1.armyfr.service.MailService;
import project1.armyfr.service.MemberService;

import javax.mail.SendFailedException;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MailService mailService;
    private final FileService fileService;

    @GetMapping("/user/signup")
    public String createForm(Model model) {
        model.addAttribute("memberDto", new MemberDto());
        return "user/signup";
    }

    @PostMapping("/user/signup")
    public String create(@Valid MemberDto memberDto, BindingResult result) {

        if (result.hasErrors()) {
            return "user/signup";
        }

        Member member = memberDto.toEntity();

        try {
            mailService.signUpEmailSender(member);
        } catch (MailException e) {
            FieldError error = new FieldError("memberDto", "email", "이메일 형식으로 입력하세요");
            result.addError(error);
            return "user/signup";
        }

        try {
            memberService.join(member);
        } catch (IllegalStateException e) {
            FieldError error = new FieldError("memberDto", "email", "이미 존재하는 이메일입니다");
            result.addError(error);
            return "user/signup";
        }


        return "redirect:/user/login";
    }


    // 로그인 페이지
    @GetMapping("/user/login")
    public String dispLogin(Model model, String result) {
        if ("fail".equals(result)) {
            model.addAttribute("result", result);
        } else {
            result = "success";
            model.addAttribute("result", result);
        }
        System.out.println();
        return "user/login";
    }

    // 로그인 결과 페이지
    @GetMapping("/user/login/result")
    public String dispLoginResult() {
        return "home";
    }

    @GetMapping("/user/login/sendMail")
    public String sendPasswordEmail(String email, Model model) {
        List<Member> memberList = memberService.findByEmail(email);
        if (memberList.isEmpty()) {
            String result = "noEmail";
            model.addAttribute("result", result);
        } else {
            mailService.changePwAndSend(memberList.get(0));
            String result = "success";
            model.addAttribute("result", result);
        }
        return "user/login";
    }

    @GetMapping("/profile/{memberEmail}")
    public String viewProfile(@PathVariable String memberEmail, @CurrentUser Member member,
                              Model model) {
        List<Member> memberList = memberService.findByEmail(memberEmail);
        if (memberList.size() == 0) {
            throw new IllegalArgumentException("해당하는 사용자가 없습니다.");
        }
        Member byEmail = memberList.get(0);

        model.addAttribute("member", byEmail);
        model.addAttribute("isOwner", member.getId().equals(byEmail.getId()));
        return "user/profile";
    }

//    //프로필 변경
//    @GetMapping("/user/profile")
//    public String createChangeForm(@CurrentUser Member member, Model model) {
//
//        model.addAttribute("memberDto", new MemberDto());
//        return "user/profile";
//    }
//
//    @PostMapping("/user/profile")
//    public String changeProfile(@RequestParam("files") MultipartFile files, MemberDto memberDto, Principal principal) throws IOException, NoSuchAlgorithmException {
//        String origFileName = files.getOriginalFilename();
//        String fileName = new MD5Generator(origFileName).toString();
//        String savePath = System.getProperty("user.dir") + "\\files";
//        if (!new File(savePath).exists()) {
//            try {
//                new File(savePath).mkdir();
//            } catch (Exception e) {
//                e.getStackTrace();
//            }
//        }
//
//        String filePath = savePath + "\\" + fileName;
//        File dest = new File(filePath);
//        files.transferTo(dest);
//
//        FileDto fileDto = new FileDto();
//        fileDto.setOrigFileName(origFileName);
//        fileDto.setFileName(fileName);
//        fileDto.setFilePath(filePath);
//
//        Long fileId = fileService.saveFile(fileDto);
//
//
//
//        memberDto.setFileId(fileId);
//        memberDto.setId(memberService.findOneByEmail(principal.getName()).getId());
//        //name address받아와야댐
//        memberService.changeProfile(memberDto);
//        return "redirect:/";
//    }


//    @RequestMapping(value = "/user/signup", method = RequestMethod.POST)
//    public String create(@Valid MemberDto memberForm, BindingResult result) {
//
//        System.out.println("");
//        if (result.hasErrors()) {
//            return "/signup";
//        }
//
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//
//        Address address = new Address(memberForm.getCity(), memberForm.getStreet(), memberForm.getZipcode());
//        Member member = new NormalMember();
//        member.setUp(memberForm.getEmail(), passwordEncoder.encode(memberForm.getPassword()));
//        member.change(memberForm.getName(), address);
//
//        memberService.join(member);
//
//        return "redirect:/user/login";
//    }
//
//
//    // 로그인 페이지
//    @GetMapping("/user/login")
//    public String dispLogin() {
//        return "/login";
//    }
//
//    // 로그인 결과 페이지
//    @GetMapping("/user/login/result")
//    public String dispLoginResult() {
//        return "/loginSuccess";
//    }

    // 로그아웃 결과 페이지
    @GetMapping("/user/logout/result")
    public String dispLogout() {
        return "/home";
    }

    // 접근 거부 페이지
    @GetMapping("/user/denied")
    public String dispDenied() {
        return "/user/denied";
    }

    // 내 정보 페이지
    @GetMapping("/user/info")
    public String dispMyInfo() {
        return "/user/myinfo";
    }

    // 어드민 페이지
    @GetMapping("/admin")
    public String dispAdmin() {
        return "/user/admin";
    }

}
