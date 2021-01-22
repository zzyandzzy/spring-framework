package zzy.cool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * @author intent zzy.main@gmail.com
 * @date 2021/1/16 23:21
 * @since 1.0
 */
@Component
public class BeanFactoryProcessor implements BeanFactoryPostProcessor {
	private static final Log logger = LogFactory.getLog(BeanFactoryProcessor.class);

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		for (String beanDefinitionName : beanFactory.getBeanDefinitionNames()) {
			logger.info(beanDefinitionName);
		}
	}
}
