package actions;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import actions.views.EmployeeView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;
import constants.MessageConst;
import constants.PropertyConst;
import models.Position;
import services.EmployeeService;
import services.PositionService;

/**
 * 従業員に関わる処理を行うActionクラス
 *
 */
public class EmployeeAction extends ActionBase {

    private EmployeeService service;
    private PositionService posService;

    /**
     * メソッドを実行する
     */
    @Override
    public void process() throws ServletException, IOException {

        service = new EmployeeService();
        posService = new PositionService();

        invoke();

        service.close();
        posService.close();
    }

    /**
     * 一覧画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void index() throws ServletException, IOException {

        if (checkAdmin()) {
            int page = getPage();

            List<EmployeeView> employees = service.getPerPage(page);

            long employeeCount = service.countAll();
            List<Position> positions = posService.getAll();

            putRequestScope(AttributeConst.EMPLOYEES, employees);
            putRequestScope(AttributeConst.EMP_COUNT, employeeCount);
            putRequestScope(AttributeConst.PAGE, page);
            putRequestScope(AttributeConst.MAX_ROW, JpaConst.ROW_PER_PAGE);
            putRequestScope(AttributeConst.POSITIONS, positions);

            String flush = getSessionScope(AttributeConst.FLUSH);
            if (flush != null) {
                putRequestScope(AttributeConst.FLUSH, flush);
                removeSessionScope(AttributeConst.FLUSH);
            }

            forward(ForwardConst.FW_EMP_INDEX);
        }
    }

    /**
     * 新規登録画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void entryNew() throws ServletException, IOException {

        if (checkAdmin()) {
            putRequestScope(AttributeConst.TOKEN, getTokenId());
            putRequestScope(AttributeConst.EMPLOYEE, new EmployeeView());

            forward(ForwardConst.FW_EMP_NEW);
        }
    }

    /**
     * 新規登録を行う
     * @throws ServletException
     * @throws IOException
     */
    public void create() throws ServletException, IOException {

      //CSRF対策 tokenのチェック
        if (checkAdmin() && checkToken()) {

          //パラメータの値を元に職位データを取得する
            Position pos = new Position();
            pos = posService.findOne(toNumber(getRequestParam(AttributeConst.POS_CODE)));

          //パラメータの値を元に従業員情報のインスタンスを作成する
            EmployeeView ev = new EmployeeView(
                    null,
                    getRequestParam(AttributeConst.EMP_CODE),
                    getRequestParam(AttributeConst.EMP_NAME),
                    getRequestParam(AttributeConst.EMP_PASS),
                    toNumber(getRequestParam(AttributeConst.EMP_ADMIN_FLG)),
                    null,
                    null,
                    AttributeConst.DEL_FLAG_FALSE.getIntegerValue(),
                    pos
                    );

            String pepper = getContextScope(PropertyConst.PEPPER);

            List<String> errors = service.create(ev, pepper);

            if (errors.size() > 0) {
                putRequestScope(AttributeConst.TOKEN, getTokenId());
                putRequestScope(AttributeConst.EMPLOYEE, ev);
                putRequestScope(AttributeConst.ERR, errors);
                forward(ForwardConst.FW_EMP_NEW);
            } else {
                putSessionScope(AttributeConst.FLUSH, MessageConst.I_REGISTERED.getMessage());
                redirect(ForwardConst.ACT_EMP, ForwardConst.CMD_INDEX);
            }
        }
    }

    public void show() throws ServletException, IOException {

        if(checkAdmin()) {

            EmployeeView ev = service.findOne(toNumber(getRequestParam(AttributeConst.EMP_ID)));

            if (ev == null || ev.getDeleteFlag() == AttributeConst.DEL_FLAG_TRUE.getIntegerValue()) {
                forward(ForwardConst.FW_ERR_UNKNOWN);
                return;
            }

            putRequestScope(AttributeConst.EMPLOYEE, ev);
            forward(ForwardConst.FW_EMP_SHOW);
        }
    }

