package org.zerock.guestbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.guestbook.entity.MovieImage;

public interface MovieImageRepository extends JpaRepository<MovieImage, Long> {
}
