package shop.tenco.blog.user;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

//user로 테이블명을 만들면, 키워드여서 안만들어질 수 있다.
// user_tb 컨벤션 지키자.
@Table(name = "user_tb")
@Data
@Entity
public class User {
	
	@Id // PK 설정 
	@GeneratedValue(strategy = GenerationType.IDENTITY)  // auto_increment 전략
	private Long id;
	private String username;
	private String password;
	private String email;

	// 카멜 표기법으로 만들면 DB는 created_at 으로 만들어진다.
	// (언더스코어 기법)
	private LocalDateTime createdAt;

}
