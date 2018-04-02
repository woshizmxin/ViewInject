package org.jokar.compiler;

import com.squareup.javapoet.ClassName;

/**
 * Created by JokAr on 16/8/8.
 */
public class TypeUtil {
    public static final ClassName ANDROID_EDIT = ClassName.get("android.text", "Editable");
    public static final ClassName ANDROID_CHAR = ClassName.get("java.lang", "CharSequence");
    public static final ClassName ANDROID_INT = ClassName.get("java.lang", "CharSequence");
    public static final ClassName ANDROID_ON_CLICK_LISTENER = ClassName.get("android.view", "View",
            "OnClickListener");
    public static final ClassName INJET = ClassName.get("org.compiler.inject", "Inject");
    public static final ClassName PROVIDER = ClassName.get("org.compiler.inject.provider",
            "Provider");
    public static final ClassName ANDROID_TEXT_WATCHER = ClassName.get("android.text",
            "TextWatcher");
}
