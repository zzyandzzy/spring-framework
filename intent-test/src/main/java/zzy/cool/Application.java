package zzy.cool;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author intent zzy.main@gmail.com
 * @date 2021/1/13 19:35
 * @since 1.0
 */
public class Application {
	public static void main(String[] args) {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("ABCircularReference.xml");
	}
}
