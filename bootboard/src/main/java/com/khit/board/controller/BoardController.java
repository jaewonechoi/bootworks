package com.khit.board.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.khit.board.dto.BoardDTO;
import com.khit.board.service.BoardService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/board")
@RequiredArgsConstructor
@Controller
public class BoardController {
	
	
	private final BoardService boardService;
	
	//글쓰기 페이지
	@GetMapping("/write")
	public String writeForm(BoardDTO boardDTO) {
		return "/board/write";	//write.html
	}
	
	@PostMapping("/write")
	public String write(@Valid BoardDTO boardDTO, BindingResult bindingResult) {
		if(bindingResult.hasErrors()) {	//에러가 있을시 글쓰기폼으로 이동
			log.info("has errors");
			return "/board/write";
		}
		//글쓰기 처리
		boardService.save(boardDTO);
		return "redirect:/board/pagelist";
	}
	
	//글 목록보기
	@GetMapping("/list")
	public String getList(Model model) {
		List<BoardDTO> boardDTOList = boardService.findAll();
		model.addAttribute("boardList", boardDTOList);
		return "/board/list";
	}
	
	//글목록(페이지)
	// /board/pagelist?page=0
	// /board/pagelist
	@GetMapping("/pagelist")
	public String getPageList(@PageableDefault(page = 1) Pageable pageable, Model model) {
		Page<BoardDTO> boardDTOList = boardService.findListAll(pageable);
		
		//하단의 페이지 블럭 만들기
		int blockLimit = 10;	//하단에 보여줄 페이지 개수
		//시작 페이지 1, 11, 21...		12/10 = 1.2 -> 2 * 10 + 1 = 21
		int startPage = (int)(Math.ceil((double)pageable.getPageNumber() / blockLimit)- 1)*blockLimit + 1;
		//마지막 페이지 10, 20, 30	// 12
		int endPage = (startPage + blockLimit - 1) > boardDTOList.getTotalPages() ? 
				boardDTOList.getTotalPages() : startPage + blockLimit - 1;
		
		model.addAttribute("boardList",boardDTOList);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		return "/board/pagelist";
	}
	
	//글 상세보기
	@GetMapping("/{id}")
	public String getBoard(@PathVariable Long id,@PageableDefault(page = 1) Pageable pageable,
			Model model) {
		//조회수
		boardService.updateHits(id);
		//글 상세보기
		BoardDTO boardDTO = boardService.findById(id);
		model.addAttribute("board", boardDTO);
		model.addAttribute("page", pageable.getPageNumber());
		return "/board/detail";
	}
	
	//글 삭제하기
	@GetMapping("/delete/{id}")
	public String deleteBoard(@PathVariable Long id) {
		boardService.deleteById(id);
		return "redirect:/board/list";
	}
	
	//수정 페이지 요청
	@GetMapping("/update/{id}")
	public String updateForm(@PathVariable Long id, Model model) {
		//수정할 페이지 가져오기
		BoardDTO boardDTO = boardService.findById(id);
		model.addAttribute("board", boardDTO);
		return "/board/update";
	}
	
	@PostMapping("/update")
	public String update(@ModelAttribute BoardDTO boardDTO) {
		boardService.update(boardDTO);
		return "redirect:/board/" + boardDTO.getId();
	}
}
