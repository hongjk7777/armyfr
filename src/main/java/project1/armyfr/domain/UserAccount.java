package project1.armyfr.domain;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import project1.armyfr.domain.member.Member;
import project1.armyfr.repository.MemberRepository;
import project1.armyfr.service.MemberRole;

import java.util.List;

@Getter
public class UserAccount extends User {

    private Member member;

    public UserAccount(Member member, List<GrantedAuthority> authorities) {
        super(member.getEmail(), member.getPassword(), authorities);
        this.member = member;
    }
}
