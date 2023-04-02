package actions;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import actions.views.EmployeeConverter;
import actions.views.EmployeeView;
import actions.views.ReportView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;
import constants.MessageConst;
import models.Employee;
import models.Position;
import services.EmployeeService;
import services.FollowService;
import services.PositionService;
import services.ReportService;

/**
 * 日報に関する処理を行うActionクラス
 *
 */
public class ReportAction extends ActionBase {

    private ReportService service;
    private FollowService followService;
    private EmployeeService empService;
    private PositionService posService;

    /**
     * メソッドを実行する
     */
    @Override
    public void process() throws ServletException, IOException {

        service = new ReportService();
        followService = new FollowService();
        empService = new EmployeeService();
        posService = new PositionService();

        invoke();

        service.close();
        followService.close();
        empService.close();
        posService.close();

    }

    /**
     * 一覧画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void index() throws ServletException, IOException {

        int page = getPage();
        List<ReportView> reports = service.getAllPerPage(page);

        long reportsCount = service.countAll();

        putRequestScope(AttributeConst.REPORTS, reports);
        putRequestScope(AttributeConst.REP_COUNT, reportsCount);
        putRequestScope(AttributeConst.PAGE, page);
        putRequestScope(AttributeConst.MAX_ROW, JpaConst.ROW_PER_PAGE);

        String flush = getSessionScope(AttributeConst.FLUSH);
        if (flush != null) {
            putRequestScope(AttributeConst.FLUSH, flush);
            removeSessionScope(AttributeConst.FLUSH);
        }

        forward(ForwardConst.FW_REP_INDEX);

    }



    /**
     * 新規登録画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void entryNew() throws ServletException, IOException {

        putRequestScope(AttributeConst.TOKEN, getTokenId());

        ReportView rv = new ReportView();
        rv.setReportDate(LocalDate.now());
        putRequestScope(AttributeConst.REPORT, rv);
        forward(ForwardConst.FW_REP_NEW);
    }

    /**
     * 新規登録を行う
     * @throws ServletException
     * @throws IOException
     */
    public void create() throws ServletException, IOException {

        if (checkToken()) {

            LocalDate day = null;
            if (getRequestParam(AttributeConst.REP_DATE).equals("")) {
                day = LocalDate.now();
            } else {
                day = LocalDate.parse(getRequestParam(AttributeConst.REP_DATE));
            }

            EmployeeView ev = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);
            ReportView rv = new ReportView(
                null,
                ev,
                day,
                getRequestParam(AttributeConst.REP_TITLE),
                getRequestParam(AttributeConst.REP_CONTENT),
                null,
                null,
                AttributeConst.REP_APPROVE_STATUS_UNAPPROVED.getIntegerValue()
                );

            List<String> errors = service.create(rv);

            if (errors.size() > 0) {
                putRequestScope(AttributeConst.TOKEN, getTokenId());
                putRequestScope(AttributeConst.REPORT, rv);
                putRequestScope(AttributeConst.ERR, errors);
                forward(ForwardConst.FW_REP_NEW);

            } else {
                putSessionScope(AttributeConst.FLUSH, MessageConst.I_REGISTERED.getMessage());

                redirect(ForwardConst.ACT_REP, ForwardConst.CMD_INDEX);
            }

        }
    }
    /**
     * 詳細画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void show() throws ServletException, IOException {

        ReportView rv = service.findOne(toNumber(getRequestParam(AttributeConst.REP_ID)));

        if (rv == null) {
            forward(ForwardConst.FW_ERR_UNKNOWN);
        } else {

            //ログイン従業員を取得
            Employee loginEmp = EmployeeConverter.toModel(getSessionScope(AttributeConst.LOGIN_EMP));

            //日報作成者を取得
            Employee targetEmp = empService.getEmp(toNumber(getRequestParam(AttributeConst.REP_ID)));

            //フォローしているか確認
            boolean isFollow = followService.isFollow(loginEmp, targetEmp);

            putRequestScope(AttributeConst.REPORT, rv);
            putRequestScope(AttributeConst.FOL_IS_FOLLOW, isFollow);
            forward(ForwardConst.FW_REP_SHOW);
        }
    }

    /**
     * 編集画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void edit() throws ServletException, IOException {

        ReportView rv = service.findOne(toNumber(getRequestParam(AttributeConst.REP_ID)));
        EmployeeView ev = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);

        if (rv == null || ev.getId() != rv.getEmployee().getId()) {
            forward(ForwardConst.FW_ERR_UNKNOWN);
        } else {
            putRequestScope(AttributeConst.TOKEN, getTokenId());
            putRequestScope(AttributeConst.REPORT, rv);

            forward(ForwardConst.FW_REP_EDIT);
        }
    }

    /**
     * 更新を行う
     * @throws ServletException
     * @throws IOException
     */
    public void update() throws ServletException, IOException {

        if (checkToken()) {
            ReportView rv = service.findOne(toNumber(getRequestParam(AttributeConst.REP_ID)));

            rv.setReportDate(toLocalDate(getRequestParam(AttributeConst.REP_DATE)));
            rv.setTitle(getRequestParam(AttributeConst.REP_TITLE));
            rv.setContent(getRequestParam(AttributeConst.REP_CONTENT));

            List<String> errors = service.update(rv);

            if (errors.size() > 0) {
                putRequestScope(AttributeConst.TOKEN, getTokenId());
                putRequestScope(AttributeConst.REPORT, rv);
                putRequestScope(AttributeConst.ERR, errors);

                forward(ForwardConst.FW_REP_EDIT);

            } else {
                putSessionScope(AttributeConst.FLUSH, MessageConst.I_UPDATED.getMessage());
                redirect(ForwardConst.ACT_REP, ForwardConst.CMD_INDEX);
            }
        }
    }

    /**
     * 承認を行う
     * @throws ServletException
     * @throws IOException
     */
    public void approve() throws ServletException, IOException {

        ReportView rv = service.findOne(toNumber(getRequestParam(AttributeConst.REP_ID)));
        Employee loginEmp = EmployeeConverter.toModel(getSessionScope(AttributeConst.LOGIN_EMP));

        if (loginEmp.getPosition().getPositionCode() == JpaConst.POS_POSITION_CHF) {
            rv.setApproveStatus(JpaConst.REP_APPROVE_STATUS_1ST_APPROVED);
        } else if (loginEmp.getPosition().getPositionCode() == JpaConst.POS_POSITION_MGR) {
            rv.setApproveStatus(JpaConst.REP_APPROVE_STATUS_FINAL_APPROVED);
        }

        List<String> errors = service.update(rv);

        if (errors.size() > 0) {
            putRequestScope(AttributeConst.TOKEN, getTokenId());
            putRequestScope(AttributeConst.REPORT, rv);
            putRequestScope(AttributeConst.ERR, errors);

        } else {
            putSessionScope(AttributeConst.FLUSH, MessageConst.I_APPROVED.getMessage());
        }
        redirect(ForwardConst.ACT_REP, ForwardConst.CMD_INDEX);
    }
    /**
     * 差し戻しを行う
     * @throws ServletException
     * @throws IOException
     */
    public void reject() throws ServletException, IOException {

        ReportView rv = service.findOne(toNumber(getRequestParam(AttributeConst.REP_ID)));
        Employee loginEmp = EmployeeConverter.toModel(getSessionScope(AttributeConst.LOGIN_EMP));

        if (loginEmp.getPosition().getPositionCode() == JpaConst.POS_POSITION_CHF || loginEmp.getPosition().getPositionCode() == JpaConst.POS_POSITION_MGR) {
            rv.setApproveStatus(JpaConst.REP_APPROVE_STATUS_REJECTED);
        }

        List<String> errors = service.update(rv);

        if (errors.size() > 0) {
            putRequestScope(AttributeConst.TOKEN, getTokenId());
            putRequestScope(AttributeConst.REPORT, rv);
            putRequestScope(AttributeConst.ERR, errors);

        } else {
            putSessionScope(AttributeConst.FLUSH, MessageConst.I_REJECTED.getMessage());
        }
        redirect(ForwardConst.ACT_REP, ForwardConst.CMD_INDEX);
    }

    /**
     * タイムラインを表示する
     * @throws ServletException
     * @throws IOException
     */
    public void timeline() throws ServletException, IOException {

        int page = getPage();

        //セッションスコープからデータ取得
        Employee loginEmp = EmployeeConverter.toModel(getSessionScope(AttributeConst.LOGIN_EMP));

        List<Position> positions = posService.getAll();

        int selectedApproveStatus = getRequestParam(AttributeConst.REP_APPROVE_STATUS) == null || getRequestParam(AttributeConst.REP_APPROVE_STATUS).length() == 0
                                    ? AttributeConst.REP_APPROVE_STATUS_ALL.getIntegerValue() //0
                                    : toNumber(getRequestParam(AttributeConst.REP_APPROVE_STATUS));

        List<ReportView> reports = new ArrayList<ReportView>();
        long reportsCount;

        //検索条件に基づきデータ取得
        //検索条件が「承認状況＝すべて」の場合
        if (selectedApproveStatus == AttributeConst.REP_APPROVE_STATUS_ALL.getIntegerValue()) {
            reports = service.getAllTimelinePerPage(loginEmp, page);
            reportsCount = service.countAllTimeline(loginEmp);
        //検索条件が「承認状況＝すべて」以外の場合
        } else {
            reports = service.getRepByStatusPerPage(loginEmp, selectedApproveStatus, page);
            reportsCount = service.countByStatus(loginEmp, selectedApproveStatus);
        }

        putRequestScope(AttributeConst.REPORTS, reports);
        putRequestScope(AttributeConst.REP_COUNT, reportsCount);
        putRequestScope(AttributeConst.PAGE, page);
        putRequestScope(AttributeConst.MAX_ROW, JpaConst.ROW_PER_PAGE);
        putRequestScope(AttributeConst.EMPLOYEE, loginEmp);
        putRequestScope(AttributeConst.POSITIONS, positions);
        putRequestScope(AttributeConst.REP_SELECTED_APPROVE_STATUS, selectedApproveStatus);

        String flush = getSessionScope(AttributeConst.FLUSH);
        if (flush != null) {
            putRequestScope(AttributeConst.FLUSH, flush);
            removeSessionScope(AttributeConst.FLUSH);
        }

        forward(ForwardConst.FW_REP_TIMELINE);

    }


}
