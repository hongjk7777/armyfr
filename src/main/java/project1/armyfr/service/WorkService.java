package project1.armyfr.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project1.armyfr.domain.Tag;
import project1.armyfr.domain.UserAccount;
import project1.armyfr.domain.Zone;
import project1.armyfr.domain.member.Member;
import project1.armyfr.domain.work.Work;
import project1.armyfr.dto.WorkDescriptionDto;
import project1.armyfr.repository.MemberRepository;
import project1.armyfr.repository.TagRepository;
import project1.armyfr.repository.WorkRepository;
import project1.armyfr.repository.ZoneRepository;

import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WorkService {

    private static final String VALID_PATH_PATTERN = "^[ㄱ-ㅎ가-힣a-z0-9_-]{2,20}$";
    private final WorkRepository workRepository;
    private final ZoneRepository zoneRepository;
    private final TagRepository tagRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public Work save(Work work, Member member) {
        Work newWork = workRepository.save(work);
        newWork.addManager(member);
        return work;
    }

    @Transactional
    public void updateWork(Long id, String name) {
        Work work = workRepository.findById(id).orElseThrow();
        work.change(name);
    }

    public List<Work> findAll() {
        return workRepository.findAll();
    }

    public Work findOne(Long id) {
        return workRepository.findById(id).orElseThrow();
    }

    @Transactional
    public void addTag(Work work, Tag tag) {
        Work findWork = workRepository.findById(work.getId()).orElseThrow();
        findWork.getTags().add(tag);
    }

    @Transactional
    public void addManager(Work work, Member member) {
        Work findWork = workRepository.findById(work.getId()).orElseThrow();
        findWork.addManager(member);

    }

    public Work getWork(String path) {
         Work work = workRepository.findByPath(path);
        if (work == null) {
            throw new IllegalStateException(path + "에 해당하는 모임이 없습니다");
        }
        return work;
    }

    public Work getWorkUpdate(Member member, String path) throws AccessDeniedException {
        Member findMember = memberRepository.findOne(member.getId());
        Work workByPath = workRepository.findByPath(path);
        if (!findMember.isManagerOf(workByPath)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }

        return workByPath;
    }

    @Transactional
    public void updateWorkDescription(Work work, WorkDescriptionDto workDescriptionDto) {
        modelMapper.map(workDescriptionDto, work);
    }

    public List<Tag> getTags(Work work) {
        return work.getTags().stream().collect(Collectors.toList());
    }

    @Transactional
    public void removeTag(Work work, Tag tag) {
        work.getTags().remove(tag);
    }

    public Work getWorkUpdateTags(Member member, String path) throws AccessDeniedException {
        Work work = workRepository.findMemberWithTagsByPath(path);
        checkIfExistWork(path, work);
        checkIfManager(memberRepository.findOne(member.getId()), work);

        return work;
    }

    private void checkIfManager(Member member, Work work) throws AccessDeniedException {
        if (!member.isManagerOf(work)) {
            throw new AccessDeniedException("해당 기능 사용불가합니다");
        }
    }

    private void checkIfExistWork(String path, Work work) {
        if (work == null) {
            throw new IllegalArgumentException(path + "에 해당하는 모임이 없습니다");
        }
    }

    public List<Zone> getZones(Work work) {
        return workRepository.findById(work.getId()).get().getZones().stream().collect(Collectors.toList());
    }

    public Work getWorkUpdateZones(Member member, String path) throws AccessDeniedException {
        Work work = workRepository.findMemberWithZonesByPath(path);
        checkIfExistWork(path, work);
        checkIfManager(memberRepository.findOne(member.getId()), work);
        return work;
    }

    @Transactional
    public void addZone(Work work, Zone zone) {
        work.getZones().add(zone);
    }

    @Transactional
    public void removeZone(Work work, Zone zone) {
        work.getZones().remove(zone);
    }

    public Work getWorkUpdateStatus(Member member, String path) throws AccessDeniedException {
        Work work = workRepository.findMemberWithManagersByPath(path);
        checkIfExistWork(path, work);
        checkIfManager(member, work);
        return work;
    }

    @Transactional
    public void publish(Work work) {
        work.publish();
    }

    @Transactional
    public void close(Work work) {
        work.close();
    }

    @Transactional
    public void startRecruit(Work work) {
        work.startRecruit();
    }

    @Transactional
    public void stopRecruit(Work work) {
        work.stopRecruit();
    }

    public boolean isValidTitle(String newTitle) {
        if (newTitle.length() <= 50) {
            return true;
        }
        return false;
    }

    public boolean isValidPath(String newPath) {
        if (newPath.matches(VALID_PATH_PATTERN)) {
            return true;
        }
        return false;
    }

    @Transactional
    public void updatePath(Work work, String newPath) {
        work.updatePath(newPath);
    }

    @Transactional
    public void updateTitle(Work work, String newTitle) {
        work.updateTitle(newTitle);
    }

    @Transactional
    public void remove(Work work) {
        if (work.isRemovable()) {
            workRepository.delete(work);
        } else {
            throw new IllegalArgumentException("모임을 삭제할 수 없습니다.");
        }
    }

    public Work findStudyWithMembersByPath(String path) {
        Work work = workRepository.findStudyWithMembersByPath(path);
        return work;
    }

    public void addMember(Work work, Member member) {
        work.addMember(member);
    }

    public void removeMember(Work work, Member member) {
        work.removeMember(member);
    }

    @Transactional
    public void updateImage(Work work, String image) {
        work.setImage(image);
    }

    @Transactional
    public void enableBanner(Work work) {
        work.setUseBanner(true);
    }

    @Transactional
    public void disableBanner(Work work) {
        work.setUseBanner(false);
    }
}

