package com.khit.board.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.khit.board.dto.MemberDTO;
import com.khit.board.entity.Member;
import com.khit.board.service.MemberService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class MemberController {
	
	private final MemberService memberService;
	
	//회원가입
	@GetMapping("/member/join")
	public String joinForm() {
		return "/member/join";	//join.html
	}
	
	@PostMapping("/member/join")
	public String join(@ModelAttribute MemberDTO memberDTO) {
		System.out.println("memberDTO: " + memberDTO);
		memberService.save(memberDTO);
		return "redirect:/"; //login.html
	}
	
	//회원목록
	@GetMapping("/member/list")
	public String getList(Model model) {
		//db에서 꺼내서 DTO로 memberDTO로 반환할 것
		List<MemberDTO> memberDTOList = memberService.findAll();
		model.addAttribute("memberList", memberDTOList);
		return "/member/list";	//list.html
	}
	
	//회원 상세보기
	//@PathVariable - 경로에 변수를 할당
	@GetMapping("/member/{id}")
	public String getMember(@PathVariable Long id, Model model) {
		MemberDTO memberDTO = memberService.findById(id);
		model.addAttribute("member", memberDTO);
		return "/member/detail"; //detail.html 
	}
	
	//회원삭제
	@GetMapping("/member/delete/{id}")
	public String deleteMember(@PathVariable Long id) {
		//삭제 지원 메서드 - deleteById()
		memberService.deleteById(id);
		return "redirect:/member/list";
	}
	
	//로그인
	@GetMapping("/member/login")
	public String loginForm() {
		return "/member/login";	//login.html
	}
	
	//로그인 처리
	@PostMapping("/member/login")
	public String login(@ModelAttribute MemberDTO memberDTO, HttpSession session, Model model) {
		MemberDTO loginMember = memberService.login(memberDTO);
		//로그인한 결과(객체가 있으면 로그인성공, 없으면 다시 로그인폼)
		if(loginMember != null) {	//loginMember로 해야 다른 정보도 가져올수있음
			session.setAttribute("sessionEmail", loginMember.getMemberEmail());
			session.setAttribute("sessionName", loginMember.getMemberName());
			return "main";	//main.html
		}else {
			String error = "아이디나 비밀번호를 확인해주세요";
			model.addAttribute("error", error);
			return "/member/login";	//login.html
		}
	}
	//로그아웃
	@GetMapping("/member/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}
	
	//정보 수정페이지
	@GetMapping("/member/update")
	public String updateForm(HttpSession session, Model model) {
		String email = (String)session.getAttribute("sessionEmail");
		MemberDTO memberDTO = memberService.findByMemberEmail(email);
		model.addAttribute("member", memberDTO);
		return "/member/update";	//update.html
	}
	
	//정보수정 처리
	@PostMapping("/member/update")
	public String update(@ModelAttribute MemberDTO memberDTO) {
		memberService.update(memberDTO);
		return "redirect:/member/" + memberDTO.getId();
	}
	
	//이메일 중복 검사
	@PostMapping("/member/check-email")
	public @ResponseBody String checkEmail(@RequestParam("memberEmail") String memberEmail) {
		String resultText = memberService.checkEmail(memberEmail);
		return resultText;
	}
}
