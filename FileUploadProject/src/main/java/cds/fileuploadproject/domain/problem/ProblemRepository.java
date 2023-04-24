package cds.fileuploadproject.domain.problem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class ProblemRepository {
    private final Map<Long, Problem> problemMap = new HashMap<>();
    private long sequence = 0L;

    public Problem save(Problem problem) {
        problem.setId(++sequence);
        problemMap.put(problem.getId(), problem);
        log.info("save: problem={} 문제가 업로드 되었습니다. ", problem);
        System.out.println("save: problem=" + problem + "문제가 업로드 되었습니다. ");
        return problem;
    }

    public Problem findById(Long id) {
        return problemMap.get(id);
    }
}
