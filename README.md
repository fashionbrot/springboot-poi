# springboot-poi

#### 介绍
Springboot  poi上传并处理百万级数据 excel 

#### 软件架构

springboot poi 上传 excel 高效处理方式


```java

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

```

测试用例

![](document/TIM截图20191028235440.png)
![](document/test.xlsx)

