package shop.tenco.blog.board;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Table(name="board_tb")
@Data
@Entity // 테이블 생성하기 위해 필요한 어노테이션
public class Board { // User 1 -> Board N
    @Id // PK 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment 전략
    private int id;
    private String title;
    private String content;

    private int userId; // 테이블에 만들어 질때 user_id=
    private LocalDateTime createdAt; // 테이블에 만들어 질때 created_at
}
