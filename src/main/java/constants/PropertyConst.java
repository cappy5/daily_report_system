package constants;

/**
 * アプリケーションスコープのパラメータ名を定義するEnumクラス
 *
 */
public enum PropertyConst {

    PEPPER("Pepper");

    private final String text;

    private PropertyConst(final String text) {
        this.text = text;
    }

    public String getValue() {
        return this.text;
    }
}
