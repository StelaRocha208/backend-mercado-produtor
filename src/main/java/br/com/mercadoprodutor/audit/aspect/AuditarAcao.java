package br.com.mercadoprodutor.audit.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//Anotação customizada 
@Target(ElementType.METHOD) 
@Retention(RetentionPolicy.RUNTIME) 
public @interface AuditarAcao {
    String valor();
}

