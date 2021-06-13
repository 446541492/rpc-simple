package com.rpc.bean;

import com.demo.service.HelloService;
import com.rpc.proxy.MyProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;

/**
 * 所有实现了BeanDefinitionRegistryPostProcessor接口的bean，其postProcessBeanDefinitionRegistry方法都会调用，然后再调用其postProcessBeanFactory方法
 *
 * @author wanglei
 * @date create in 15:18 2018/7/10
 */
public class RpcBean implements BeanDefinitionRegistryPostProcessor {


    /**
     * 该方法的实现中，主要用来对bean定义做一些改变
     * @param beanFactory
     * @throws BeansException
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    /**
     * 该方法用来注册更多的bean到spring容器中
     * @param beanDefinitionRegistry
     * @throws BeansException
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        //todo 需要被代理的接口，暂时写死，可以改成读取配置
        Class<?> cls = HelloService.class;
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(cls);
        GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
        definition.getPropertyValues().add("interfaceClass", definition.getBeanClassName());
        definition.setBeanClass(MyProxyFactory.class);
        definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
        // 注册bean名,一般为类名首字母小写
        beanDefinitionRegistry.registerBeanDefinition("helloService", definition);
    }

}
