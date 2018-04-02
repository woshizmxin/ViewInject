package org.jokar.compiler.model;

import com.annotation.BindView;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 * Created by JokAr on 16/8/8.
 */
public class BindViewField {
    private VariableElement mVariableElement;
    private int[] mresId;

    public BindViewField(Element element) throws IllegalArgumentException {
        if (element.getKind() != ElementKind.FIELD) {
            throw new IllegalArgumentException(
                    String.format("Only fields can be annotated with @%s",
                            BindView.class.getSimpleName()));
        }
        mVariableElement = (VariableElement) element;

        BindView bindView = mVariableElement.getAnnotation(BindView.class);
        mresId = bindView.value();
        if (mresId.length < 2 || mresId[0] <= 0 || mresId[1] <= 0) {
            throw new IllegalArgumentException(
                    String.format("value() in %s for field %s is not valid !",
                            BindView.class.getSimpleName(),
                            mVariableElement.getSimpleName()));
        }
    }

    /**
     * 获取变量名称
     */
    public Name getFieldName() {
        return mVariableElement.getSimpleName();
    }

    public Name getTextName() {
        Name name = new Name() {
            @Override
            public String toString() {
                return "android.widget.TextView";
            }

            @Override
            public boolean contentEquals(CharSequence charSequence) {
                return charSequence.equals(this);
            }

            @Override
            public int length() {
                return this.length();
            }

            @Override
            public char charAt(int i) {
                return this.charAt(i);
            }

            @Override
            public CharSequence subSequence(int i, int i1) {
                return this.subSequence(i, i1);
            }
        };
        return name;
    }

    /**
     * 获取变量id
     */
    public int getBranchId() {
        return mresId[0];
    }

    public int getMasterId() {
        return mresId[1];
    }

    /**
     * 获取变量类型
     */
    public TypeMirror getFieldType() {
        return mVariableElement.asType();
    }
}
