package org.jokar.compiler.model;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.jokar.compiler.TypeUtil;

import java.util.ArrayList;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by JokAr on 16/8/8.
 */
public class AnnotatedClass {

    private TypeElement mTypeElement;
    private ArrayList<BindViewField> mFields;
    private ArrayList<OnClickMethod> mMethods;
    private Elements mElements;

    public AnnotatedClass(TypeElement typeElement, Elements elements) {
        mTypeElement = typeElement;
        mElements = elements;
        mFields = new ArrayList<>();
        mMethods = new ArrayList<>();
    }

    public String getFullClassName() {
        return mTypeElement.getQualifiedName().toString();
    }

    public void addField(BindViewField field) {
        mFields.add(field);
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
                .addParameter(TypeName.OBJECT, "source")
                .addParameter(TypeUtil.PROVIDER, "provider");

        for (BindViewField field : mFields) {
            // find views
            injectMethod.addStatement("host.$N = ($T)(provider.findView(source, $L))",
                    field.getFieldName(),
                    ClassName.get(field.getFieldType()), field.getBranchId());

            TypeSpec listener = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(TypeUtil.ANDROID_TEXT_WATCHER)
                    .addMethod(MethodSpec.methodBuilder("afterTextChanged")
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .returns(TypeName.VOID)
                            .addParameter(TypeUtil.ANDROID_EDIT, "editable")
                            .addStatement("(($T)host.$N).setVisibility($T.GONE);",
                                    field.getFieldType(), field.getFieldName(),
                                    field.getFieldType())
                            .build())
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
                            .build())
                    .build();
            injectMethod.addStatement("TextWatcher textWatcher = $L ", listener);
            System.out.print("*********************");
            System.out.print(field.getFieldType());
            System.out.print("*********************");
            injectMethod.addStatement(
                    "(($T)(provider.findView(source, $L))).addTextChangedListener(textWatcher)",
                    field.getFieldType(), field.getMasterId());
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
