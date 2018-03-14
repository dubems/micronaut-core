package io.micronaut.inject.constructor.nullableinjection

import io.micronaut.context.BeanContext
import io.micronaut.context.DefaultBeanContext
import io.micronaut.context.exceptions.DependencyInjectionException
import io.micronaut.context.BeanContext
import io.micronaut.context.DefaultBeanContext
import io.micronaut.context.exceptions.DependencyInjectionException
import io.micronaut.inject.BeanDefinition
import spock.lang.Specification

class ConstructorNullableInjectionSpec extends Specification {

    void "test nullable injection with constructor"() {
        given:
        BeanContext context = new DefaultBeanContext()
        context.start()

        when:"A bean is obtained that has a constructor with @Inject"
        B b =  context.getBean(B)

        then:"The implementation is not injected, but null is"
        b.a == null
    }

    void "test normal injection still fails"() {
        given:
        BeanContext context = new DefaultBeanContext()
        context.start()

        when:"A bean is obtained that has a constructor with @Inject"
        C c =  context.getBean(C)

        then:"The bean is not found"
        thrown(DependencyInjectionException)
    }
}