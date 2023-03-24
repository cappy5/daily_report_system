package models;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import constants.JpaConst;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * フォローデータのDTOモデル
 *
 */
@Table(name=JpaConst.TABLE_FOL)
@NamedQueries({
        @NamedQuery(
                name = JpaConst.Q_FOL_COUNT_FOLLOWER,
                query = JpaConst.Q_FOL_COUNT_FOLLOWER_DEF),
        @NamedQuery(
                name = JpaConst.Q_FOL_GET_ALL_FOLLOWER,
                query = JpaConst.Q_FOL_GET_ALL_FOLLOWER_DEF)
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Follow {

    /**
     * id
     */
    @Id
    @Column(name = JpaConst.FOL_COL_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * フォローした従業員の社員番号
     */
    @Column(name = JpaConst.FOL_COL_EMP, nullable = false)
    private Integer employeeCode;

    /**
     * フォローされた従業員の社員番号
     */
    @Column(name = JpaConst.FOL_COL_FOL_EMP, nullable = false)
    private Integer followedEmployeeCode;

    /**
     * 登録日時
     */
    @Column(name = JpaConst.FOL_COL_CREATED_AT)
    private LocalDateTime createdAt;

    /**
     * 更新日時
     */
    @Column(name = JpaConst.FOL_COL_UPDATED_AT)
    private LocalDateTime updatedAt;
}