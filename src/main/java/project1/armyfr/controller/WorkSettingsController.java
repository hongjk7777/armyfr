package project1.armyfr.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.Errors;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import project1.armyfr.annotation.CurrentUser;
import project1.armyfr.domain.Tag;
import project1.armyfr.domain.Zone;
import project1.armyfr.domain.member.Member;
import project1.armyfr.domain.work.Work;
import project1.armyfr.dto.TagDto;
import project1.armyfr.dto.WorkDescriptionDto;
import project1.armyfr.dto.WorkDto;
import project1.armyfr.dto.ZoneDto;
import project1.armyfr.repository.TagRepository;
import project1.armyfr.repository.ZoneRepository;
import project1.armyfr.service.TagService;
import project1.armyfr.service.WorkService;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/work/{path}/settings")
public class WorkSettingsController {

    private final WorkService workService;
    private final ModelMapper modelMapper;
    private final TagService tagService;
    private final TagRepository tagRepository;
    private final ZoneRepository zoneRepository;
    private final ObjectMapper objectMapper;

    @GetMapping("/description")
    public String viewStudySetting(@CurrentUser Member member,
                                   @PathVariable String path, Model model) throws AccessDeniedException {
        Work work = workService.getWorkUpdate(member, path);
        model.addAttribute(member);
        model.addAttribute("work", work);
        model.addAttribute("workDescriptionDto", modelMapper.map(work, WorkDescriptionDto.class));

        return "work/settings/description";//이거는 왜 ㄱㅊ지
    }

    @PostMapping("/description")
    public String changeStudyDescription(@CurrentUser Member member, @PathVariable String path,
                                         @Valid WorkDescriptionDto workDescriptionDto,
                                         BindingResult result, Model model, RedirectAttributes attributes) throws AccessDeniedException {
        Work work = workService.getWorkUpdate(member, path);
        if (result.hasErrors()) {
            model.addAttribute(member);
            model.addAttribute("work", work);
            return "work/settings/description";
        }
        workService.updateWorkDescription(work, workDescriptionDto);
        attributes.addFlashAttribute("message", "모임 소개를 수정했습니다.");
        return "redirect:/work/" + getPath(path) + "/settings/description";
    }

