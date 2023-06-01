package cds.fileuploadproject.repository;

import cds.fileuploadproject.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
