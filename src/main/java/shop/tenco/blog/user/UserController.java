package shop.tenco.blog.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor 
@Controller
public class UserController {
	
	// UserController 멤버
	// 자바에서는 final 변수는 반드시 초기화 되어야 한다. 
	private final UserRepository userRepository;
	// 세션 메모리에 접근할 수 있는 인터페이서 
	private final HttpSession session;
	
	
	@GetMapping("/joinForm")
    public String joinForm() {
        return "user/joinForm";
    }
    
    @PostMapping("/join")
    public String join(UserRequest.JoinDTO dto) {
    	System.out.println("회원가입 요청이 들어 왔습니다");
    	System.out.println(dto.toString()); 
    	// 함수 호출 
    	userRepository.save(dto);
        return "redirect:/loginForm";
    }

    // http://localhost:8080/login
    @PostMapping("/login")
    public String login(UserRequest.LoginDTO dto) {
        if(dto.getUsername().length() < 3) {
        	return "error/400"; // ViewResolver 설정이 되어 있음. (앞 경로, 뒤 경로)
        }
        User user = userRepository.findByUsernameAndPassword(dto);
        if(user == null) {
        	System.out.println("조회 되지 않았습니다.");
        	return "error/401";
        } else {
        	System.out.println("정상 조회 : " + user.toString());
        	session.setAttribute("sessionUser", user); // 세션 영역에 담음 
        }
        
        return "redirect:/"; // // 컨트롤러가 존재하면 무조건 redirect 외우기  
    }
    

    @GetMapping("/loginForm")
    public String loginForm() {
        return "user/loginForm";
    }

    @GetMapping("/user/updateForm")
    public String updateForm(HttpServletRequest request ) {
    	// 1. 인증 체크
	    User sessionUser = (User) session.getAttribute("sessionUser");
	    if(sessionUser == null){
	        return "redirect:/loginForm";
	    }
	    
	    request.setAttribute("user", sessionUser);	    
        return "user/updateForm";
    }
    
    @PostMapping("/user/update")
    public String update(HttpServletRequest request) {
        // 30분이 지나면 세션 만료 
    	User sessionUser = (User) session.getAttribute("sessionUser");
	    if(sessionUser == null){
	        return "redirect:/loginForm";
	    }
	    
	    String password = request.getParameter("password");
	    System.out.println("password : " + password);
	    userRepository.update(password, sessionUser.getId());
	    // 객체 상태 변경 
	    sessionUser.setPassword(password);
	    // 세션 정보 수정 
	    session.setAttribute("sessionUser", sessionUser);
        
        return "redirect:/";
    }
    
    
    // 코드 수정 
    @GetMapping("/logout")
    public String logout() {
    	// 세션 메모리 영역에서 해제 
    	session.invalidate();
        return "redirect:/";
    }
}
