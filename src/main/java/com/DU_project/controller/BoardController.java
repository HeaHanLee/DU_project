package com.DU_project.controller;

import com.DU_project.domain.BoardDto;
import com.DU_project.domain.PageHandler;
import com.DU_project.domain.SearchCondition;
import com.DU_project.service.BoardService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/board")
public class BoardController {
    @Autowired
    BoardService boardService;

    @PostMapping("/modify")
    public  String modify(BoardDto boardDto, HttpSession session, Model m, RedirectAttributes rattr){
        String writer = (String) session.getAttribute("id");
        boardDto.setWriter(writer);

        try {
            int rowCnt = boardService.modify(boardDto);

            if(rowCnt!=1)
                throw new Exception("Modify failed");

            // m.addAttribute("msg","WRT_OK");
            rattr.addFlashAttribute("msg","MOD_OK");

            return "redirect:/board/list";
        } catch (Exception e) {
            e.printStackTrace();
            m.addAttribute("boardDto",boardDto);
            m.addAttribute("msg","MOD_ERR");
            return "board";
        }
    }

    // 글 쓰기
    @RequestMapping(value="/write")
    public String write(BoardDto vo) throws IOException {
        // 파일 업로드 처리
        String fileName=null;
        MultipartFile uploadFile = vo.getUploadFile();
        if (!uploadFile.isEmpty()) {
            String originalFileName = uploadFile.getOriginalFilename();
            String ext = FilenameUtils.getExtension(originalFileName);	//확장자 구하기
            UUID uuid = UUID.randomUUID();	//UUID 구하기
            fileName=uuid+"."+ext;
            uploadFile.transferTo(new File("C:/Users/lhh91/IdeaProjects/ch4/src/main/webapp/resources/image" + fileName));
        }
        vo.setFileName(fileName);
        try {
            boardService.write(vo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "redirect:board";
    }

    @GetMapping("/write")
    public String write(Model m){
        m.addAttribute("mode","new");
        return "board"; // 읽기와 쓰기에 사용, 쓰기에 사용할때는 mode는 new로 지정

    }



    @PostMapping("/write")
    public  String write(BoardDto boardDto, HttpSession session,Model m, RedirectAttributes rattr){
        String writer = (String) session.getAttribute("id");
        boardDto.setWriter(writer);

        try {
            int rowCnt = boardService.write(boardDto);

            if(rowCnt!=1)
                throw new Exception("Write failed");

            // m.addAttribute("msg","WRT_OK");
            rattr.addFlashAttribute("msg","WRT_OK");

            return "redirect:/board/list";
        } catch (Exception e) {
            e.printStackTrace();
            m.addAttribute("boardDto",boardDto);
            m.addAttribute("msg","WRT_ERR");
            return "board";
        }

    }

    @PostMapping("/remove")
    public String remove(Integer bno,  RedirectAttributes rattr, HttpSession session) {
        String writer = (String)session.getAttribute("id");
        String msg = "DEL_OK";

        try {
            if(boardService.remove(bno, writer)!=1)
                throw new Exception("Delete failed.");
        } catch (Exception e) {
            e.printStackTrace();
            msg = "DEL_ERR";
        }

        rattr.addFlashAttribute("msg", msg);
        return "redirect:/board/list";
    }

    @GetMapping("/read")
    public String read(Integer bno,Integer page, Integer pageSize, Model m){
        try {
            BoardDto boardDto = boardService.read(bno);
            m.addAttribute("boardDto", boardDto);
            m.addAttribute("page",page);
            m.addAttribute("pageSize",pageSize);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "board";
    }

    @GetMapping("/list")
    public String list(Model m, SearchCondition sc, HttpServletRequest request) {
        if(!loginCheck(request))
            return "redirect:/login/login?toURL="+request.getRequestURL();  // 로그인을 안했으면 로그인 화면으로 이동

        try {
            int totalCnt = boardService.getSearchResultCnt(sc);
            m.addAttribute("totalCnt", totalCnt);

            PageHandler pageHandler = new PageHandler(totalCnt, sc);


            List<BoardDto> list = boardService.getSearchResultPage(sc);
            m.addAttribute("list", list);
            m.addAttribute("ph", pageHandler);



            Instant startOfToday = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
            m.addAttribute("startOfToday", startOfToday.toEpochMilli());
        } catch (Exception e) {
            e.printStackTrace();
            m.addAttribute("msg", "LIST_ERR");
            m.addAttribute("totalCnt", 0);
        }

        return "boardList"; // 로그인을 한 상태이면, 게시판 화면으로 이동
    }


    private boolean loginCheck(HttpServletRequest request) {
        // 1. 세션을 얻어서
        HttpSession session = request.getSession();
        // 2. 세션에 id가 있는지 확인, 있으면 true를 반환
        return session.getAttribute("id")!=null;
    }
}