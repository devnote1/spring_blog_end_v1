package shop.tenco.blog.reply;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import shop.tenco.blog.reply.ReplyRequest.WriteDTO;

@RequiredArgsConstructor
@Repository
public class ReplyRepository {

	private final EntityManager em;

	@Transactional
	public void save(WriteDTO dto, int userId) {
		Query query = em
				.createNativeQuery("insert into reply_tb(comment, board_id, user_id, created_at) values(?,?,?, now())");
		query.setParameter(1, dto.getComment());
		query.setParameter(2, dto.getBoardId());
		query.setParameter(3, userId);

		query.executeUpdate();
	}
	
	@Transactional
	public void deleteById(int id) {
	    String q = "delete from reply_tb where id = ?";
	    Query query = em.createNativeQuery(q);
	    query.setParameter(1, id);

	    query.executeUpdate();
	}

	public Reply findById(int id){
	    String q = "select * from reply_tb where id = ?";
	    Query query = em.createNativeQuery(q, Reply.class);
	    query.setParameter(1, id);
	    
	    return (Reply) query.getSingleResult();
	}
	

}
