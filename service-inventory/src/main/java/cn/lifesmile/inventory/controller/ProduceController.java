package cn.lifesmile.inventory.controller;


import cn.lifesmile.inventory.service.ProduceService;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/v1/produce")
public class ProduceController {

    @Autowired
    private ProduceService produceService;

    /**
     * 根据主键ID获取商品
     */
    @GetMapping("/find")
    public String findById(@RequestParam(value = "produceId") int produceId) {
        return JSON.toJSONString(produceService.findById(produceId));

    }

}
