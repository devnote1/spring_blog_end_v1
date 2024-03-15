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
	public void save(WriteDTO dto, Long userId) {
		Query query = em
				.createNativeQuery("insert into reply_tb(comment, board_id, user_id, created_at) values(?,?,?, now())");
		query.setParameter(1, dto.getComment());
		query.setParameter(2, dto.getBoardId());
		query.setParameter(3, userId);

		query.executeUpdate();
	}
	
	

}
