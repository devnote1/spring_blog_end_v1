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
}
