package services;

import constants.JpaConst;
import models.Follow;

/**
 * フォローテーブルの操作に関わる処理を行うクラス
 */
public class FollowService extends ServiceBase {

    /**
     * ログイン従業員のid、フォローされている可能性のある従業員のidを条件に検索し、データが取得できるかどうかで結果を返却する
     * @param ログイン従業員のid
     * @param ログイン従業員がフォローしている可能性のある従業員のid
     * @return 結果を返却する(フォローしている:true フォローしていない:false)
     */
    public boolean isFollow(int EmpId, int followedEmpId) {

        boolean isFollowResult = false;
        long count = em.createNamedQuery(JpaConst.Q_FOL_COUNT_FOLLOWER_BY_ID, Long.class)
                            .setParameter(JpaConst.JPQL_PARM_ID, EmpId)
                            .setParameter(JpaConst.JPQL_PARM_FOLLOWED_ID, followedEmpId)
                            .getSingleResult();

        if (count == 1) {
            isFollowResult = true;
        }

        return isFollowResult;
    }

    /**
    * 画面から選択された日報作成者をフォローし、フォローテーブルに登録する
    * @param empId ログインユーザのid
    * @param followedEmpId フォローしたい従業員id
    */
   public void create(Follow fol){

       createInternal(fol);
   }

   /**
    * フォローデータを1件登録する
    * @param rv フォローデータ
    */
   private void createInternal(Follow follow) {

       em.getTransaction().begin();
       em.persist(follow);
       em.getTransaction().commit();
   }

}
