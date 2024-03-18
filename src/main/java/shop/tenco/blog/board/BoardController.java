package shop.tenco.blog.board;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import shop.tenco.blog.user.User;



@RequiredArgsConstructor
@Controller
public class BoardController {

	private final BoardRepository boardRepository;
	private final HttpSession session;
	
	// 주소 설계 
	// localhost:8080?page=1 -> page 값이 1
	// localhost:8080  -> page 값이 0
	@GetMapping("/")
	public String index(HttpServletRequest request,
			@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "keyword", defaultValue = "") String keyword) {
		List<Board> boardList = null;
		
		 if (keyword.isBlank()) {
			 // 공백 처리 주의 
			 request.setAttribute("keyword", "");
			 boardList = boardRepository.findAll(page);
		 } else {
			 request.setAttribute("keyword", keyword);
			 boardList = boardRepository.findAll(page, keyword);
		 }
		 
		
		// 전체 페이지 계산하기 
		int count = boardRepository.count(keyword).intValue();
		System.out.println("count : "+ count);
		// 하나의 페이지에 보여줄 게시물 수는 3개로 고정해보자. 
		// 총 갯수가 2개 --> 1 page 
		// 3 --> 1page, 4 --> 2page
		// 5 --> 2page, 6 --> 2page  
		// 7 --> 3page, 8 --> 3page 
		// 9 --> 3page, 10 --> 4page 
		// 위 내용을 확인해 보면 총 갯수 / 3 일때 나머지가 있다면 + 1을 해주어 한다. 
		int namerge = count % 3 == 0 ? 0 : 1; 
		int allPageCount = (count / 3) + namerge;
		
		
		request.setAttribute("boardList", boardList);
		request.setAttribute("first", page == 0); // 비교 연산자 
		request.setAttribute("last", allPageCount == (page + 1)); // 비교 연산자 
		request.setAttribute("prev", page - 1);
		request.setAttribute("next", page + 1);
		
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

	@GetMapping("/board/{id}")
	public String detail(@PathVariable(value = "id") int id, HttpServletRequest request) {
	    
		// 1. 모델 진입 - 상세보기 데이터 가져오기
	    BoardResponse.DetailDTO responseDTO = boardRepository.findByIdWithUserAndWithReply(id);

	    // 2. 페이지 주인 여부 체크 (board의 userId와 sessionUser의 id를 비교)
	    User sessionUser = (User) session.getAttribute("sessionUser");

	    boolean pageOwner;
	    if (sessionUser == null) {
	        pageOwner = false;
	    } else {
	        int authorId = responseDTO.getUserId();
	        int loggedInUserId = sessionUser.getId();
	        pageOwner = authorId == loggedInUserId;
	    }

	    // 페이지 주인 여부
	    responseDTO.setPageOwner(pageOwner);

	    // 댓글 주인 여부
	    if (sessionUser != null) {
	        for (BoardResponse.ReplyDTO reply : responseDTO.getReplies()){
	            if(reply.getRUserId() == sessionUser.getId()){
	                reply.setROwner(true);
	            }
	        }
	    }

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
