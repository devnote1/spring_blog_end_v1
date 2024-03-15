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

	public List<Board> findAll(){
        Query query = em.createNativeQuery("select * from board_tb order by id desc", Board.class);
        // 조회된 데이터가 없을 경우 null을 반환하지 않고, 비어 있는 리스트(empty list)를 반환합니다.
        // List<Board> boardListEntity = query.getResultList();  
        return query.getResultList();
    }
	
	// 필요하다면 게시글 출력 시 댓글 정보도 함께 들고 와야 한다. 
	public List<BoardResponse.BoardDTO> findAllV2(){
		String queryStr = " select bt.id, bt.title, bt.content, bt.user_id, "
				+ "			 bt.created_at, ifnull(rt.id, 0) rid, ifnull(rt.board_id, 0), "
				+ "			 ifnull(rt.comment,'') comment  "
				+ " from board_tb bt left outer join reply_tb rt on bt.id = rt.board_id";
		
        Query query = em.createNativeQuery(queryStr);
        // 6개의 ROW 가 발생 
        List<Object[]> rows = (List<Object[]>) query.getResultList();
        List<BoardResponse.BoardDTO> boardList = new ArrayList<>();
        List<BoardResponse.ReplyDTO> replyList = new ArrayList<>();
        
        // 데이터 가공 4개로 줄이가 
        for (Object[] row : rows) {
        	
        	// BoardDTO
            Integer id = (Integer) row[0];
            String title = (String) row[1];
            String content = (String) row[2];
            Integer userId = (Integer) row[3];
            Timestamp createdAt = (Timestamp) row[4];
            BoardResponse.BoardDTO board = 
            		new BoardResponse.BoardDTO(id, title, content, userId, createdAt);
            // List Board 자료구조 
            boardList.add(board);
          
            
            // ReplyDTO
            Integer rid = (Integer) row[5];
            Integer boardId = (Integer) row[6];
            String comment = (String) row[7];
            
            BoardResponse.ReplyDTO reply = 
            		new BoardResponse.ReplyDTO(rid, boardId, comment);
            // List replyList 자료구조 
            replyList.add(reply);
        }
        
        
        // distinct() 메서드는 자바 스트림(java.util.stream.Stream) API의 중간 연산으로, 
        // 스트림의 요소 중 중복된 값을 제거하고 유일한 요소만을 남기는 데 사용됩니다.
        // 단, 자바 16 이상 부터 사용가능

        // 6개의 크기를 4개로 줄임 
        boardList = boardList.stream().distinct().toList();
        // 크기 확인  
        System.out.println("Board Size - " + boardList.size());
        
        // boardList의 각각의 요소(BoardDTO 안에 <- ReplyDTO 을 넣어 주자) 
        for (BoardResponse.BoardDTO b : boardList){
            // 6 바퀴
            for (BoardResponse.ReplyDTO r : replyList){
                if(b.getId() == r.getBoardId()){
                	// BoardDTO 에 ReplyList 추가  
                    b.getReplyDTOList().add(r);
                }
            }
        }
        
       System.out.println("최종 : " + boardList.toString());
        
       return boardList;
    }
	
	public BoardResponse.DetailDTO findById(int idx) {
		// getSingleResult() 결과가 없으면 예외 클래스를 반환 합니다.  
		BoardResponse.DetailDTO responseDTO = new BoardResponse.DetailDTO();
		
		try {
			// 메서드명 확인 (createNativeQuery)
			Query query = em.createNativeQuery("select b.id, b.title, b.content, b.user_id, u.username "
					+ "from board_tb b "
					+ "inner join user_tb u on b.user_id = u.id "
					+ "where b.id = ?1 ");
			query.setParameter(1, idx);
			
			// 컬럼이 여러개 -> Object[] 배열로 넘어 온다. 
			Object[] row = (Object[]) query.getSingleResult();
			Integer id = (Integer) row[0];
	        String title = (String) row[1];
	        String content = (String) row[2];
	        int userId = (Integer) row[3];
	        String username = (String) row[4];

	        System.out.println("id : "+id);
	        System.out.println("title : "+title);
	        System.out.println("content : "+content);
	        System.out.println("userId : "+userId);
	        System.out.println("username : "+username);
	        
	        responseDTO.setId(id);
	        responseDTO.setTitle(title);
	        responseDTO.setContent(content);
	        responseDTO.setUserId(userId);
	        responseDTO.setUsername(username);
	        
		} catch (Exception e) {
			System.err.println("findById 예외 발생 : " + e.getClass());
			System.err.println("msg: " + e.getMessage());
		}
		return responseDTO; 
	}

	@Transactional
	public void save(BoardRequest.SaveDTO requestDTO, Long userId) {
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
