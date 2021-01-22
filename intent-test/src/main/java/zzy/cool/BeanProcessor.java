package zzy.cool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @author intent zzy.main@gmail.com
 * @date 2021/1/16 22:41
 * @since 1.0
 */
@Component
public class BeanProcessor implements BeanPostProcessor {
	private static final Log logger = LogFactory.getLog(BeanProcessor.class);

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		logger.info(beanName);
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		logger.info(beanName);
		return bean;
	}
}
