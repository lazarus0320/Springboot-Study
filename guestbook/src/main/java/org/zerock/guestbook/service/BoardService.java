package org.zerock.guestbook.service;

import org.zerock.guestbook.dto.BoardDTO;
import org.zerock.guestbook.dto.PageRequestDTO;
import org.zerock.guestbook.dto.PageResultDTO;
import org.zerock.guestbook.entity.Board;
import org.zerock.guestbook.entity.Member;

public interface BoardService {
    Long register(BoardDTO dto);
    PageResultDTO<BoardDTO, Object[]> getList(PageRequestDTO pageRequestDTO);
    BoardDTO get(Long bno);

    default Board dtoToEntity(BoardDTO dto) {
        Member member = Member.builder()
                .email(dto.getWriterEmail())
                .build();

        return Board.builder()
                .bno(dto.getBno())
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(member)
                .build();
    }

    default BoardDTO entityToDTO(Board board, Member member, Long replyCount) {

        return BoardDTO.builder()
                .bno(board.getBno())
                .title(board.getTitle())
                .content(board.getContent())
                .regDate(board.getRegDate())
                .modDate(board.getModDate())
                .writerEmail(member.getEmail())
                .writerName(member.getName())
                .replyCount(replyCount.intValue())
                .build();
    }
}
