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
		private Timestamp createdAt;
		private Boolean pageOwner; // 페이지 주인 여부

		private List<ReplyDTO> replies = new ArrayList<>();

		public void addReply(ReplyDTO reply) {
			replies.add(reply);
		}

        public DetailDTO(Integer id, String title, String content, Integer userId, String username, Timestamp createdAt) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.userId = userId;
            this.username = username;
            this.createdAt = createdAt;
        }
	}
	
    @AllArgsConstructor
    @Data
    public static class ReplyDTO {
        private Integer rId;
        private Integer rUserId;
        private String rUsername;
        private String rComment;
        private Boolean rOwner; // 로그인 한 유저가 댓글의 주인인지 여부
    }

	// 삭제 예정(사용 안함)
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


}
