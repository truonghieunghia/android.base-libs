package groupbase.vn.thn.baselibs.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Created by nghiath on 4/3/15.
 */
@Target( ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonAnnotation {

    public String FieldName () default "";

    public Class< ? > FieldType () default Object.class;

    public boolean isObject() default false;
}
