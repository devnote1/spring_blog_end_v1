package shop.tenco.blog.reply;

import lombok.Data;

public class ReplyRequest {
	
	@Data
    public static class WriteDTO {
        private String comment;
        private int boardId;
    }
}
