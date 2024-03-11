package shop.tenco.blog.user;

import lombok.Data;

public class UserRequest {

	@Data
	public static class JoinDTO {
		private String username;
		private String password;
		private String email;
	} // end of JoinDTO
	
	@Data
	public static class LoginDTO {
		private String username;
		private String password;
	}
	
} // end of UserRequest 
