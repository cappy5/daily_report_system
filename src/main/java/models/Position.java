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
 * 職位データのDTOモデル
 *
 */
@Table(name=JpaConst.TABLE_POS)
@NamedQueries({
    @NamedQuery(
            name = JpaConst.Q_POS_BY_POSCODE,
            query = JpaConst.Q_POS_BY_POSCODE_DEF)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Position {

    /**
     * id
     */
    @Id
    @Column(name = JpaConst.POS_COL_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 職位コード
     */
    @Column(name = JpaConst.POS_COL_POSITION_CODE, nullable = false, unique = true)
    private Integer positionCode;

    /**
     * 職位名
     */
    @Column(name = JpaConst.POS_COL_POSITION_NAME, nullable = false)
    private String positionName;

    /**
     * 承認レベル(0:承認不可 1:一次承認可能 2:最終承認可能)
     */
    @Column(name = JpaConst.POS_COL_APPROVE_LEVEL, nullable = false)
    private Integer approvalLevel;

    /**
     * 登録日時
     */
    @Column(name = JpaConst.POS_COL_CREATED_AT, nullable = false)
    private LocalDateTime createdAt;

    /**
     * 更新日時
     */
    @Column(name = JpaConst.POS_COL_UPDATED_AT, nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 削除フラグ
     */
    @Column(name = JpaConst.POS_COL_DELETE_FLAG, nullable = false)
    private Integer deleteFlag;

}
