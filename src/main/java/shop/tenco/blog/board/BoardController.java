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
import org.springframework.web.bind.annotation.ResponseBody;

@RequiredArgsConstructor
@Controller
public class BoardController {

	private final BoardRepository boardRepository;
	private final HttpSession session;

//	@GetMapping({ "/", "/board" })
//	public String index(HttpServletRequest request) {
//
//		List<Board> boardList = boardRepository.findAll();
//		request.setAttribute("boardList", boardList);
//
//		return "index";
//	}
	
	@GetMapping({ "/", "/board" })
	@ResponseBody
	public List<BoardResponse.BoardDTO> index(HttpServletRequest request) {
		List<BoardResponse.BoardDTO> boardList = boardRepository.findAllV2();
		return boardList;
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
	
	@PostMapping("/board/{id}/delete")
	public String delete(@PathVariable(value = "id") int id, HttpServletRequest request ) {
		 // 1. 인증 체크
	    User sessionUser = (User) session.getAttribute("sessionUser");
	    if (sessionUser == null) {
	        return "redirect:/loginForm";
	    }
	    // 2. 권한 체크 
	    Board board = boardRepository.findByBoardId(id);
	    // getUserId -> int, sessionUser.getId() --> long
	    if((long)board.getUserId() != sessionUser.getId()) {
	    	request.setAttribute("status", 403);
	        request.setAttribute("msg", "게시글을 삭제할 권한이 없습니다");
	        return "error/40x";
	    }
	    
	    // 3. 삭제 처리 위임 
	    boardRepository.deleteById(id);
		
		return "redirect:/";
	}
	
	@GetMapping("/board/{id}/updateForm")
	public String updateForm(@PathVariable(value = "id") int id, HttpServletRequest request){
	    // 1. 인증 안되면 나가
	    User sessionUser = (User) session.getAttribute("sessionUser");
	    if(sessionUser == null){
	        return "redirect:/loginForm";
	    }

	    // 2. 권한 없으면 나가
	    // 모델 위임 (id로 board를 조회)
	    Board board = boardRepository.findByBoardId(id);
	    if(board.getUserId() != sessionUser.getId()){
	        return "error/403";
	    }
	    
	    // 3. 가방에 담기
	    request.setAttribute("board", board);

	    return "board/updateForm";
	}
	
	
	@PostMapping("/board/{id}/update")
	public String update(@PathVariable(value = "id") int id, BoardRequest.UpdateDTO dto){
		// 1. 인증 체크
	    User sessionUser = (User) session.getAttribute("sessionUser");
	    if(sessionUser == null){
	        return "redirect:/loginForm";
	    }

	    // 2. 권한 체크
	    Board board = boardRepository.findByBoardId(id);
	    if(board.getUserId() != sessionUser.getId()){
	        return "error/403";
	    }

	    // 3. 핵심 로직
	    // update board_tb set title = ?, content = ? where id = ?;
	    boardRepository.update(dto, id);

	    return "redirect:/board/"+id;
	}
	
	
}
