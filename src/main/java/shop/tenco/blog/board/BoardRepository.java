package shop.tenco.blog.board;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class BoardRepository {
	private final EntityManager em;
	
	// 한 페이지당 갯수는 3로 고정 함(limit)  
	// 시작할 인덱스 번호, ROW 갯수    
	public List<Board> findAll(Integer page){ 
        Query query = em.createNativeQuery("select * from board_tb order by id desc limit ?, 3 ", Board.class);
        query.setParameter(1, (page * 3));
        return query.getResultList();
    }
	// 오버로딩 활용 
	public List<Board> findAll(Integer page, String keyword) {
	    Query query = em.createNativeQuery("select * from board_tb where title like ? order by id desc limit ?, 3", Board.class);
	    query.setParameter(1, "%"+keyword+"%");
	    query.setParameter(2, page * 3);
	    return query.getResultList();
	}
	
	// 3개씩 끊어서 총 몇 페이지 될지 계산해주어야 한다.  
	public Long count(String keyword) {
	    Query query = em.createNativeQuery("select count(*) from board_tb where title like ?");
	    query.setParameter(1, "%"+keyword+"%");
	    return (Long) query.getSingleResult();
	}

	
	public BoardResponse.DetailDTO findByIdWithUserAndWithReply(int idx) {
		// 텍스트 블록(Text Blocks) 
		// Java 15부터 정식 확정 
	    String q = """
	            select bt.id, bt.title, bt.content, bt.user_id, but.username, bt.created_at, 
	            rt.id r_id, rt.user_id r_user_id, rut.username, rt.comment from board_tb bt
	            left outer join reply_tb rt on bt.id = rt.board_id 
	            inner join user_tb but on bt.user_id = but.id 
	            left outer join user_tb rut on rt.user_id = rut.id 
	            where bt.id = ?
	            """;


	    Query query = em.createNativeQuery(q);
	    query.setParameter(1, idx);

	    // 1. 전체 결과 받기
	    List<Object[]> rows = (List<Object[]>) query.getResultList();

	    // 2. Board 결과가 3개가 중복되기 때문에, 0번지의 값만 가져오기
	    Integer id = (Integer) rows.get(0)[0];
	    String title = (String) rows.get(0)[1];
	    String content = (String) rows.get(0)[2];
	    int userId = (Integer) rows.get(0)[3];
	    String username = (String) rows.get(0)[4];
	    Timestamp createdAt = (Timestamp) rows.get(0)[5];

	    BoardResponse.DetailDTO detailDTO = new BoardResponse.DetailDTO(
	            id, title, content, userId, username, createdAt
	    );

	    // 3 바퀴 돌면서 댓글 추가하기
	    for (Object[] row : rows) {
	        Integer rId = (Integer) row[6];
	        Integer rUserId = (Integer) row[7];
	        String rUsername = (String) row[8];
	        String rComment = (String) row[9];

	        BoardResponse.ReplyDTO replyDTO = new BoardResponse.ReplyDTO(
	                rId, rUserId, rUsername, rComment, false
	        );

	        // 댓글이 없으면 add 안하기 (중괄호 제거 권장 안함)
	        if (rId != null) detailDTO.addReply(replyDTO);
	    }

	    return detailDTO;
	}
	

	@Transactional
	public void save(BoardRequest.SaveDTO requestDTO, int userId) {
	    Query query = em.createNativeQuery("insert into board_tb(title, content, user_id, created_at) values(?,?,?, now())");
	    query.setParameter(1, requestDTO.getTitle());
	    query.setParameter(2, requestDTO.getContent());
	    query.setParameter(3, userId);
	    
	    query.executeUpdate();  // insert, update, delete 
	}

	public Board findByBoardId(int id) {
		Query query = em.createNativeQuery("select * from board_tb where id = ?", Board.class);
		query.setParameter(1, id);
		Board board = (Board) query.getSingleResult();
		return board;
	}
	
	@Transactional
	public void deleteById(int id) {
	    Query query = em.createNativeQuery("delete from board_tb where id = ?");
	    query.setParameter(1, id);
	    query.executeUpdate(); // insert, update, delete 
	}

	@Transactional
	public void update(BoardRequest.UpdateDTO requestDTO, int id) {
	    Query query = em.createNativeQuery("update board_tb set title=?, content=? where id = ?");
	    query.setParameter(1, requestDTO.getTitle());
	    query.setParameter(2, requestDTO.getContent());
	    query.setParameter(3, id);

	    query.executeUpdate();
	}
}
