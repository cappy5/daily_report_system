package actions;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.servlet.ServletException;

import actions.views.EmployeeView;
import actions.views.ReportView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.MessageConst;
import models.Follow;
import services.FollowService;
import services.ReportService;

public class FollowAction extends ActionBase {

    private FollowService folService;
    private ReportService repService;

    @Override
    public void process() throws ServletException, IOException {

        folService = new FollowService();
        repService = new ReportService();

        invoke();

        folService.close();
        repService.close();

    }

    /**
     * 新規登録を行う（まだ作成中だよ！）
     * @throws ServletException
     * @throws IOException
     */
    public void create() throws ServletException, IOException {

        if (checkToken()) {

            //ログインユーザのidを取得
            int empId = ((EmployeeView) (getSessionScope(AttributeConst.LOGIN_EMP))).getId();

            //フォロー対象従業員のidを取得
            ReportView rv = repService.findOne(toNumber(getRequestParam(AttributeConst.REP_ID)));
            int followedEmpId = rv.getId();

            //Followモデルにデータをセット
            Follow fol = new Follow();
            LocalDateTime ldt = LocalDateTime.now();
            fol.setEmployeeId(empId);
            fol.setFollowedEmployeeId(followedEmpId);
            fol.setCreatedAt(ldt);
            fol.setUpdatedAt(ldt);

            folService.create(fol);

            putSessionScope(AttributeConst.FLUSH, MessageConst.I_REGISTERED.getMessage());
            redirect(ForwardConst.ACT_FOL, ForwardConst.CMD_TIMELINE);
        }
    }

}
