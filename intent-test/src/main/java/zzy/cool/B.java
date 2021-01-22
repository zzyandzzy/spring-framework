package zzy.cool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

/**
 * @author intent zzy.main@gmail.com
 * @date 2021/1/14 16:21
 * @since 1.0
 */
@Component
public class B {
	private static final Log logger = LogFactory.getLog(Application.class);
	private A a;

	public A getA() {
		return a;
	}

	public void setA(A a) {
		this.a = a;
	}

	public void destroy() {
		logger.info("Bean B destroy.");
	}
}
