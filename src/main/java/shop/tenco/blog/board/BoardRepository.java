package shop.tenco.blog.board;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class BoardRepository {
	private final EntityManager em;

	public List<Board> findAll(){
        Query query = em.createNativeQuery("select * from board_tb order by id desc", Board.class);
        // 조회된 데이터가 없을 경우 null을 반환하지 않고, 비어 있는 리스트(empty list)를 반환합니다.
        // List<Board> boardListEntity = query.getResultList();  
        return query.getResultList();
    }
}
