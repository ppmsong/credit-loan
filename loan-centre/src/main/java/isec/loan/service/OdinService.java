
package isec.loan.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import isec.base.util.Md5;
import isec.base.util.http.HttpClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OdinService {

    private static Logger logger = LoggerFactory.getLogger(OdinService.class);


    @Value("${config.odinPushKey}")
    private String odinPushKey;
    @Value("${config.odinPushSercet}")
    private String odinPushSercet;
    @Value("${config.odinPushUrl}")
    private String odinPushUrl;
    @Value("${spring.profiles.active}")
    private String env;


    /**
     * 奥丁推送方法
     *
     * @param memberId 推送会员编号
     * @param content  推送内容
     * @param jumpTo   跳转
     * @param datas    扩展数据
     * @return
     */
    public String sendCommonPush(String memberId, String content, String jumpTo, Map<String, String> datas) {

        //请求参数
        JSONObject params = new JSONObject();

        //可使用平台，1 android;2 ios
        params.put("plats", new int[]{1, 2});
        //推送内容
        params.put("content", content);
        //推送范围: 1广播；2别名；3标签；4regid；5地理位置；
        params.put("target", 2);
        //推送类型：1通知；2自定义
        params.put("type", 1);
        //离线属性
        params.put("offlineTime",7);
        //请求别名
        params.put("alias", new String[]{memberId});
        //扩展数据 json，附加字段键值对
        datas.put("jumpTo", jumpTo);
        params.put("extras", JSON.toJSONString(datas));
        //plats包含2的情况下，0开发环境，1生产环境，默认为1
        if (!"prod".equals(env)){
            params.put("iosProduction",0);
        }

        //请求头
        Map headers = new HashMap<String, Object>(2);
        headers.put("odin-key", odinPushKey);
        headers.put("sign", Md5.bigDataMd5(params.toJSONString() + odinPushSercet));

        String batchId = HttpClientManager.getClient().httpPostWithoutParse(odinPushUrl, headers, JSON.toJSONString(params));
        logger.info("推送参数：{}", JSON.toJSONString(params));
        logger.info("推送批次号：{}", batchId);

        return batchId;

    }


}