    public void edit() throws ServletException, IOException {

        if (checkAdmin()) {

            EmployeeView ev = service.findOne(toNumber(getRequestParam(AttributeConst.EMP_ID)));

            if (ev == null || ev.getDeleteFlag() == AttributeConst.DEL_FLAG_TRUE.getIntegerValue()) {
                forward(ForwardConst.FW_ERR_UNKNOWN);
                return;
            }

            putRequestScope(AttributeConst.TOKEN, getTokenId());
            putRequestScope(AttributeConst.EMPLOYEE, ev);

            forward(ForwardConst.FW_EMP_EDIT);
        }
    }

    public void update() throws ServletException, IOException {

        if (checkAdmin() && checkToken()) {

            //職位データを取得
            Position pos = posService.findOne(toNumber(getRequestParam(AttributeConst.POS_CODE)));

            EmployeeView ev = new EmployeeView(
                    toNumber(getRequestParam(AttributeConst.EMP_ID)),
                    getRequestParam(AttributeConst.EMP_CODE),
                    getRequestParam(AttributeConst.EMP_NAME),
                    getRequestParam(AttributeConst.EMP_PASS),
                    toNumber(getRequestParam(AttributeConst.EMP_ADMIN_FLG)),
                    null,
                    null,
                    AttributeConst.DEL_FLAG_FALSE.getIntegerValue(),
                    pos
                    );

            String pepper = getContextScope(PropertyConst.PEPPER);

            List<String> errors = service.update(ev, pepper);

            if (errors.size() > 0) {
                putRequestScope(AttributeConst.TOKEN, getTokenId());
                putRequestScope(AttributeConst.EMPLOYEE, ev);
                putRequestScope(AttributeConst.ERR, errors);
                forward(ForwardConst.FW_EMP_EDIT);
            } else {
                putSessionScope(AttributeConst.FLUSH, MessageConst.I_UPDATED.getMessage());
                redirect(ForwardConst.ACT_EMP, ForwardConst.CMD_INDEX);
            }
        }
    }

    public void destroy() throws ServletException, IOException {

        if (checkAdmin() && checkToken()) {
            service.destroy(toNumber(getRequestParam(AttributeConst.EMP_ID)));
            putSessionScope(AttributeConst.FLUSH, MessageConst.I_DELETED.getMessage());
        }

        redirect(ForwardConst.ACT_EMP, ForwardConst.CMD_INDEX);

    }

    /**
     * ログイン中の従業員が管理者かどうかチェックし、管理者でなければエラー画面を表示
     * true: 管理者 false: 管理者ではない
     * @throws ServletException
     * @throws IOException
     */
    public boolean checkAdmin() throws ServletException, IOException {
        EmployeeView ev = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);
        if (ev.getAdminFlag() != AttributeConst.ROLE_ADMIN.getIntegerValue()) {
            forward(ForwardConst.FW_ERR_UNKNOWN);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 検索条件に紐付く従業員データを取得し、一覧に検索結果を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void search() throws ServletException, IOException {

        //職位データを取得
        Position selectedPosition = posService.findOne(toNumber(getRequestParam(AttributeConst.POS_CODE)));


        if (checkAdmin()) {
            int page = getPage();

            List<EmployeeView> employees = service.getEmpByPosCodePerPage(page, selectedPosition);

            long employeeCount = service.countAll();
            List<Position> positions = posService.getAll();

            putRequestScope(AttributeConst.EMPLOYEES, employees);
            putRequestScope(AttributeConst.EMP_COUNT, employeeCount);
            putRequestScope(AttributeConst.PAGE, page);
            putRequestScope(AttributeConst.MAX_ROW, JpaConst.ROW_PER_PAGE);
            putRequestScope(AttributeConst.POSITIONS, positions);
            putRequestScope(AttributeConst.POS_SELECTED_POSITION, selectedPosition);

            String flush = getSessionScope(AttributeConst.FLUSH);
            if (flush != null) {
                putRequestScope(AttributeConst.FLUSH, flush);
                removeSessionScope(AttributeConst.FLUSH);
            }


            forward(ForwardConst.FW_EMP_INDEX);
        }

        }
    }
