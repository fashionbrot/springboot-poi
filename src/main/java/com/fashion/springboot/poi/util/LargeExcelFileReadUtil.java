package com.fashion.springboot.poi.util;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.InputStream;
import java.util.*;


public class LargeExcelFileReadUtil  {

    private LinkedHashMap<String, String> rowContents=new LinkedHashMap<String, String>();
    private  SheetHandler sheetHandler;

    public LinkedHashMap<String, String> getRowContents() {
        return rowContents;
    }
    public void setRowContents(LinkedHashMap<String, String> rowContents) {
        this.rowContents = rowContents;
    }

    public SheetHandler getSheetHandler() {
        return sheetHandler;
    }
    public void setSheetHandler(SheetHandler sheetHandler) {
        this.sheetHandler = sheetHandler;
    }
    //处理一个sheet
    public void processOneSheet(String filename) throws Exception {
        InputStream sheet2=null;
        OPCPackage pkg =null;
        try {
            pkg = OPCPackage.open(filename);
            XSSFReader r = new XSSFReader(pkg);
            SharedStringsTable sst = r.getSharedStringsTable();
            XMLReader parser = fetchSheetParser(sst);
            sheet2 = r.getSheet("rId1");
            InputSource sheetSource = new InputSource(sheet2);
            parser.parse(sheetSource);
            setRowContents(sheetHandler.getRowContents());
        }catch (Exception e) {
            e.printStackTrace();
            throw e;
        }finally{
            if(pkg!=null){
                pkg.close();
            }
            if(sheet2!=null){
                sheet2.close();
            }
        }
    }

    public void processOneSheet(InputStream inputStream) throws Exception {
        InputStream sheet2=null;
        OPCPackage pkg =null;
        try {
            pkg = OPCPackage.open(inputStream);
            XSSFReader r = new XSSFReader(pkg);
            SharedStringsTable sst = r.getSharedStringsTable();
            XMLReader parser = fetchSheetParser(sst);
            sheet2 = r.getSheet("rId1");
            InputSource sheetSource = new InputSource(sheet2);
            parser.parse(sheetSource);
            setRowContents(sheetHandler.getRowContents());
        }catch (Exception e) {
            e.printStackTrace();
            throw e;
        }finally{
            if(pkg!=null){
                pkg.close();
            }
            if(sheet2!=null){
                sheet2.close();
            }
        }
    }
    //处理多个sheet
    public void processAllSheets(String filename) throws Exception {
        OPCPackage pkg =null;
        InputStream sheet=null;
        try{
            pkg= OPCPackage.open(filename);
            XSSFReader r = new XSSFReader( pkg );
            SharedStringsTable sst = r.getSharedStringsTable();
            XMLReader parser = fetchSheetParser(sst);
            Iterator<InputStream> sheets = r.getSheetsData();
            while(sheets.hasNext()) {
                System.out.println("Processing new sheet:\n");
                sheet = sheets.next();
                InputSource sheetSource = new InputSource(sheet);
                parser.parse(sheetSource);
            }
        }catch (Exception e) {
            e.printStackTrace();
            throw e;
        }finally{
            if(pkg!=null){
                pkg.close();
            }
            if(sheet!=null){
                sheet.close();
            }
        }
    }

    public XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException {
        XMLReader parser =
                XMLReaderFactory.createXMLReader(
                        "com.sun.org.apache.xerces.internal.parsers.SAXParser"
                );
        setSheetHandler(new SheetHandler(sst));
        ContentHandler handler = (ContentHandler) sheetHandler;
        parser.setContentHandler(handler);
        return parser;
    }

    /**
     * See org.xml.sax.helpers.DefaultHandler javadocs
     */
    public static void main(String[] args) throws Exception {
        Long time= System.currentTimeMillis();
        LargeExcelFileReadUtil example = new LargeExcelFileReadUtil();

        example.processOneSheet("C:\\Users\\Administrator\\Desktop\\test.xlsx");
        Long endtime= System.currentTimeMillis();

        LinkedHashMap<String, String> map= example.getRowContents();
        int count=0;
        String prePos="";
        for(Map.Entry mm: map.entrySet()){
            String key = (String) mm.getKey();
            String value=(String) mm.getValue();
            System.out.println(mm.getKey()+":"+mm.getValue());
            if(!key.substring(1).equals(prePos)){
                prePos=key.substring(1);
                count++;
            }
        }

        System.out.println("解析数据"+count+"条;耗时"+(endtime-time)/1000+"秒");

        /**
         * 读取 xlsx 后日期 转换
         */
        Calendar calendar = new GregorianCalendar(1900,0,-1);
        Date d = calendar.getTime();
        Date dd = DateUtils.addDays(d, Integer.valueOf(43712));
    }

}
