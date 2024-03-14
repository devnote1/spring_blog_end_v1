package shop.tenco.blog.board;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import shop.tenco.blog.user.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Controller
public class BoardController {

	private final BoardRepository boardRepository;
	private final HttpSession session;

	@GetMapping({ "/", "/board" })
	public String index(HttpServletRequest request) {

		List<Board> boardList = boardRepository.findAll();
		request.setAttribute("boardList", boardList);

		return "index";
	}

	// /board/saveForm 요청(Get)이 온다
	@GetMapping("/board/saveForm")
	public String saveForm() {
		// session 영역에 sessionUser 키값에 user 객체 있는지 체크
		User sessionUser = (User) session.getAttribute("sessionUser");

		// 값이 null 이면 로그인 페이지로 리다이렉션
		// 값이 null 이 아니면, /board/saveForm 으로 이동
		if (sessionUser == null) {
			return "redirect:/loginForm";
		}
		return "board/saveForm";
	}

	@PostMapping("/board/save")
	public String save(BoardRequest.SaveDTO dto, HttpServletRequest request) {
		 // 1. 인증 체크
	    User sessionUser = (User) session.getAttribute("sessionUser");
	    if (sessionUser == null) {
	        return "redirect:/loginForm";
	    }

	    // 2. 바디 데이터 확인 및 유효성 검사
	    System.out.println(dto);

	    if (dto.getTitle().length() > 30) {
	        request.setAttribute("status", 400);
	        request.setAttribute("msg", "title의 길이가 30자를 초과해서는 안되요");
	        return "error/400"; // BadRequest
	    }

	    // 3. 모델 위임
	    // insert into board_tb(title, content, user_id, created_at) values(?,?,?, now());
	    boardRepository.save(dto, sessionUser.getId());
		return "redirect:/";
	}

	// Spring MVC에서 URI의 경로 변수를 메소드의
	// 매개변수로 바인딩하려면 @PathVariable 어노테이션을 사용해야 합니다.
	@GetMapping("/board/{id}")
	public String detail(@PathVariable("id") int id, HttpServletRequest request) {
		System.out.println("id : " + id);

		BoardResponse.DetailDTO responseDTO = boardRepository.findById(id);
		request.setAttribute("board", responseDTO);

		return "board/detail";
	}
}
