package cds.fileuploadproject.domain.problem;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ProblemRepository {
    private final Map<Long, Problem> problemMap = new HashMap<>();
    private long sequence = 0L;

    public Problem save(Problem problem) {
        problem.setId(++sequence);
        problemMap.put(problem.getId(), problem);
        return problem;
    }

    public Problem findById(Long id) {
        return problemMap.get(id);
    }
}
