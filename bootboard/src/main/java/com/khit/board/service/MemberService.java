package com.khit.board.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.khit.board.dto.MemberDTO;
import com.khit.board.entity.Member;
import com.khit.board.exception.BootBoardException;
import com.khit.board.repository.MemberRepository;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

//@AllArgsConstructor
@RequiredArgsConstructor	//생성자 주입 방식 - final을 꼭 붙임
@Service
public class MemberService {
	
	private final MemberRepository memberRepository;

	public void save(MemberDTO memberDTO) {
		//DB안으로 저장(entity를 저장해야함
		//memberDTO를 entity로 변환 메서드
		Member member = Member.toSaveEntity(memberDTO);
		memberRepository.save(member);
	}

	public List<MemberDTO> findAll() {
		//db에서 member 엔티티를 꺼내와서
		List<Member> memberList = memberRepository.findAll();
		//변환 메서드가 필요
		//member를 담을 비어있는 dto 리스트를 생성
		List<MemberDTO> memberDTOList = new ArrayList<>();
		
		for(Member member : memberList) { //memberList를 반복하면서 변환함
			MemberDTO memberDTO = MemberDTO.toSaveDTO(member);
			memberDTOList.add(memberDTO);
		}
		//컨트롤러에 DTO로 보냄
		return memberDTOList;
	}

	public MemberDTO findById(Long id) {
		//db에서 member 1개 꺼내오기
		//findById(id).get()
		Optional<Member> member = memberRepository.findById(id);
		if(member.isPresent()) {
			//entity -> dto 변환
			MemberDTO memberDTO = MemberDTO.toSaveDTO(member.get());
			return memberDTO;
		}else {
			throw new BootBoardException("찾는 데이터가 없습니다.");
		}
		
	}

	public void deleteById(Long id) {
		memberRepository.deleteById(id);
	}

	public MemberDTO login(MemberDTO memberDTO) {
		// 1. 이메일로 회원 조회(이메일과 비밀번호를 비교)
		Optional<Member> memberEmail = memberRepository.findByMemberEmail(memberDTO.getMemberEmail());
		if(memberEmail.isPresent()) {
			//조회 결과 이메일이 있다면 - 1건 가져옴
			Member member = memberEmail.get();
			//비밀번호 일치
			if(member.getMemberPassword().equals(memberDTO.getMemberPassword())) {
				//entity -> dto로 변환
				MemberDTO dto = MemberDTO.toSaveDTO(member);
				return dto;
			}else {
				//비밀번호 불일치
				return null;
			}
		}else {
			return null;
		}
	}

	public MemberDTO findByMemberEmail(String email) {
		//db에 있는 이메일을 검색한 회원 객체를 가져오고 
		Member member = memberRepository.findByMemberEmail(email).get();
		//회원객체(entity)를 dto로 바꿈
		MemberDTO memberDTO = MemberDTO.toSaveDTO(member);
		return memberDTO;
	}

	public void update(MemberDTO memberDTO) {
		//save는 가입할때는 id가 없음, 수정할때는 id가 존재함
		//id가 있는 엔티티의 메서드가 필요함
		Member member = Member.toUpdateEntity(memberDTO);
		
		memberRepository.save(member);
		
	}

	public String checkEmail(String memberEmail) {
		//db에 있는 이메일을 조회해서 있으면 "OK" 없으면 "NO"를 보냄
		Optional<Member> findMember = memberRepository.findByMemberEmail(memberEmail);
		if(findMember.isEmpty()) {
			return "OK";
		}else {
			return "NO";
		}
	}
}