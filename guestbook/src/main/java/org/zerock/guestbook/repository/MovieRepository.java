package org.zerock.guestbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.guestbook.entity.Movie;

public interface MovieRepository extends JpaRepository<Movie, Long> {

}
