package org.zerock.guestbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.guestbook.entity.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
}
