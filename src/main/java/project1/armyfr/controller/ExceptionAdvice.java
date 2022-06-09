package project1.armyfr.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import project1.armyfr.annotation.CurrentUser;
import project1.armyfr.domain.member.Member;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
public class ExceptionAdvice {

    @ExceptionHandler
    public String handleRuntimeException(@CurrentUser Member member,
                                         HttpServletRequest request,
                                         RuntimeException e) {
        if (member != null) {
            log.info("'{}' requested '{}'", member.getName(), request.getRequestURI());
        } else {
            log.info("requested '{}'", request.getRequestURI());
        }
        log.error("bad request", e);
        return "/error";
    }
}
