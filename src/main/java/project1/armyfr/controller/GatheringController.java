package project1.armyfr.controller;

import jdk.dynalink.linker.LinkerServices;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.stylesheets.LinkStyle;
import project1.armyfr.annotation.CurrentUser;
import project1.armyfr.domain.event.Enrollment;
import project1.armyfr.domain.event.Gathering;
import project1.armyfr.domain.member.Member;
import project1.armyfr.domain.work.Work;
import project1.armyfr.dto.GatheringDto;
import project1.armyfr.repository.EnrollmentRepository;
import project1.armyfr.repository.GatheringRepository;
import project1.armyfr.service.EnrollmentService;
import project1.armyfr.service.GatheringService;
import project1.armyfr.service.WorkService;
import project1.armyfr.validator.GatheringValidator;

import javax.validation.Valid;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/work/{path}")
@RequiredArgsConstructor
public class GatheringController {

    private final WorkService workService;
    private final ModelMapper modelMapper;
    private final GatheringService gatheringService;
    private final GatheringRepository gatheringRepository;
    private final GatheringValidator gatheringValidator;
    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentService enrollmentService;

    @InitBinder("gatheringDto")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(gatheringValidator);
    }

    @GetMapping("/new-gathering")
    public String createGatheringForm(@CurrentUser Member member,
                                      @PathVariable String path, Model model) throws AccessDeniedException {
        Work work = workService.getWorkUpdateStatus(member, path);

        model.addAttribute(member);
        model.addAttribute("work", work);
        model.addAttribute("gatheringDto", new GatheringDto());
        return "work/gathering/form";
    }

    @PostMapping("/new-gathering")
    public String createNewGathering(@CurrentUser Member member, @PathVariable String path,
                                     @Valid GatheringDto gatheringDto, BindingResult result,
                                     Model model) throws AccessDeniedException {
        Work work = workService.getWorkUpdateStatus(member, path);
        if (result.hasErrors()) {
            model.addAttribute(member);
            model.addAttribute("work", work);
            return "work/gathering/form";
        }
        Gathering gathering = gatheringService.createGathering
                (modelMapper.map(gatheringDto, Gathering.class), member, work);

        return "redirect:/work/" + work.getEncodedPath() + "/gatherings/" + gathering.getId();
    }

    @GetMapping("/gatherings/{id}")
    public String createGatheringForm(@CurrentUser Member member, @PathVariable String path,
                                      @PathVariable Long id, Model model) throws AccessDeniedException {
        Work work = workService.getWork(path);
        Gathering gathering = gatheringService.findGatheringById(id);

        model.addAttribute(member);
        model.addAttribute("work", work);
        model.addAttribute("gathering", gathering);
        return "work/gathering/view";
    }

    @GetMapping("/gatherings")
    public String viewGatherings(@CurrentUser Member member, @PathVariable String path,
                                 Model model) throws AccessDeniedException {
        Work work = workService.getWork(path);
        model.addAttribute("member", member);
        model.addAttribute("work", work);

        List<Gathering> gatherings = gatheringRepository.findByWorkOrderByStartDateTime(work);
        ArrayList<Gathering> newGatherings = new ArrayList<>();
        ArrayList<Gathering> oldGatherings = new ArrayList<>();
        gatherings.forEach(e -> {
            if (e.getEndDateTime().isBefore(LocalDateTime.now())) {
                oldGatherings.add(e);
            } else {
                newGatherings.add(e);
            }
        });
        model.addAttribute("newGatherings", newGatherings);
        model.addAttribute("oldGatherings", oldGatherings);
        return "work/gatherings";
    }

    @GetMapping("/gatherings/{id}/edit")
    public String editGatheringForm(@CurrentUser Member member, @PathVariable String path,
                                    Model model, @PathVariable Long id) throws AccessDeniedException {
        Work work = workService.getWorkUpdate(member, path);
        Gathering gathering = gatheringService.findGatheringById(id);
        model.addAttribute(member);
        model.addAttribute("work", work);
        model.addAttribute("gathering", gathering);
        model.addAttribute("gatheringDto", modelMapper.map(gathering, GatheringDto.class));
        return "/work/gathering/update-form";
    }

    @PostMapping("/gatherings/{id}/edit")
    public String editGathering(@CurrentUser Member member, @PathVariable String path,
                                @Valid GatheringDto gatheringDto, @PathVariable Long id,
                                Errors error, Model model) throws AccessDeniedException {
        Work work = workService.getWorkUpdateStatus(member, path);
        Gathering gathering = gatheringService.findGatheringById(id);
        gatheringDto.setEventType(gathering.getEventType());

        gatheringValidator.validateUpdateForm(gatheringDto, gathering, error);

        if (error.hasErrors()) {
            model.addAttribute(member);
            model.addAttribute("work", work);
            model.addAttribute("gathering", gathering);
            return "work/gathering/form";
        }
        gatheringService.updateGathering(gathering, gatheringDto);

        return "redirect:/work/" + work.getEncodedPath() + "/gatherings/" + gathering.getId();
    }

    @DeleteMapping("/gatherings/{id}")
    public String editGathering(@CurrentUser Member member, @PathVariable String path,
                                @PathVariable Long id) throws AccessDeniedException {
        Work work = workService.getWorkUpdate(member, path);
        Gathering gathering = gatheringService.findGatheringById(id);
        gatheringService.removeGathering(gathering);
        return "redirect:/work/" + work.getEncodedPath() + "/gatherings";
    }

    @PostMapping("/gatherings/{id}/enroll")
    public String createEnrollment(@CurrentUser Member member, @PathVariable String path,
                                   @PathVariable Long id) throws AccessDeniedException {
        Work work = workService.getWork(path);
        Gathering gathering = gatheringService.findGatheringById(id);
        gatheringService.newEnrollment(gathering, member);
        return "redirect:/work/" + work.getEncodedPath() + "/gatherings/" + id;
    }

    @PostMapping("/gatherings/{id}/disenroll")
    public String cancelEnrollment(@CurrentUser Member member, @PathVariable String path,
                                   @PathVariable Long id) throws AccessDeniedException {

        Work work = workService.getWork(path);
        Gathering gathering = gatheringService.findGatheringById(id);
        gatheringService.cancelEnrollment(gathering, member);
        return "redirect:/work/" + work.getEncodedPath() + "/gatherings/" + id;
    }

    @GetMapping("/gatherings/{gatheringId}/enrollments/{enrollmentId}/accept")
    public String acceptEnrollment(@CurrentUser Member member, @PathVariable String path,
                                   @PathVariable Long gatheringId, @PathVariable Long enrollmentId) throws AccessDeniedException {
        Work work = workService.getWorkUpdate(member, path);
        Gathering gathering = gatheringService.findGatheringById(gatheringId);
        Enrollment enrollment = enrollmentService.findById(enrollmentId);
        enrollmentService.acceptEnrollment(gathering, enrollment);

        return "redirect:/work/" + work.getEncodedPath() + "/gatherings/" + gatheringId;
    }

    @GetMapping("/gatherings/{gatheringId}/enrollments/{enrollmentId}/reject")
    public String rejectEnrollment(@CurrentUser Member member, @PathVariable String path,
                                   @PathVariable Long gatheringId, @PathVariable Long enrollmentId) throws AccessDeniedException {
        Work work = workService.getWorkUpdate(member, path);
        Gathering gathering = gatheringService.findGatheringById(gatheringId);
        Enrollment enrollment = enrollmentService.findById(enrollmentId);
        enrollmentService.rejectEnrollment(gathering, enrollment);

        return "redirect:/work/" + work.getEncodedPath() + "/gatherings/" + gatheringId;
    }

    @GetMapping("/gatherings/{gatheringId}/enrollments/{enrollmentId}/checkin")
    public String checkInEnrollment(@CurrentUser Member member, @PathVariable String path,
                                   @PathVariable Long gatheringId, @PathVariable Long enrollmentId) throws AccessDeniedException {
        Work work = workService.getWorkUpdate(member, path);
        Gathering gathering = gatheringService.findGatheringById(gatheringId);
        Enrollment enrollment = enrollmentService.findById(enrollmentId);
        enrollmentService.checkInEnrollment(enrollment);

        return "redirect:/work/" + work.getEncodedPath() + "/gatherings/" + gatheringId;
    }

    @GetMapping("/gatherings/{gatheringId}/enrollments/{enrollmentId}/cancel-checkin")
    public String cancelCheckInEnrollment(@CurrentUser Member member, @PathVariable String path,
                                   @PathVariable Long gatheringId, @PathVariable Long enrollmentId) throws AccessDeniedException {
        Work work = workService.getWorkUpdate(member, path);
        Gathering gathering = gatheringService.findGatheringById(gatheringId);
        Enrollment enrollment = enrollmentService.findById(enrollmentId);
        enrollmentService.cancelCheckInEnrollment(enrollment);

        return "redirect:/work/" + work.getEncodedPath() + "/gatherings/" + gatheringId;
    }
}