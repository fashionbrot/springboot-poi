package com.fashion.springboot.poi.controller;



import com.alibaba.fastjson.JSON;
import com.fashion.springboot.poi.model.TestModel;
import com.fashion.springboot.poi.util.LargeExcelFileReadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Controller
@Slf4j
public class TestController {


    @RequestMapping("/test")
    @ResponseBody
    public String test(@RequestParam(value = "file",required = false) MultipartFile file){
        LargeExcelFileReadUtil example = new LargeExcelFileReadUtil();
        LinkedHashMap<String, String> rowMap=null;
        try {
            example.processOneSheet(file.getInputStream());
            rowMap = example.getRowContents();
        } catch (Exception e) {
            log.error("upload readExcel error",e);
        }

        if (CollectionUtils.isEmpty(rowMap)){
            throw new RuntimeException("读取："+file.getOriginalFilename()+"失败");
        }

        int count=2;
        List<TestModel>  testModelList = new ArrayList<>();
        String column1="";
        String column2="";
        for(Map.Entry entry:rowMap.entrySet()){

            String key= (String) entry.getKey();
            String value= (String) entry.getValue();
            if (("A"+count).equals(key)){
                column1=value;
            }else if (("B"+count).equals(key)){
                column2=value;

                testModelList.add(TestModel.builder()
                        .column1(column1)
                        .column2(column2)
                        .build());

                count++;
                column1="";
                column2="";
            }
        }

        log.info(JSON.toJSONString(testModelList));

        return JSON.toJSONString(testModelList);
    }

}
