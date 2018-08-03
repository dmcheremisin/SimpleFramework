package simple.framework.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented // will be in java doc
@Inherited // will be inhereted by all child classes of parent with this annotation
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {
    String name();

    boolean lazyLoad() default false;
}
