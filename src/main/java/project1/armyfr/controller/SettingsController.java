package project1.armyfr.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import project1.armyfr.annotation.CurrentUser;
import project1.armyfr.domain.Tag;
import project1.armyfr.domain.Zone;
import project1.armyfr.domain.file.MD5Generator;
import project1.armyfr.domain.member.Member;
import project1.armyfr.dto.*;
import project1.armyfr.repository.TagRepository;
import project1.armyfr.repository.ZoneRepository;
import project1.armyfr.service.FileService;
import project1.armyfr.service.MemberService;
import project1.armyfr.service.TagService;
import project1.armyfr.validator.NicknameDtoValidator;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    private final FileService fileService;
    private final MemberService memberService;
    private final TagService tagService;
    private final TagRepository tagRepository;
    private final ObjectMapper objectMapper;
    private final ZoneRepository zoneRepository;
    private final NicknameDtoValidator nicknameDtoValidator;
    private final ModelMapper modelMapper;

    @InitBinder("nicknameDto")
    public void nicknameDtoInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(nicknameDtoValidator);
    }

    //프로필 변경

    @GetMapping("/settings")
    public String settingDefault() {
        return "redirect:/settings/profile";
    }

    @GetMapping("/settings/profile")
    public String createChangeForm(@CurrentUser Member member, Model model) {
        Member findMember = memberService.findOne(member.getId());
        model.addAttribute("member", findMember);
        model.addAttribute("profileDto", new ProfileDto(findMember));
//        FileDto fileDto;
//        if (!member.getImageFileId().equals(null)) {
//            fileDto = fileService.getFile(member.getImageFileId());
//        } else { fileDto = new FileDto();}
        FileDto fileDto = fileService.getFile(findMember.getImageFileId());
        fileDto.setFilePath("/img/" + fileDto.getFileName());
        model.addAttribute("fileDto", fileDto);
        return "settings/profile";
    }

    @PostMapping("/settings/profile")
    public String changeProfile(@Valid ProfileDto profileDto, BindingResult result, @CurrentUser Member member) throws IOException, NoSuchAlgorithmException {

        MultipartFile files = profileDto.getFiles();
        if (result.hasErrors()) {
            return "settings/profile";
        }

        if (!profileDto.getFiles().getOriginalFilename().equals("")) {
            String origFileName = files.getOriginalFilename();
            String fileName = new MD5Generator(origFileName).toString() + ".png";
            String savePath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\img";

            if (!new File(savePath).exists()) {
                try {
                    new File(savePath).mkdir();
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }

            String filePath = savePath + "\\" + fileName;
            File dest = new File(filePath);
            files.transferTo(dest);

            FileDto fileDto = new FileDto();
            fileDto.setOrigFileName(origFileName);
            fileDto.setFileName(fileName);
            fileDto.setFilePath(filePath);

            Long fileId = fileService.saveFile(fileDto);


            profileDto.setFileId(fileId);
        }

        //name address받아와야댐
        memberService.changeProfile(member.getId(), profileDto);
        return "redirect:/settings/profile";
    }

    @GetMapping("/settings/checkPassword")
    public String createPasswordForm(Model model) {
        model.addAttribute("memberDto", new MemberDto());
        return "settings/checkPassword";
    }

    @PostMapping("/settings/checkPassword")
    public String checkPassword(Model model, MemberDto memberDto, @CurrentUser Member member, BindingResult result) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        Member findMember = memberService.findOne(member.getId());

        if (!passwordEncoder.matches(memberDto.getPassword(), findMember.getPassword())) {
            FieldError error = new FieldError("memberDto", "password", "비밀번호가 틀립니다.");
            result.addError(error);
            return "settings/checkPassword";
        }

        return "settings/changePassword";
    }

    @GetMapping("/settings/changePassword")
    public String changePasswordForm(Model model) {
        model.addAttribute("memberDto", new MemberDto());
        return "settings/changePassword";
    }

    @PostMapping("/settings/changePassword")
    public String changePassword(MemberDto memberDto, @CurrentUser Member member, BindingResult result, @RequestParam("passwordCheck") String passwordCheck) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Member findMember = memberService.findOne(member.getId());

        if (!memberDto.getPassword().equals(passwordCheck)) {
            FieldError error = new FieldError("memberDto", "password", "비밀번호가 일치하지 않습니다");
            result.addError(error);
            return "/settings/changePassword";
        }

        if (!memberDto.checkPWPattern(memberDto.getPassword())) {
            result.addError(new FieldError("memberDto", "password", "패스워드는 8자 이상 15자 이하로 해주세요"));
            return "/settings/changePassword";
        }

        if (passwordEncoder.matches(memberDto.getPassword(), findMember.getPassword())) {
            FieldError error = new FieldError("memberDto", "password", "지금 비밀번호와 같습니다");
            result.addError(error);
            return "settings/changePassword";
        }

        memberService.changePW(member.getId(), passwordEncoder.encode(memberDto.getPassword()));

        return "redirect:/settings";
    }

    @GetMapping("/settings/notifications")
    public String notificationsSetting(Model model, @CurrentUser Member member) {
        Member findMember = memberService.findOne(member.getId());
        model.addAttribute("notificationDto", new NotificationDto(findMember));
        return "settings/notifications";
    }

    @PostMapping("/settings/notifications")
    public String changeNotificationSetting(NotificationDto notificationDto, @CurrentUser Member member) {
        Member findMember = memberService.findOne(member.getId());
        memberService.changeNotify(findMember.getId(), notificationDto);

        return "redirect:/settings";
    }

    @GetMapping("/settings/tags")
    public String startTagSettings(Model model, @CurrentUser Member member) throws JsonProcessingException {
        Member findMember = memberService.findOne(member.getId());
        model.addAttribute("member", findMember);
        List<Tag> tags = memberService.getTags(findMember);
        model.addAttribute("tags", tags.stream().map(Tag::getTitle).collect(Collectors.toList()));

        List<String> allTags = tagRepository.findAll().stream()
                .map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTags));

        return "settings/tags";
    }

    @ResponseBody
    @PostMapping("/settings/tags/add")
    public ResponseEntity<?> addTagToDB(@CurrentUser Member member, @RequestBody TagDto tagDto) {
        Member findMember = memberService.findOne(member.getId());
        String title = tagDto.getTagTitle();
        Tag tag = tagService.findOrCreateNew(title);
        memberService.addTag(findMember, tag);

        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @PostMapping("/settings/tags/remove")
    public ResponseEntity<?> removeTagToDB(@CurrentUser Member member, @RequestBody TagDto tagDto) {
        Member findMember = memberService.findOne(member.getId());
        String title = tagDto.getTagTitle();
        Tag tag = tagRepository.findByTitle(title);
        if (!tag.getTitle().equals("wrong title")) {
            memberService.deleteTag(findMember, tag);
//            tagRepository.delete(tag);
        } else {
            ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @PostMapping("/settings/tags/autoComplete")
    public ResponseEntity<?> autoCompleteTag(@RequestBody TagDto tagDto) {
        String keyword = tagDto.getTagTitle();

        return ResponseEntity.ok().build();
    }

    @GetMapping("/settings/zones")
    public String zoneSetting(Model model, @CurrentUser Member member) throws JsonProcessingException {
        List<Zone> zoneList = memberService.getZones(member);
        model.addAttribute("zones", zoneList.stream().map(Zone::getCityKrName).collect(Collectors.toList()));

        List<String> allZones = zoneRepository.findAll().stream().map(zone -> zone.getCityKrName())
                .collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allZones));
        return "settings/zones";
    }

    @ResponseBody
    @PostMapping("/settings/zones/add")
    public ResponseEntity<?> addZoneTag(@CurrentUser Member member, @RequestBody ZoneDto zoneDto) {
        String cityKrName = zoneDto.getCityKrName();
        Zone zone = Zone.builder().cityKrName(cityKrName).build();
        memberService.addZone(member, zone);
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @PostMapping("/settings/zones/remove")
    public ResponseEntity<?> removeZoneTag(@CurrentUser Member member, @RequestBody ZoneDto zoneDto) {
        String cityKrName = zoneDto.getCityKrName();
        Optional<Zone> zone = zoneRepository.findByCityKrName(cityKrName);
        memberService.deleteZone(member, zone.orElseThrow());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/settings/account")
    public String updateMemberForm(@CurrentUser Member member, Model model) {
        Member findMember = memberService.findOne(member.getId());
        model.addAttribute(findMember);
        NicknameDto nicknameDto = modelMapper.map(findMember, NicknameDto.class);
        model.addAttribute("nicknameDto", nicknameDto);
        return "settings/account";
    }

    @PostMapping("/settings/account")
    public String updateMemberNickname(@CurrentUser Member member, @Valid NicknameDto nicknameDto,
                                       Errors errors, Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(member);
            return "settings/account";
        }

        String newName = nicknameDto.getName();
        memberService.updateName(member, newName);
        attributes.addFlashAttribute("message", "닉네임을 수정했습니다.");
        return "redirect:/settings/profile";
    }

}
