package actions;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.ServletException;

import actions.views.EmployeeConverter;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.MessageConst;
import models.Employee;
import models.Follow;
import services.EmployeeService;
import services.FollowService;
import services.ReportService;

public class FollowAction extends ActionBase {

    private FollowService folService;
    private ReportService repService;
    private EmployeeService empService;

    @Override
    public void process() throws ServletException, IOException {

        folService = new FollowService();
        repService = new ReportService();
        empService = new EmployeeService();

        invoke();

        folService.close();
        repService.close();
        empService.close();

    }

    /**
     * フォローする
     * @throws ServletException
     * @throws IOException
     */
    public void create() throws ServletException, IOException {

        //ログイン従業員のデータを取得（Object型からEmployee型にダウンキャスト）
        Employee loginEmp = (Employee) (getSessionScope(AttributeConst.LOGIN_EMP));

        //フォロー対象従業員のデータを取得（EmployeeView型からEmployee型にキャスト）
        Employee followedEmp = EmployeeConverter.toModel(empService.findOne(toNumber(getRequestParam(AttributeConst.EMP_ID))));

        //Followモデルにデータをセット
        Follow fol = new Follow();
        LocalDateTime ldt = LocalDateTime.now();
        fol.setEmployee(loginEmp);
        fol.setFollowedEmployee(followedEmp);
        fol.setCreatedAt(ldt);
        fol.setUpdatedAt(ldt);

        List<String> errors = folService.create(loginEmp, followedEmp, fol);

        if (errors.size() > 0) {
            putSessionScope(AttributeConst.ERR, errors);
            forward(ForwardConst.FW_REP_INDEX);

        } else {
            putSessionScope(AttributeConst.FLUSH, MessageConst.I_FOLLOWED.getMessage());
            redirect(ForwardConst.ACT_REP, ForwardConst.CMD_TIMELINE);
        }
    }


    /**
     * アンフォローする
     * @throws ServletException
     * @throws IOException
     */
    public void destroy() throws ServletException, IOException {

        //ログイン従業員のデータを取得（Object型からEmployee型にダウンキャスト）
        Employee loginEmp = (Employee) (getSessionScope(AttributeConst.LOGIN_EMP));

        //フォロー対象従業員のデータを取得（EmployeeView型からEmployee型にキャスト）
        Employee followedEmp = EmployeeConverter.toModel(empService.findOne(toNumber(getRequestParam(AttributeConst.EMP_ID))));

        folService.destroy(loginEmp, followedEmp);

        putSessionScope(AttributeConst.FLUSH, MessageConst.I_UNFOLLOWED.getMessage());
        redirect(ForwardConst.ACT_REP, ForwardConst.CMD_TIMELINE);
    }


}
