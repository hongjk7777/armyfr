package project1.armyfr.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project1.armyfr.domain.Tag;
import project1.armyfr.repository.TagRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TagService {
    private final TagRepository tagRepository;

    public Tag findOrCreateNew(String tagTitle) {
        Tag newTag;
        Tag tag = tagRepository.findByTitle(tagTitle);
        if (tag == null) {
            tag = tagRepository.save(Tag.builder().title(tagTitle).build());
        }
        return tag;
    }
}
