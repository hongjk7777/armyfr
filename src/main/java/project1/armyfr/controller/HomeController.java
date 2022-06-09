package project1.armyfr.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import project1.armyfr.annotation.CurrentUser;
import project1.armyfr.domain.member.Member;
import project1.armyfr.domain.work.Work;
import project1.armyfr.repository.WorkRepository;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final WorkRepository workRepository;

    //    @GetMapping("/")
//    public String home() {
//        log.info("home controller");
//        return "home";
//    }
    @GetMapping("/")
    public String home(@CurrentUser Member member, Model model) {
        if (member != null) {
            model.addAttribute(member);
        }
        List<Work> workList = workRepository.findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(true, false);
        model.addAttribute("workList", workList);

        return "home";
    }

    @GetMapping("/test")
    public String test() {
        log.info("home controller");
        return "test";
    }

    @GetMapping("/search/work")
    public String searchWork(String keyword,
                             @PageableDefault(size = 9, sort = "publishedDateTime", direction = Sort.Direction.DESC)Pageable pageable, Model model) {
        Page<Work> workPage = workRepository.findByKeyword(keyword, pageable);
        model.addAttribute("workPage", workPage);
        model.addAttribute("keyword", keyword);
        return "search";
    }
}
