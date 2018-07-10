package demo;

import com.rpc.transport.NettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * Demo class
 *
 * @author wanglei
 * @date create in 13:41 2018/7/5
 */
@SpringBootApplication
public class Main {
    @Bean
    public NettyServer getRpcStart() {
        return new NettyServer();
    }

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Main.class, args);
//        Hello bean = (Hello) context.getBean("helloImpl");
//        bean.say("hello world!");
//
//        System.out.println("start success! listen port:" + ConfigProperties.getRpcPort());
    }
}
