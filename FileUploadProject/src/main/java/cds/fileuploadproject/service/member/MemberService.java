package cds.fileuploadproject.repository;

import cds.fileuploadproject.dto.MemberDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
public class MemberService {
    private static Map<Long, MemberDto> store = new HashMap<>();
    private static long sequence = 0L;

    public MemberDto save(MemberDto memberDto) {
        memberDto.setId(++sequence);
        log.info("save: member={}", memberDto);
        System.out.println("member = " + memberDto);
        store.put(memberDto.getId(), memberDto);
        return memberDto;
    }

    public MemberDto findById(Long id) {
        return store.get(id);
    }

    public Optional<MemberDto> findByLoginId(String loginId) {
        List<MemberDto> all = findAll();
        for (MemberDto m : all) {
            if(m.getLoginId().equals(loginId)){
                return Optional.of(m);
            }
        }
        return Optional.empty();
    }

    public List<MemberDto> findAll(){
        ArrayList<MemberDto> memberDtoArrayList = new ArrayList<>(store.values());
        return memberDtoArrayList;
    }

    public void clearStore(){
        store.clear();
    }
}
