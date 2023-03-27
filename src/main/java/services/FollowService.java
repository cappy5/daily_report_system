package services;

import java.util.ArrayList;
import java.util.List;

import constants.JpaConst;
import constants.MessageConst;
import models.Follow;

/**
 * フォローテーブルの操作に関わる処理を行うクラス
 */
public class FollowService extends ServiceBase {

    /**
     * ログイン従業員のid、フォローされている可能性のある従業員のidを条件に検索し、結果を返却する
     * @param ログイン従業員のid
     * @param ログイン従業員がフォローしている可能性のある従業員のid
     * @return 結果を返却する(true : すでにフォローしている false : フォローしていない)
     */
    public boolean isFollow(int EmpId, int followedEmpId) {

        boolean isFollowResult = false;
        long count = em.createNamedQuery(JpaConst.Q_FOL_COUNT_FOLLOWER_BY_ID, Long.class)
                            .setParameter(JpaConst.JPQL_PARM_ID, EmpId)
                            .setParameter(JpaConst.JPQL_PARM_FOLLOWED_ID, followedEmpId)
                            .getSingleResult();
        if (count >= 1) {
            isFollowResult = true;
        }
        return isFollowResult;
    }

    /**
    * 画面から選択された日報作成者をフォローし、フォローテーブルに登録する
    * @param empId ログインユーザのid
    * @param followedEmpId フォローしたい従業員id
    */
   public List<String> create(Follow fol){

       List<String> errors = new ArrayList<String>();

       long count = em.createNamedQuery(JpaConst.Q_FOL_COUNT_FOLLOWER_BY_ID, Long.class)
                       .setParameter(JpaConst.JPQL_PARM_ID, fol.getEmployeeId())
                       .setParameter(JpaConst.JPQL_PARM_FOLLOWED_ID, fol.getFollowedEmployeeId())
                       .getSingleResult();

       if (count >= 1) {
           errors.add(MessageConst.E_FOLLOW_EXIST.getMessage());
           return errors;
       }

       createInternal(fol);
       return errors;
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

   /**
    * ログインユーザ、フォローしている従業員idを条件にフォローデータを物理削除する
    * @param id
    */

   public void destroy(Integer empId, Integer followedEmpIp) {

       //idを条件に登録済みの従業員情報を取得する
       Follow delFol = findOne(empId, followedEmpIp);

       //削除処理を行う
       destoryInternal(delFol);

   }

   /**
    * ログイン従業員のid、フォローされている従業員のidを条件に検索する
    * @param ログイン従業員のid
    * @param ログイン従業員がフォローしている従業員のid
    * @return Follow
    */
   private Follow findOne(Integer empId, Integer followedEmpIp) {

       Follow delFol = em.createNamedQuery(JpaConst.Q_FOL_GET_BY_ID, Follow.class)
                           .setParameter(JpaConst.JPQL_PARM_ID, empId)
                           .setParameter(JpaConst.JPQL_PARM_FOLLOWED_ID, followedEmpIp)
                           .getSingleResult();
       return delFol;
}

/**
    * フォローデータを物理削除する
    * @param Follow
    */
   public void destoryInternal(Follow delFol) {

      em.getTransaction().begin();
      em.remove(delFol);
      em.getTransaction().commit();

   }
}
