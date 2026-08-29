package br.com.digidatasistemas.starterPackage.security.permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({
        ElementType.TYPE
})
@Retention(RetentionPolicy.RUNTIME)
public @interface ResourcePermission {

    String value();

    String permission() default "";
}