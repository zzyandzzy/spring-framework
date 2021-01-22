package zzy.cool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author intent zzy.main@gmail.com
 * @date 2021/1/13 19:35
 * @since 1.0
 */
@ComponentScan("zzy.cool")
public class Application {
	private static final Log logger = LogFactory.getLog(Application.class);

	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("ABCircularReference.xml");
		// b对象单例
		B b = (B) applicationContext.getBean("b");
		// a对象原型，原型每获取一遍就实例化一次
		A a = (A) applicationContext.getBean("a");
		applicationContext.close();
//		ApplicationContext applicationContext = new AnnotationConfigApplicationContext(Application.class);
	}
}
