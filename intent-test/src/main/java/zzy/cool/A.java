package zzy.cool;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

/**
 * @author intent zzy.main@gmail.com
 * @date 2021/1/14 16:21
 * @since 1.0
 */
@Component
public class A implements DisposableBean {
	private B b;

	public B getB() {
		return b;
	}

	public void setB(B b) {
		this.b = b;
	}

	@Override
	public void destroy() throws Exception {
		this.b = null;
	}
}
