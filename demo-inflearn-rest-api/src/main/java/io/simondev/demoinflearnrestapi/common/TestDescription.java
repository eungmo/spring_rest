package io.simondev.demoinflearnrestapi.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// junit 4에는 디스크립션용 애노테이션이 없으므로
// 사설로 만들어서 써보자
// 붙일 수 있는 타겟
@Target(ElementType.METHOD)
// 얼마나 오래 가져갈 것인가
@Retention(RetentionPolicy.SOURCE)
public @interface TestDescription {
    String value();
}
