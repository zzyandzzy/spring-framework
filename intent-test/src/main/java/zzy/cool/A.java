package zzy.cool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

/**
 * @author intent zzy.main@gmail.com
 * @date 2021/1/14 16:21
 * @since 1.0
 */
@Component
public class A implements DisposableBean {
	private static final Log logger = LogFactory.getLog(Application.class);
	private B b;

	public B getB() {
		return b;
	}

	public void setB(B b) {
		this.b = b;
	}

	public void init() {
		logger.info("Bean A init.");
	}

	@Override
	public void destroy() throws Exception {
		this.b = null;
		logger.info("Bean A destroy.");
	}
}
