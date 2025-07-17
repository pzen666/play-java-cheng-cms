package controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class JsonPX {

    public static void main(String[] args) {
        String path = "D:\\GX\\统发.json";
        //解析json文件，按照json文件内容不可修改文件结构及其显示顺序
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 读取JSON文件
            File file = new File(path);
            // 先读取为 JsonNode
            JsonNode jsonNode = objectMapper.readTree(file).get(0);
            JsonNode columns = jsonNode.get("columns");
            LinkedList<LinkedHashMap<String, Object>> columnsList = objectMapper.readValue(
                    columns.traverse(),
                    new TypeReference<LinkedList<LinkedHashMap<String, Object>>>() {
                    }
            );
            for (int i = 0; i < columnsList.size(); i++) {
                LinkedHashMap<String, Object> modifiedColumns = columnsList.get(i);
                modifiedColumns.put("column", i + 1);
            }
            // 将修改后的 jsonMap 输出为 JSON 字符串并打印
            String outputJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(columnsList);
            System.out.println(outputJson);


        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
