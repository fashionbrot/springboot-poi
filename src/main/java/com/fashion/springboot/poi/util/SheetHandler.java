package com.fashion.springboot.poi.util;


import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.LinkedHashMap;

public class SheetHandler  extends DefaultHandler {

    private SharedStringsTable sst;
    private String lastContents;
    private boolean nextIsString;
    private String cellPosition;
    private LinkedHashMap<String, String> rowContents=new LinkedHashMap<String, String>();

    public LinkedHashMap<String, String> getRowContents() {
        return rowContents;
    }

    public void setRowContents(LinkedHashMap<String, String> rowContents) {
        this.rowContents = rowContents;
    }

    public SheetHandler(SharedStringsTable sst) {
        this.sst = sst;
    }

    public void startElement(String uri, String localName, String name,
                             Attributes attributes) throws SAXException {
        if(name.equals("c")) {
            //   System.out.print(attributes.getValue("r") + " - ");
            cellPosition=attributes.getValue("r");
            String cellType = attributes.getValue("t");
            if(cellType != null && cellType.equals("s")) {
                nextIsString = true;
            } else {
                nextIsString = false;
            }
        }
        // 清楚缓存内容
        lastContents = "";
    }

    public void endElement(String uri, String localName, String name)
            throws SAXException {
        if(nextIsString) {
            int idx = Integer.parseInt(lastContents);
            lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
            nextIsString = false;
        }

        if(name.equals("v")) {
//            System.out.println("lastContents:"+cellPosition+";"+lastContents);
            //数据读取结束后，将单元格坐标,内容存入map中
            if(!(cellPosition.length()==2)||(cellPosition.length()==2&&!"1".equals(cellPosition.substring(1)))){//不保存第一行数据
                rowContents.put(cellPosition, lastContents);
            }
        }
    }



    public void characters(char[] ch, int start, int length)
            throws SAXException {
        lastContents += new String(ch, start, length);
    }
}
