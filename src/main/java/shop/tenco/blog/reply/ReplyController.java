package shop.tenco.blog.reply;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import shop.tenco.blog.user.User;


@RequiredArgsConstructor
@Controller
public class ReplyController {

	private final HttpSession session; 
	private final ReplyRepository replyRepository;
	
	@PostMapping("/reply/save")
	public String write(ReplyRequest.WriteDTO dto) {
		
		User sessionUser = (User) session.getAttribute("sessionUser");
		if(sessionUser == null) {
			return "redirect:/loginForm";
		}
		
		// 유효성 검사(도전 과제) 
		
		// 핵심 코드 
		replyRepository.save(dto, sessionUser.getId());
		
		
		return "redirect:/board/"+dto.getBoardId();		
	}
	
	@PostMapping("/reply/{id}/delete")
	public String delete(@PathVariable(value = "id") int id){
	    User sessionUser = (User) session.getAttribute("sessionUser");

	    if (sessionUser == null) {
	        return "redirect:/loginForm";
	    }

	    Reply reply = replyRepository.findById(id);

	    // 댓글 없거나, 댓글 주인이 아니거나, 댓글 주인이거나
	    if(reply == null){
	        return "error/404";
	    }

	    if(reply.getUserId() != sessionUser.getId()){
	        return "error/403";
	    }

	    replyRepository.deleteById(id);

	    return "redirect:/board/"+reply.getBoardId();
	}
	
	
}
