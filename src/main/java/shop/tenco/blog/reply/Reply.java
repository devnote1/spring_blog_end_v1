package shop.tenco.blog.reply;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Table(name = "reply_tb")
@Data
@Entity
public class Reply {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String comment; 
	private int userId; 
	private int boardId;
	private LocalDateTime createdAt; 
}
