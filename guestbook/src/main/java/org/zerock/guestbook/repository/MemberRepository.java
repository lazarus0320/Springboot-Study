package org.zerock.guestbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.guestbook.entity.Member;

public interface MemberRepository extends JpaRepository<Member, String> {
}
