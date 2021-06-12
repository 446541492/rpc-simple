package com.demo;


import com.demo.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = {"/api/test"})
public class SayHelloController {
    @Autowired
    HelloService service;
    /**
     * 根据uuid获取用户或客服昵称，头像列表
     * 
     * @param ids
     * @return
     */
    @GetMapping("say")
    public String say(@RequestParam("msg") String msg) {
        return service.say(msg);
    }

}
