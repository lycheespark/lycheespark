package com.spark.modules.pages.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

/**
 * @author LYCHEE
 * 页面管理器
 */
@Controller
public class PageController {

    /**
     * 默认页面
     * @param model
     * @return
     */
    @GetMapping(value = "/")
    public String index(Model model) {
        model.addAttribute("currentTime", LocalDateTime.now());
        // 返回视图名称 "index"，对应 src/main/resources/templates/index.html
        return "/fileUpload/index";
    }


}