    private String getPath(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

    @GetMapping("/banner")
    public String workBanner(@CurrentUser Member member, @PathVariable String path,
                             Model model) throws AccessDeniedException {
        Work work = workService.getWorkUpdate(member, path);
        model.addAttribute(member);
        model.addAttribute("work", work);
        return "work/settings/banner";
    }

    @PostMapping("/banner")
    public String updateWorkBanner(@CurrentUser Member member, @PathVariable String path,
                             String image, RedirectAttributes attributes) throws AccessDeniedException {
        Work work = workService.getWorkUpdate(member, path);
        workService.updateImage(work, image);
        attributes.addFlashAttribute("message", "모임 이미지 수정했습니다.");
        return "redirect:/work/" + work.getEncodedPath() + "/settings/banner";
    }

    @PostMapping("/banner/enable")
    public String enableWorkBanner(@CurrentUser Member member, @PathVariable String path) throws AccessDeniedException {
        Work work = workService.getWorkUpdate(member, path);
        workService.enableBanner(work);
        return "redirect:/work/" + work.getEncodedPath() + "/settings/banner";
    }

    @PostMapping("/banner/disable")
    public String disableWorkBanner(@CurrentUser Member member, @PathVariable String path) throws AccessDeniedException {
        Work work = workService.getWorkUpdate(member, path);
        workService.disableBanner(work);
        return "redirect:/work/" + work.getEncodedPath() + "/settings/banner";
    }

    @GetMapping("/tags")
    public String createWorkTagsDto(@CurrentUser Member member,
                                    @PathVariable String path, Model model) throws AccessDeniedException, JsonProcessingException {
        Work work = workService.getWorkUpdate(member, path);
        List<String> workTags = workService.getTags(work).stream().map(Tag::getTitle).collect(Collectors.toList());
        List<String> allTags = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());

        model.addAttribute(member);
        model.addAttribute("work", work);
        model.addAttribute("workDescriptionDto", modelMapper.map(work, WorkDescriptionDto.class));
        model.addAttribute("tags", workTags);
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTags));

        return "work/settings/tags";
    }

    @ResponseBody
    @PostMapping("/tags/add")
    public ResponseEntity<?> addTagToDB(@CurrentUser Member member, @RequestBody TagDto tagDto,
                                        @PathVariable String path) throws AccessDeniedException {
        Work work = workService.getWorkUpdateTags(member, path);
        String title = tagDto.getTagTitle();
        Tag tag = tagService.findOrCreateNew(title);
        workService.addTag(work, tag);

        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @PostMapping("/tags/remove")
    public ResponseEntity<?> removeTagToDB(@CurrentUser Member member, @RequestBody TagDto tagDto,
                                           @PathVariable String path) throws AccessDeniedException {
        Work work = workService.getWorkUpdateTags(member, path);
        String title = tagDto.getTagTitle();
        Tag tag = tagRepository.findByTitle(title);
        if (!tag.getTitle().equals("wrong title")) {
            workService.removeTag(work, tag);
//            tagRepository.delete(tag);
        } else {
            ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }


    @GetMapping("/zones")
    public String createWorkZonesDto(@CurrentUser Member member,
                                     @PathVariable String path, Model model) throws AccessDeniedException, JsonProcessingException {
        Work work = workService.getWorkUpdate(member, path);
        List<String> workZones = work.getZones().stream().map(Zone::getCityKrName).collect(Collectors.toList());
        List<String> allZones = zoneRepository.findAll().stream().map(Zone::getCityKrName).collect(Collectors.toList());

        model.addAttribute(member);
        model.addAttribute("work", work);
        model.addAttribute("workDescriptionDto", modelMapper.map(work, WorkDescriptionDto.class));
        model.addAttribute("zones", workZones);
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allZones));

        return "work/settings/zones";
    }

    @ResponseBody
    @PostMapping("/zones/add")
    public ResponseEntity<?> addZoneToDB(@CurrentUser Member member, @RequestBody ZoneDto zoneDto,
                                         @PathVariable String path) throws AccessDeniedException {
        Work work = workService.getWorkUpdateZones(member, path);
        String cityKrName = zoneDto.getCityKrName();
        Zone zone = zoneRepository.findByCityKrName(cityKrName).orElseThrow();
        workService.addZone(work, zone);

        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @PostMapping("/zones/remove")
    public ResponseEntity<?> removeZoneToDB(@CurrentUser Member member, @RequestBody ZoneDto zoneDto,
                                            @PathVariable String path) throws AccessDeniedException {
        Work work = workService.getWorkUpdateZones(member, path);
        String cityKrName = zoneDto.getCityKrName();
        Zone zone = zoneRepository.findByCityKrName(cityKrName).orElseGet(
                () -> Zone.builder().cityKrName("잘못된 도시").build());
        if (!zone.getCityKrName().equals("잘못된 도시")) {
            workService.removeZone(work, zone);
        } else {
            ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }


    @GetMapping("/work")
    public String workSettingForm(@CurrentUser Member member, @PathVariable String path,
                                  Model model) throws AccessDeniedException {
        Work work = workService.getWorkUpdateStatus(member, path);
        model.addAttribute(member);
        model.addAttribute("work", work);

        return "work/settings/work";
    }

    @PostMapping("/work/publish")
    public String publishStudy(@CurrentUser Member member, @PathVariable String path,
                               RedirectAttributes attributes) throws AccessDeniedException {
        Work work = workService.getWorkUpdateStatus(member, path);
        workService.publish(work);
        attributes.addFlashAttribute("message", "모임을 공개했습니다.");
        return "redirect:/work/" + getPath(path) + "/settings/work";

    }

    @PostMapping("/work/close")
    public String closeStudy(@CurrentUser Member member, @PathVariable String path,
                               RedirectAttributes attributes) throws AccessDeniedException {
        Work work = workService.getWorkUpdateStatus(member, path);
        workService.close(work);
        attributes.addFlashAttribute("message", "모임을 닫았습니다.");
        return "redirect:/work/" + getPath(path) + "/settings/work";

    }

    @PostMapping("/recruit/start")
    public String recruitStudy(@CurrentUser Member member, @PathVariable String path,
                               RedirectAttributes attributes) throws AccessDeniedException {
        Work work = workService.getWorkUpdateStatus(member, path);
        if (!work.canUpdateRecruiting()) {
            attributes.addFlashAttribute("message", "1시간에 한 번 모집 설정을 변경가능힙니다.");
            return "redirect:/work/" + getPath(path) + "/settings/work";
        }

        workService.startRecruit(work);
        attributes.addFlashAttribute("message", "모집을 시작했습니다.");
        return "redirect:/work/" + getPath(path) + "/settings/work";

    }

    @PostMapping("/recruit/stop")
    public String stopRecruitStudy(@CurrentUser Member member, @PathVariable String path,
                               RedirectAttributes attributes) throws AccessDeniedException {
        Work work = workService.getWorkUpdateStatus(member, path);
        if (!work.canUpdateRecruiting()) {
            attributes.addFlashAttribute("message", "1시간에 한 번 모집 설정을 변경가능힙니다.");
            return "redirect:/work/" + getPath(path) + "/settings/work";
        }

        workService.stopRecruit(work);
        attributes.addFlashAttribute("message", "모집을 종료했습니다.");
        return "redirect:/work/" + getPath(path) + "/settings/work";

    }

    @PostMapping("/work/path")
    public String changeWorkPath(@CurrentUser Member member, @PathVariable String path,
                                 RedirectAttributes attributes, @RequestParam String newPath,
                                 Model model) throws AccessDeniedException {
        Work work = workService.getWorkUpdateStatus(member, path);
        if (!workService.isValidPath(newPath)) {
            model.addAttribute(member);
            model.addAttribute(work);
            model.addAttribute("workPathError", "해당 경로는 사용불가합니다.");
            return "/work/settings/work";
        }

        workService.updatePath(work, newPath);
        attributes.addFlashAttribute("message", "모임 경로가 변경되었습니다.");
        return "redirect:/work/" + getPath(newPath) + "/settings/work";
    }

    @PostMapping("/work/title")
    public String changeWorkTitle(@CurrentUser Member member, @PathVariable String path,
                                 RedirectAttributes attributes, @RequestParam String newTitle,
                                 Model model) throws AccessDeniedException {
        Work work = workService.getWorkUpdateStatus(member, path);
        if (!workService.isValidTitle(newTitle)) {
            model.addAttribute(member);
            model.addAttribute(work);
            model.addAttribute("workTitleError", "해당 이름은 사용불가합니다.");
            return "/work/settings/work";
        }

        workService.updateTitle(work, newTitle);
        attributes.addFlashAttribute("message", "모임 이름이 변경되었습니다.");
        return "redirect:/work/" + getPath(path) + "/settings/work";
    }

    @PostMapping("/work/remove")
    public String removeWork(@CurrentUser Member member, @PathVariable String path,
                                  RedirectAttributes attributes, @RequestParam String newTitle,
                                  Model model) throws AccessDeniedException {
        Work work = workService.getWorkUpdateStatus(member, path);
        workService.remove(work);
        return "redirect:/";
    }
}
