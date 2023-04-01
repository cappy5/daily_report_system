package services;

import java.util.List;

import constants.JpaConst;
import models.Position;

/**
 * 職位テーブルの操作に関わる処理を行うクラス
 */
public class PositionService extends ServiceBase {

    /**
     * 職位コードを条件に検索し、職位データを返却する
     * @param 職位コード
     * @return 職位モデルを返却する
     */
    public Position findOne(int position_code) {

            Position pos = em.createNamedQuery(JpaConst.Q_POS_BY_POSCODE, Position.class)
                            .setParameter(JpaConst.JPQL_PARM_POS_CODE, position_code)
                            .getSingleResult();

            return pos;

    }

    /**
     * 職位データを全件取得し、返却する
     * @return 職位モデルを返却する
     */
    public List<Position> getAll() {

            List<Position> pos = em.createNamedQuery(JpaConst.Q_POS_GET_ALL, Position.class)
                            .getResultList();
            return pos;

    }

}
