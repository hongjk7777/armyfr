package project1.armyfr.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.dom4j.dom.DOMText;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import project1.armyfr.annotation.CurrentUser;
import project1.armyfr.domain.Address;
import project1.armyfr.domain.Tag;
import project1.armyfr.domain.Zone;
import project1.armyfr.domain.member.Member;
import project1.armyfr.domain.member.Region;
import project1.armyfr.domain.work.SubWork1;
import project1.armyfr.domain.work.Work;
import project1.armyfr.dto.TagDto;
import project1.armyfr.dto.WorkDto;
import project1.armyfr.dto.ZoneDto;
import project1.armyfr.repository.TagRepository;
import project1.armyfr.repository.WorkRepository;
import project1.armyfr.repository.ZoneRepository;
import project1.armyfr.service.MemberService;
import project1.armyfr.service.TagService;
import project1.armyfr.service.WorkService;
import project1.armyfr.validator.WorkFormValidator;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class WorkController {

    private final WorkService workService;
    private final MemberService memberService;
    private final TagRepository tagRepository;
    private final TagService tagService;
    private final ZoneRepository zoneRepository;
    private final ObjectMapper objectMapper;
    private final WorkFormValidator workFormValidator;
    private final WorkRepository workRepository;

    @GetMapping("/work/create")
    public String register(Model model, @CurrentUser Member member) throws JsonProcessingException {
        List<String> whiteTagList = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
        List<String> whiteZoneList = zoneRepository.findAll().stream().map(Zone::getCityKrName).collect(Collectors.toList());

        memberService.refreshTemp(member);

        model.addAttribute("workDto", new WorkDto());
        model.addAttribute("whiteTagList", objectMapper.writeValueAsString(whiteTagList));
        model.addAttribute("whiteZoneList", objectMapper.writeValueAsString(whiteZoneList));
        return "/work/create";
    }

    @PostMapping("/work/create")
    public String create(WorkDto workDto, @CurrentUser Member member, Model model, BindingResult result) {
        if (result.hasErrors()) {
            model.addAttribute(member);
            return "/work/create";
        }

//        Work work = new SubWork1();     //여기를 카테고리별로 ㄱㄱ

        Work work = memberService.createWork(member, workDto);
//
//        Work work = workDto.toEntity();
//        work.changeTags();

        Work newWork = workService.save(work, member);

        return "redirect:/work/" + URLEncoder.encode(newWork.getPath(), StandardCharsets.UTF_8);
    }

    @ResponseBody
    @PostMapping("/work/create/add/tag")
    public ResponseEntity<?> addTag(@CurrentUser Member member, @RequestBody TagDto tagDto) {
        String title = tagDto.getTagTitle();
        Tag tag = tagService.findOrCreateNew(title);
        memberService.addTempTag(member, tag);

        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @PostMapping("/work/create/remove/tag")
    public ResponseEntity<?> removeTag(@CurrentUser Member member, @RequestBody TagDto tagDto) {
        String title = tagDto.getTagTitle();
        Tag tag = tagRepository.findByTitle(title);
        memberService.deleteTempTag(member, tag);

        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @PostMapping("/work/create/add/zone")
    public ResponseEntity<?> addZone(@CurrentUser Member member, @RequestBody ZoneDto zoneDto) {
        String cityKrName = zoneDto.getCityKrName();
        Zone zone = zoneRepository.findByCityKrName(cityKrName).orElseThrow();
        memberService.addTempZone(member, zone);

        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @PostMapping("/work/create/remove/zone")
    public ResponseEntity<?> removeZone(@CurrentUser Member member, @RequestBody ZoneDto zoneDto) {
        String cityKrName = zoneDto.getCityKrName();
        Zone zone = zoneRepository.findByCityKrName(cityKrName).orElseThrow();
        memberService.deleteTempZone(member, zone);

        return ResponseEntity.ok().build();
    }

//    @GetMapping("/works")
//    public String showList(Model model) {
//        List<Work> works = workService.findAll();
//        model.addAttribute(works);
//        return "works/workList";
//    }
//
//    @GetMapping("/works/{workId}/edit")
//    public String updateWorkDto(@PathVariable("workId") Long workId, Model model) {
//        Work work = workService.findOne(workId);
//        WorkDto form = new WorkDto();
//        form.change(work.getTitle(), work.getContent(), work.getTag(), null,
//                "", null);
//
//        model.addAttribute("form", form);
//        return "works/updateWorkDto";
//    }
//
//    //이거 한번 시험해봐야댐
//    @PostMapping("/works/{workId}/edit")
//    public String updateWork(@PathVariable("workId") Long workId, @ModelAttribute("form") WorkDto workDto) {
//
//        Work work = workService.findOne(workId);
//        work.change(workDto.getTitle(), workDto.getContent(), null);
//        //위에거로 되나????
//        //workService.save(work); //이거 될지 몰겟음
//
//        return "redirect:/works";
//    }

    @GetMapping("/work/category/bicycle")
    public String goBicycle() {
        return "/work/category/bicycle";
    }


    @InitBinder("workDto")
    public void workDtoInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(workFormValidator);
    }

    @GetMapping("/work/{path}")
    public String viewStudy(@CurrentUser Member member, @PathVariable String path, Model model) {
        model.addAttribute(member);
        model.addAttribute("work", workService.getWork(path));
        return "work/view";
    }

    @GetMapping("/work/{path}/members")
    public String viewStudyMember(@CurrentUser Member member, @PathVariable String path,
                                  Model model) {
        Work byPath = workService.getWork(path);
        model.addAttribute(member);
        model.addAttribute("work", byPath);
        return "work/members";
    }

    @GetMapping("/work/{path}/join")
    public String joinWork(@CurrentUser Member member, @PathVariable String path) {
        Work work = workService.findStudyWithMembersByPath(path);
        workService.addMember(work, member);
        return "redirect:/work/" + work.getEncodedPath() + "/members";
    }

    @GetMapping("/work/{path}/leave")
    public String leaveWork(@CurrentUser Member member, @PathVariable String path) {
        Work work = workService.findStudyWithMembersByPath(path);
        workService.removeMember(work, member);
        return "redirect:/work/" + work.getEncodedPath() + "/members";
    }
}
