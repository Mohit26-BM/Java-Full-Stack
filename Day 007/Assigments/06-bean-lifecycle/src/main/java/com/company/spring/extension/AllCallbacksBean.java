package com.company.spring.extension;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.company.spring.lifecycle.LifecycleLog;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/*
OPTIONAL EXTENSION: ONE bean using EVERY lifecycle mechanism at once,
to show the exact order Spring calls them in:

  INIT    : constructor -> BeanNameAware -> @PostConstruct
            -> InitializingBean.afterPropertiesSet() -> @Bean initMethod
  DESTROY : @PreDestroy -> DisposableBean.destroy() -> @Bean destroyMethod

In real code you pick ONE style per bean - this is only a comparison.
*/
public class AllCallbacksBean implements BeanNameAware, InitializingBean, DisposableBean {

    private static final String NAME = "AllCallbacksBean";

    public AllCallbacksBean() {
        LifecycleLog.step(NAME, "constructor");
    }

    @Override
    public void setBeanName(String beanName) {
        LifecycleLog.step(NAME, "BeanNameAware.setBeanName(\"" + beanName + "\")");
    }

    @PostConstruct
    public void postConstruct() {
        LifecycleLog.step(NAME, "@PostConstruct");
    }

    @Override
    public void afterPropertiesSet() {
        LifecycleLog.step(NAME, "InitializingBean.afterPropertiesSet()");
    }

    public void customInit() {
        LifecycleLog.step(NAME, "@Bean initMethod customInit()");
    }

    public void doWork() {
        LifecycleLog.step(NAME, "business method doWork()");
    }

    @PreDestroy
    public void preDestroy() {
        LifecycleLog.step(NAME, "@PreDestroy");
    }

    @Override
    public void destroy() {
        LifecycleLog.step(NAME, "DisposableBean.destroy()");
    }

    public void customDestroy() {
        LifecycleLog.step(NAME, "@Bean destroyMethod customDestroy()");
    }
}
