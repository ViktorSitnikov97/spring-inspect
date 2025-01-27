package app;

import java.lang.reflect.Proxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

// BEGIN
@Component
@Slf4j
public class CustomBeanPostProcessor implements BeanPostProcessor {

    private Map<String, Class> annotatedBeans = new HashMap<>();
    private Map<String, String> loggingLevels = new HashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        if (bean.getClass().isAnnotationPresent(Inspect.class)) {
            String level = bean.getClass().getAnnotation(Inspect.class).level();

            annotatedBeans.put(beanName, bean.getClass());
            loggingLevels.put(beanName, level);
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (!annotatedBeans.containsKey(beanName)) {
            return bean;
        }
        var beanClass = annotatedBeans.get(beanName);
        String level = loggingLevels.get(beanName);

        return Proxy.newProxyInstance(beanClass.getClassLoader(), beanClass.getInterfaces(), (proxy, method, args) -> {
            String message = String.format(
                    "Was called method: %s() with parameters: %s",
                    method.getName(),
                    Arrays.toString(args)
            );

            if (level.equals("info")) {
                log.info(message);
            } else {
                log.debug(message);
            }

            return method.invoke(bean, args);
        });
    }
}
// END
