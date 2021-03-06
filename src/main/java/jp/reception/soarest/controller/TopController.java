package jp.reception.soarest.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.reception.soarest.domain.dto.LoginUserSearchResultDto;
import jp.reception.soarest.enums.UrlEnum;

/*
 * 管理画面TOPコントローラー
 * 
 * @author k.abe
 * @version 1.0
 */
@Controller
public class TopController {

    @Autowired
    HttpServletRequest request;

    @Autowired
    HttpSession session; 

    @RequestMapping(value = "/top", method = RequestMethod.GET)
    public String init(Model model) {

        // セッションの取得
        session = request.getSession(false);

        // セッション情報のチェック
        if (null == session|| null == (LoginUserSearchResultDto)session.getAttribute("loginUser")) {
            return "redirect:/";
        }

        // セッションから表示情報を取得
        model.addAttribute("loginUser", session.getAttribute("loginUser"));

        // return "top/top";
        return UrlEnum.TOP.getPass();
    }

}
