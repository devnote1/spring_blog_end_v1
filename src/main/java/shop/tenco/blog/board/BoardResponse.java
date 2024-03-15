package shop.tenco.blog.board;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

public class BoardResponse {
	
	@Data
	public static class DetailDTO {
		private int id; 
		private String title; 
		private String content; 
		private int userId; 
		private String username; 
	}
	
	@Data
	@AllArgsConstructor
	public static class BoardDTO {
		private int id; 
		private String title; 
		private String content; 
		private int userId; 
		private Timestamp createdAt;
		// 에러 예방 -> new ArrayList<>() 
		private List<ReplyDTO> replyDTOList = new ArrayList<>();
		
		public BoardDTO(int id, String title, String content, int userId, Timestamp createdAt) {
			this.id = id;
			this.title = title;
			this.content = content;
			this.userId = userId;
			this.createdAt = createdAt;
		} 
		
	}
	
	
	@Data
	@AllArgsConstructor
	public static class ReplyDTO {
		private int rid ; 
		private int boardId; 
		private String comment; 
	}
	
	
}
