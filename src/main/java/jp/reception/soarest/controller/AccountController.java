package jp.reception.soarest.controller;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.reception.soarest.common.utils.CommonUtils;
import jp.reception.soarest.domain.dto.AccountSearchDto;
import jp.reception.soarest.domain.dto.LoginUserSearchResultDto;
import jp.reception.soarest.enums.CharEnum;
import jp.reception.soarest.enums.UrlEnum;
import jp.reception.soarest.form.AccountSearchForm;
import jp.reception.soarest.service.AccountService;


/*
 * アカウント情報 コントローラー
 * 
 * @author k.abe
 * @version 1.0
 */
@Controller
public class AccountController {

    @Autowired
    // アカウント関連サービスクラス
    AccountService accountService;

    @Autowired
    HttpServletRequest request;

    @Autowired
    HttpSession session;

    // ロガー
    private final Logger logger = LoggerFactory.getLogger(LoginController.class);

    // アカウント情報一覧URL
    private final String ACCOUNT_LIST = "/account_list";

    // アカウント情報検索結果URL
    private final String ACCOUNT_SEARCH = "/account_search";

    // ログインユーザー
    private final String LOGIN_USER = "loginUser";

     /*
      * アカウント情報一覧 初期表示
      * 
      * @param model モデル
      * @param session セッション
      * @return アカウント情報一覧画面
      */
    @RequestMapping(value = ACCOUNT_LIST, method = RequestMethod.GET)
    public String init(Model model) {

        // セッション存在チェック
        session = request.getSession(false);
        if (null == session|| null == (LoginUserSearchResultDto)session.getAttribute(LOGIN_USER)) {
            return "redirect:/";
        }

        // セッションから表示情報を取得
        model.addAttribute("loginUser", session.getAttribute("loginUser"));

        // 初期処理
        try {
            accountService.init(model);
        } catch (SQLException e) {
            CommonUtils.outputErrLog(logger, e, "アカウント情報一覧の初期表示処理にて、例外が発生しました。");
            return "error";
        } catch (Exception e) {
            CommonUtils.outputErrLog(logger, e, "予期せぬ例外が発生しました。");
            return "error";
        }
        // アカウント情報一覧画面へ遷移
        return UrlEnum.ACCOUNT_LIST.getPass();
    }

     /*
      * アカウント情報一覧 検索処理
      * 
      * @param form アカウント情報一覧 フォームクラス 
      * @param model モデル
      * @return アカウント情報一覧画面
      */
    @RequestMapping(value = ACCOUNT_SEARCH, method = RequestMethod.GET)
    public String searchAccountList(@Validated AccountSearchForm form, Model model) {

        // 開始ログ
        logger.info(new Object(){}.getClass().getEnclosingMethod().getName() + CharEnum.START.getChar());

        // セッション存在チェック
        session = request.getSession(false);
        if (null == session|| null == (LoginUserSearchResultDto)session.getAttribute(LOGIN_USER)) {
            return "redirect:/";
        }
        // 検索値を入力欄に保持
        accountService.saveWord(form, model);

        // 入力チェック
        if(!accountService.inputCheck(form, model)) {
            return CharEnum.FORWARD.getChar() + UrlEnum.ACCOUNT_LIST.getUrl();
        }

        try {
            // 検索処理
            accountService.searchAccountList(form, new AccountSearchDto(), model);
        } catch (SQLException e) {
            CommonUtils.outputErrLog(logger, e, "アカウント情報一覧の検索処理にて、例外が発生しました。");
            // session.invalidate(); ←セッションを破棄するかは要相談
            return "error";
        } catch (Exception e) {
            CommonUtils.outputErrLog(logger, e, "予期せぬ例外が発生しました。");
            // session.invalidate(); ←セッションを破棄するかは要相談
            return "error";
        }
        // 終了ログ
        logger.info(new Object(){}.getClass().getEnclosingMethod().getName() + CharEnum.END.getChar());

        // ※forwardがないとプルダウンが表示されない。また、リダイレクトだとURLがaccount_listの
        // ままになるが、URLにパラメータが表示されないことに加え、検索結果も表示されない。
        // (redirectの場合、redirectAttributesにsetしないと連携できない)
        // return "redirect:/account_list";
        return CharEnum.FORWARD.getChar() + UrlEnum.ACCOUNT_LIST.getUrl();

   }
}
