package actions.views;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 職位情報について画面の入力値・出力値を扱うViewモデル
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PositionView {

    /**
     * 職位コード
     */
    private Integer positionCode;

}
