package org.zerock.mreview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.mreview.entity.Member2;

public interface MemberRepository extends JpaRepository<Member2, Long> {
}
