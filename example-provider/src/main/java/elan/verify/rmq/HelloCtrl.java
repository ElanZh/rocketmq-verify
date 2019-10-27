package elan.verify.rmq;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 张一然
 * @date 2019/10/27 下午11:30
 * @Package elan.verify.rmq
 * @Description
 */
@RestController
@RequestMapping("hello")
public class HelloCtrl {
    @GetMapping("")
    public String hello() {
        return "hello example-provider";
    }
}
