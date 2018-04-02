package org.jokar.compiler.model;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.jokar.compiler.TypeUtil;

import java.util.ArrayList;
import java.util.HashMap;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by JokAr on 16/8/8.
 */
public class AnnotatedClass {

    private TypeElement mTypeElement;
    private ArrayList<BindViewField> mFields;
    private HashMap<Integer, ArrayList<BindViewField>> mFieldMap;
    private ArrayList<OnClickMethod> mMethods;
    private Elements mElements;

    public AnnotatedClass(TypeElement typeElement, Elements elements) {
        mTypeElement = typeElement;
        mElements = elements;
        mFields = new ArrayList<>();
        mMethods = new ArrayList<>();
        mFieldMap = new HashMap<>();
    }

    public String getFullClassName() {
        return mTypeElement.getQualifiedName().toString();
    }

    public void addField(BindViewField field) {
        mFields.add(field);
        if (mFieldMap.get(field.getMasterId()) == null) {
            ArrayList<BindViewField> fields = new ArrayList<>();
            fields.add(field);
            mFieldMap.put(field.getMasterId(), fields);
        } else {
            mFieldMap.get(field.getMasterId()).add(field);
        }
    }

    public void addMethod(OnClickMethod method) {
        mMethods.add(method);
    }

    public JavaFile generateFile() {
        //generateMethod
        MethodSpec.Builder injectMethod = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(TypeName.get(mTypeElement.asType()), "host", Modifier.FINAL)
                .addParameter(TypeName.OBJECT, "source", Modifier.FINAL)
                .addParameter(TypeUtil.PROVIDER, "provider", Modifier.FINAL);
        for (ArrayList<BindViewField> mFields : mFieldMap.values()) {
            if (mFields == null || mFields.size() <= 0) {
                continue;
            }
            int masterId = mFields.get(0).getMasterId();
            TypeSpec.Builder listenerBuilder = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(TypeUtil.ANDROID_TEXT_WATCHER)
                    .addMethod(MethodSpec.methodBuilder("beforeTextChanged")
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .returns(TypeName.VOID)
                            .addParameter(TypeUtil.ANDROID_CHAR, "charSequence")
                            .addParameter(TypeName.INT, "i")
                            .addParameter(TypeName.INT, "i1")
                            .addParameter(TypeName.INT, "i2")
                            .build())
                    .addMethod(MethodSpec.methodBuilder("onTextChanged")
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .returns(TypeName.VOID)
                            .addParameter(TypeUtil.ANDROID_CHAR, "charSequence")
                            .addParameter(TypeName.INT, "i")
                            .addParameter(TypeName.INT, "i1")
                            .addParameter(TypeName.INT, "i2")
                            .build());
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("afterTextChanged")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(TypeName.VOID)
                    .addParameter(TypeUtil.ANDROID_EDIT, "editable");
            for (BindViewField field : mFields) {
                injectMethod.addStatement("host.$N = ($T)(provider.findView(source, $L))",
                        field.getFieldName(),
                        ClassName.get(field.getFieldType()), field.getBranchId());
                methodBuilder.addStatement(
                        "           if (editable != null && editable.length() >"
                                + " 0) {\n"
                                + "                    (($T)host.$N)"
                                + ".setVisibility($T"
                                + ".VISIBLE);\n"
                                + "                } else {\n"
                                + "                    (($T)host.$N)"
                                + ".setVisibility($T"
                                + ".GONE)"
                                + ";\n"
                                + "                }", field.getFieldType(),
                        field.getFieldName(), field.getFieldType(),
                        field.getFieldType(), field.getFieldName(),
                        field.getFieldType());
                System.out.print("*********************");
                System.out.print(field.getFieldType());
                System.out.print("*********************");
            }
            methodBuilder.addStatement(
                    "           if (editable != null && editable.length() >"
                            + " 0) {\n" +
                            "(($T)provider.findView(source,  $L)).setVisibility"
                            + "($T.VISIBLE);"
                            + "                } else {\n"
                            + ";\n" +
                            "(($T)provider.findView(source,  $L)).setVisibility"
                            + "($T.GONE);"
                            + "                }",
                    TypeUtil.ANDROID_TEXT_VIEW, masterId,
                    TypeUtil.ANDROID_TEXT_VIEW,
                    TypeUtil.ANDROID_TEXT_VIEW,
                    masterId,
                    TypeUtil.ANDROID_TEXT_VIEW);
            listenerBuilder.addMethod(methodBuilder.build());
            injectMethod.addStatement("TextWatcher textWatcher = $L ", listenerBuilder.build());
            injectMethod.addStatement(
                    "(($T)(provider.findView(source, $L))).addTextChangedListener(textWatcher)",
                    TypeUtil.ANDROID_TEXT_VIEW, masterId);
        }

        //generaClass
        TypeSpec injectClass = TypeSpec.classBuilder(mTypeElement.getSimpleName() + "$$ViewInject")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(TypeUtil.INJET,
                        TypeName.get(mTypeElement.asType())))
                .addMethod(injectMethod.build())
                .build();

        String packgeName = mElements.getPackageOf(mTypeElement).getQualifiedName().toString();

        return JavaFile.builder(packgeName, injectClass).build();
    }
}
