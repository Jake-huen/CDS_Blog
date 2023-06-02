package cds.fileuploadproject.service.problem;

import cds.fileuploadproject.dto.ProblemDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class ProblemService {
    private final Map<Long, ProblemDto> problemMap = new HashMap<>();
    private long sequence = 0L;

    public ProblemDto save(ProblemDto problemDto) {
        problemDto.setId(++sequence);
        problemMap.put(problemDto.getId(), problemDto);
        log.info("save: problem={} 문제가 업로드 되었습니다. ", problemDto);
        System.out.println("save: problem=" + problemDto + "문제가 업로드 되었습니다. ");
        return problemDto;
    }

    public ProblemDto findById(Long id) {
        return problemMap.get(id);
    }
}
