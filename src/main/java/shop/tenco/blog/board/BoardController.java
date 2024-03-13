package shop.tenco.blog.board;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class BoardController {

	private final BoardRepository boardRepository;
	
    @GetMapping({ "/", "/board" })
    public String index(HttpServletRequest request) {
    	
    	List<Board> boardList = boardRepository.findAll();
    	request.setAttribute("boardList", boardList);
    	
        return "index";
    }

    @GetMapping("/board/saveForm")
    public String saveForm() {
        return "board/saveForm";
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
