package cds.fileuploadproject.repository;

import cds.fileuploadproject.domain.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {
    Problem findByFileName(String fileName);
}
