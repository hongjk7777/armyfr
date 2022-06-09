package project1.armyfr.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project1.armyfr.domain.Tag;
import project1.armyfr.domain.UserAccount;
import project1.armyfr.domain.Zone;
import project1.armyfr.domain.member.Member;
import project1.armyfr.domain.member.MemberTag;
import project1.armyfr.domain.member.MemberZone;
import project1.armyfr.domain.work.Work;
import project1.armyfr.dto.NotificationDto;
import project1.armyfr.dto.ProfileDto;
import project1.armyfr.dto.WorkDto;
import project1.armyfr.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final MemberTagRepository memberTagRepository;
    private final MemberZoneRepository memberZoneRepository;
    private final ZoneRepository zoneRepository;
    private final TagRepository tagRepository;

    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member);
        if (member.getEmail().equals("admin@example.com"))
            member.becomeAdmin();
        member.change(LocalDateTime.now(), false);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> memberList = memberRepository.findByEmail(member.getEmail());
        if (!memberList.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 이메일입니다");
        }
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

    public List<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }


    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {

        Member user = memberRepository.findOneByEmail(userEmail);

        List<GrantedAuthority> authorities = new ArrayList<>();

        if (userEmail.equals("admin@example.com")) {
            authorities.add(new SimpleGrantedAuthority(MemberRole.ADMIN.getValue()));
        } else {
            authorities.add(new SimpleGrantedAuthority(MemberRole.MEMBER.getValue()));
        }

        return new UserAccount(user, authorities);
    }

    @Transactional
    public void changeProfile(Long memberId, ProfileDto profileDto) {
        Member member = memberRepository.findOne(memberId);
        memberRepository.changeProfile(member, profileDto);
    }

    @Transactional
    public void changePW(Long memberId, String password) {
        Member member = memberRepository.findOne(memberId);
        memberRepository.changePW(member, password);
    }

    @Transactional
    public void changeNotify(Long memberId, NotificationDto n_dto) {
        Member member = memberRepository.findOne(memberId);
        memberRepository.changeNotify(member, n_dto);
    }

    public void changePassword(Member member, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        newPassword = passwordEncoder.encode(newPassword);

        member.changePassword(newPassword);
    }

    @Transactional
    public void addTag(Member member, Tag tag) {
        Member findMember = memberRepository.findOne(member.getId());
        if (findMember.isMyTag(tag) == false) {
            MemberTag memberTag = memberTagRepository.save(MemberTag.builder()
                    .member(findMember).tag(tag).build());
            findMember.addMemberTag(memberTag);
        }

    }

    @Transactional
    public void deleteTag(Member member, Tag tag) {
        Member findMember = memberRepository.findOne(member.getId());
        if (findMember.isMyTag(tag) == true) {
            memberTagRepository.delete(findMember.findMemberTag(tag));
            member.deleteTag(tag);
        }
    }

    public List<Tag> getTags(Member member) {
        Member findMember = memberRepository.findOne(member.getId());

        List<MemberTag> memberTags = findMember.getTags();
        List<Tag> tags = new ArrayList<>();

        for (int i = 0; i < memberTags.size(); i++) {
            tags.add(tagRepository.findByTitle(memberTags.get(i).getTag().getTitle()));
        }

        return tags;

    }

    @Transactional
    public void addZone(Member member, Zone zone) {
        Member findMember = memberRepository.findOne(member.getId());
        Optional<Zone> findZone = zoneRepository.findByCityKrName(zone.getCityKrName());
        if (findMember.isMyZone(findZone.orElseThrow()) == false) {
            MemberZone memberZone = memberZoneRepository.save(MemberZone.builder()
                    .member(findMember).zone(findZone.orElseThrow()).build());
            findMember.addMemberZone(memberZone);
        }

    }

    @Transactional
    public void deleteZone(Member member, Zone zone) {
        Member findMember = memberRepository.findOne(member.getId());
        if (findMember.isMyZone(zone) == true) {
            memberZoneRepository.delete(findMember.findMemberZone(zone));
            findMember.deleteZone(zone);
        }
    }

    public List<Zone> getZones(Member member) {
        Member findMember = memberRepository.findOne(member.getId());

        List<MemberZone> memberZones = findMember.getZones();
        List<Zone> zones = new ArrayList<>();

        for (int i = 0; i < memberZones.size(); i++) {
            zones.add(zoneRepository.findByCityKrName(memberZones.get(i).getZone().getCityKrName()).orElseThrow());
        }

        return zones;

    }

    @Transactional
    public void addTempTag(Member member, Tag tag) {
        Member findMember = memberRepository.findOne(member.getId());
        findMember.getTempTags().add(tag);
    }

    @Transactional
    public void deleteTempTag(Member member, Tag tag) {
        Member findMember = memberRepository.findOne(member.getId());
        findMember.getTempTags().remove(tag);
    }

    public List<Tag> getTempTags(Member member) {
        Member findMember = memberRepository.findOne(member.getId());
        return findMember.getTempTags();
    }

    @Transactional
    public void addTempZone(Member member, Zone zone) {
        Member findMember = memberRepository.findOne(member.getId());
        findMember.getTempZones().add(zone);
    }

    @Transactional
    public void deleteTempZone(Member member, Zone zone) {
        Member findMember = memberRepository.findOne(member.getId());
        findMember.getTempZones().remove(zone);
    }

    public List<Zone> getTempZones(Member member) {
        Member findMember = memberRepository.findOne(member.getId());
        return findMember.getTempZones();
    }


    @Transactional
    public void refreshTemp(Member member) {
        Member findMember = memberRepository.findOne(member.getId());
        findMember.refreshTempTags();
        findMember.refreshTempZones();
    }

    @Transactional
    public Work createWork(Member member, WorkDto workDto) {
        Member findMember = memberRepository.findOne(member.getId());

        Work work = workDto.toEntity();
//        work.setImage("");
        work.changeTags(new ArrayList<>(findMember.getTempTags()),
                new ArrayList<>(findMember.getTempZones()));
        return work;
    }

    @Transactional
    public void updateName(Member member, String newName) {
        Member findMember = memberRepository.findOne(member.getId());
        findMember.changeName(newName);
    }
}
