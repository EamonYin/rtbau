package com.eamon.rtbau.zara;

import com.eamon.rtbau.zara.entity.BrandSale;
import com.eamon.rtbau.zara.service.IZaraSaleService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/zara")
@Log4j2
public class ZaraController {
    @Autowired
    IZaraSaleService zaraSaleService;

    @PostMapping("/insertZaraSale")
    public List<BrandSale> insertZaraSale(@RequestBody BrandSale brandSale){
       return zaraSaleService.insertSale(brandSale);
    }

    @PostMapping("/deleteZaraSale")
    public Integer deleteZaraSale(){
        return zaraSaleService.deleteSale();
    }
}
