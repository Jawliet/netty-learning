package org.nox.netty.rpc.consumer.config;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.NonNull;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

/**
 * @author Jawliet on 2019/3/14
 */
public class ClassPathRpcScanner extends ClassPathBeanDefinitionScanner {

    private RpcFactoryBean<?> rpcFactoryBean = new RpcFactoryBean<>();

    private Class<? extends Annotation> annotationClass;

    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    public ClassPathRpcScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    @NonNull
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

        if (beanDefinitions.isEmpty()) {
            logger.warn("No RPC mapper was found in '" + Arrays.toString(basePackages) + "' package. Please check your configuration.");
        } else {
            processBeanDefinitions(beanDefinitions);
        }
        return beanDefinitions;
    }

    public void registerFilters() {
        boolean acceptAllInterfaces = true;

        if (this.annotationClass != null) {
            addIncludeFilter(new AnnotationTypeFilter(this.annotationClass));
            acceptAllInterfaces = false;
        }

        if (acceptAllInterfaces) {
            addIncludeFilter((metadataReader, metadataReaderFactory) -> true);
        }

        // exclude package-info.java
        addExcludeFilter((metadataReader, metadataReaderFactory) -> {
            String className = metadataReader.getClassMetadata().getClassName();
            return className.endsWith("package-info");
        });
    }

    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {

        GenericBeanDefinition definition;

        for (BeanDefinitionHolder holder : beanDefinitions) {

            definition = (GenericBeanDefinition) holder.getBeanDefinition();
            definition.getConstructorArgumentValues().addGenericArgumentValue(Objects.requireNonNull(definition.getBeanClassName()));
            definition.setBeanClass(this.rpcFactoryBean.getClass());

            definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
            System.out.println(holder);
        }
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }
}
