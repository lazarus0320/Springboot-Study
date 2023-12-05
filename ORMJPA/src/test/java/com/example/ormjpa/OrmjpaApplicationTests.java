package com.example.ormjpa;

import com.example.ormjpa.domain.Member;
import com.example.ormjpa.domain.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class OrmjpaApplicationTests {
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
	EntityManager em = emf.createEntityManager();

	@Test
	public void testSave() {
		List<Member> members = new ArrayList<>();

		Member member1 = new Member();
		member1.setId(1L);
		member1.setUsername("멤버1");
		member1.setAge(25);
		members.add(member1);

		Member member2 = new Member();
		member2.setId(2L);
		member2.setUsername("멤버2");
		member2.setAge(30);
		members.add(member2);

		Team team1 = new Team("team1", "팀1", members);

		em.getTransaction().begin();

		em.persist(team1);

		em.getTransaction().commit();
	}

	@Test
	public void biDirection() {
		Team team = em.find(Team.class, "team1");
		List<Member> members = team.getMembers();

		for (Member member: members) {
			System.out.println("member.username = " +
					member.getUsername());
		}
	}

}
