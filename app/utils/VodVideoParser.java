package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import entity.JsonAnyData;
import entity.vo.VodVideoClassVO;
import entity.vo.VodVideoListVO;
import entity.vo.VodVideoVO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import play.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 采集接口解析工具
 */
public class VodVideoParser {

    private static final Logger.ALogger log = Logger.of(VodVideoParser.class);

    public static void main(String[] args) throws Exception {
        String json = "https://api.wujinapi.me/api.php/provide/vod/from/wjm3u8/";
        String xml = "http://api.ffzyapi.com/api.php/provide/vod/at/xml/";
        VodVideoVO jsonVodVideoVO = json(json);
        VodVideoVO xmlVodVideoVO = xml(xml);
        log.info("66666666666");
    }

    /**
     * xml 解析
     *
     * @param xmlUrl
     * @return
     * @throws Exception
     */
    public static VodVideoVO xml(String xmlUrl) throws Exception {
        String xmlData = getHttpContent(xmlUrl); // 完整的 XML 字符串
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            // 关闭 DTD 验证，防止因外部资源缺失导致解析失败
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setValidating(false);
            factory.setNamespaceAware(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xmlData));
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();
            Element root = doc.getDocumentElement();
            if (!"rss".equalsIgnoreCase(root.getTagName())) {
                throw new IllegalArgumentException("XML 根节点不是 <rss>");
            }
            // 解析 list 节点
            Element listElement = (Element) doc.getElementsByTagName("list").item(0);
            if (listElement == null) {
                log.error("XML 数据中不存在 <list> 节点");
                throw new RuntimeException("<list> 节点不存在，请检查 XML 数据");
            }
            VodVideoVO vodVideoVO = new VodVideoVO();
            String page = listElement.getAttribute("page");
            String pageCount = listElement.getAttribute("pagecount");
            String pageSize = listElement.getAttribute("pagesize");
            String recordCount = listElement.getAttribute("recordcount");
            vodVideoVO.setCode("0");
            vodVideoVO.setMsg("success");
            vodVideoVO.setPage(page);
            vodVideoVO.setPageCount(pageCount);
            vodVideoVO.setLimit(pageSize);
            vodVideoVO.setTotal(recordCount);
            List<VodVideoListVO> vodVideoListVOList = new ArrayList<>();
            NodeList videoList = listElement.getElementsByTagName("video");
            if (videoList == null || videoList.getLength() == 0) {
                log.error("⚠️ 未找到 <video> 节点，可能是解析错误或 XML 内容为空");
            } else {
                for (int i = 0; i < videoList.getLength(); i++) {
                    Element video = (Element) videoList.item(i);
                    VodVideoListVO v = new VodVideoListVO();
                    v.setVodId(getTextContent(video, "id"));
                    v.setVodName(getTextContent(video, "name"));
                    v.setTypeId(getTextContent(video, "tid"));
                    v.setTypeName(getTextContent(video, "type"));
                    v.setVodEn("");
                    v.setVodTime(getTextContent(video, "last"));
                    v.setVodRemarks("");
                    v.setVodPlayFrom(getTextContent(video, "dt"));
                    vodVideoListVOList.add(v);
                }
            }
            vodVideoVO.setList(vodVideoListVOList);
            // 解析 class 节点
            Element classElement = (Element) doc.getElementsByTagName("class").item(0);
            List<VodVideoClassVO> classTypeList = new ArrayList<>();
            if (classElement != null) {
                NodeList tyList = classElement.getElementsByTagName("ty");
                for (int i = 0; i < tyList.getLength(); i++) {
                    Element ty = (Element) tyList.item(i);
                    VodVideoClassVO v = new VodVideoClassVO();
                    v.setTypeId(ty.getAttribute("id"));
                    v.setTypeName(ty.getTextContent());
                    classTypeList.add(v);
                }
            }
            vodVideoVO.setClassType(classTypeList);
            return vodVideoVO;
        } catch (Exception e) {
            log.error("转换异常 : {}", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


    private static String getTextContent(Element parent, String tagName) {
        NodeList list = parent.getElementsByTagName(tagName);
        if (list.getLength() > 0) {
            return list.item(0).getTextContent();
        }
        return "";
    }

    /**
     * json 解析
     *
     * @param url
     * @return
     * @throws Exception
     */
    public static VodVideoVO json(String url) throws Exception {
        String jsonValue = getHttpContent(url);
        VodVideoVO vodVideoVO = new VodVideoVO();
        if (jsonValue != null && jsonValue.length() > 20) {
            ObjectMapper mapper = new ObjectMapper();
            JsonAnyData b = mapper.readValue(jsonValue, JsonAnyData.class);
            Map<String, Object> m = b.getOtherFields();
            vodVideoVO.setCode(m.get("code").toString());
            vodVideoVO.setMsg(m.get("msg").toString());
            vodVideoVO.setPage(m.get("page").toString());
            vodVideoVO.setPageCount(m.get("pagecount").toString());
            vodVideoVO.setLimit(m.get("limit").toString());
            vodVideoVO.setTotal(m.get("total").toString());
            List<Map<String, Object>> listData = (List<Map<String, Object>>) m.get("list");
            List<Map<String, Object>> classData = (List<Map<String, Object>>) m.get("class");
            List<VodVideoListVO> vodVideoListVOList = new ArrayList<>();
            List<VodVideoClassVO> classTypeList = new ArrayList<>();
            for (Map<String, Object> map : listData) {
                VodVideoListVO v = new VodVideoListVO();
                v.setVodId(map.get("vod_id").toString());
                v.setVodName(map.get("vod_name").toString());
                v.setTypeId(map.get("type_id").toString());
                v.setTypeName(map.get("type_name").toString());
                v.setVodEn(map.get("vod_en").toString());
                v.setVodTime(map.get("vod_time").toString());
                v.setVodRemarks(map.get("vod_remarks").toString());
                v.setVodPlayFrom(map.get("vod_play_from").toString());
                vodVideoListVOList.add(v);
            }
            for (Map<String, Object> map : classData) {
                VodVideoClassVO v = new VodVideoClassVO();
                v.setTypeId(map.get("type_id").toString());
                v.setTypeName(map.get("type_name").toString());
                classTypeList.add(v);
            }
            vodVideoVO.setList(vodVideoListVOList);
            vodVideoVO.setClassType(classTypeList);
        }
        return vodVideoVO;
    }


    /**
     * GET 请求  支持HTTP HTTPS
     *
     * @param urlString
     * @return
     * @throws Exception
     */
    private static String getHttpContent(String urlString) throws Exception {
        //获取http https  然后判断是否走ssl
        URL url = new URL(urlString);
        if ("https".equalsIgnoreCase(url.getProtocol())) {
            // 设置默认 SSL 协议
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, null, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        }
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
        conn.setRequestProperty("Connection", "keep-alive");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream())
        );
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }


}
