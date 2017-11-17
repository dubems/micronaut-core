/*
 * Copyright 2017 original authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.particleframework.validation;

import org.particleframework.aop.InterceptPhase;
import org.particleframework.aop.MethodInterceptor;
import org.particleframework.aop.MethodInvocationContext;

import javax.inject.Singleton;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;

/**
 * A {@link MethodInterceptor} that validates method invocations
 *
 * @author Graeme Rocher
 * @since 1.0
 */
@Singleton
public class ValidatingInterceptor implements MethodInterceptor {

    /**
     * The position of the interceptor. See {@link org.particleframework.core.order.Ordered}
     */
    public static final int POSITION = InterceptPhase.VALIDATE.getPosition();

    @Override
    public int getOrder() {
        return POSITION;
    }

    private final ExecutableValidator executableValidator;

    public ValidatingInterceptor(Optional<ValidatorFactory> validatorFactory) {

        executableValidator = validatorFactory
                                    .map(factory -> factory.getValidator().forExecutables())
                                    .orElse(null);

    }

    @Override
    public Object intercept(MethodInvocationContext context) {
        if (executableValidator == null) {
            return context.proceed();
        } else {
            Method targetMethod = context.getTargetMethod();

            Set<ConstraintViolation<Object>> constraintViolations = executableValidator
                    .validateParameters(
                            context.getTarget(),
                            targetMethod,
                            context.getParameterValues()
                    );
            if (constraintViolations.isEmpty()) {
                return context.proceed();
            } else {
                throw new ConstraintViolationException(constraintViolations);
            }
        }
    }
}