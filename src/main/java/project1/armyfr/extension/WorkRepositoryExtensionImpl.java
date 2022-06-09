package project1.armyfr.extension;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import project1.armyfr.domain.QTag;
import project1.armyfr.domain.QZone;
import project1.armyfr.domain.member.QMember;
import project1.armyfr.domain.work.QWork;
import project1.armyfr.domain.work.Work;

import java.util.List;

public class WorkRepositoryExtensionImpl extends QuerydslRepositorySupport implements WorkRepositoryExtension {
    /**
     * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
     *
     *
     */
    public WorkRepositoryExtensionImpl() {
        super(Work.class);
    }

    @Override
    public Page<Work> findByKeyword(String keyword, Pageable pageable) {
        QWork work = QWork.work;
        JPQLQuery<Work> query = from(work).where(work.published.isTrue()
                .and(work.title.containsIgnoreCase(keyword))
                .or(work.zones.any().cityKrName.containsIgnoreCase(keyword))
                .or(work.tags.any().title.containsIgnoreCase(keyword)))
                .leftJoin(work.tags, QTag.tag).fetchJoin()
                .leftJoin(work.zones, QZone.zone).fetchJoin()
//                .leftJoin(work.members, QMember.member).fetchJoin()
                .distinct();
        JPQLQuery<Work> pageableQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Work> fetchResults = pageableQuery.fetchResults();
        return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());
    }
}
