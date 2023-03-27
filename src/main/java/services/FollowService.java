package services;

import java.util.ArrayList;
import java.util.List;

import constants.JpaConst;
import constants.MessageConst;
import models.Employee;
import models.Follow;

/**
 * フォローテーブルの操作に関わる処理を行うクラス
 */
public class FollowService extends ServiceBase {

    /**
     * ログイン従業員、フォローされている可能性のある従業員を条件に検索し、結果を返却する
     * @param ログイン従業員
     * @param ログイン従業員がフォローしている可能性のある従業員
     * @return 結果を返却する(true : すでにフォローしている false : フォローしていない)
     */
    public boolean isFollow(Employee employee, Employee followedEmp) {

        boolean isFollowResult = false;
        long count = em.createNamedQuery(JpaConst.Q_FOL_MY_COUNT_FOLLOWEE, Long.class)
                            .setParameter(JpaConst.JPQL_PARM_EMPLOYEE, employee)
                            .setParameter(JpaConst.JPQL_PARM_FOLLOWED_EMPLOYEE, followedEmp)
                            .getSingleResult();
        if (count >= 1) {
            isFollowResult = true;
        }
        return isFollowResult;
    }

    /**
    * 画面から選択された日報作成者をフォローし、フォローテーブルに登録する
    * @param empId ログイン従業員
    * @param followedEmpId フォローしたい従業員
    */
   public List<String> create(Employee loginEmp, Employee followedEmp, Follow fol) {

       List<String> errors = new ArrayList<String>();

       //すでにフォローしていたらエラーにする
       if (isFollow(loginEmp, followedEmp)) {
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

   public void destroy(Employee employee, Employee followedEmp) {

       //idを条件に登録済みの従業員情報を取得する
       Follow delFol = findOne(employee, followedEmp);

       //削除処理を行う
       destoryInternal(delFol);
   }

   /**
    * ログイン従業員、フォローされている従業員を条件にフォロー情報を検索する
    * @param ログイン従業員
    * @param ログイン従業員がフォローしている従業員
    * @return Follow
    */
   private Follow findOne(Employee employee, Employee followedEmployee) {

       Follow delFol = em.createNamedQuery(JpaConst.Q_FOL_MY_FOLOWEE, Follow.class)
                           .setParameter(JpaConst.JPQL_PARM_EMPLOYEE, employee)
                           .setParameter(JpaConst.JPQL_PARM_FOLLOWED_EMPLOYEE, followedEmployee)
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
