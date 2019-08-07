package isec.loan.controller;

import com.alibaba.fastjson.JSONObject;
import isec.base.bean.MapBox;
import isec.base.util.S;
import isec.loan.common.In;
import isec.loan.core.PromptException;
import isec.loan.entity.Risk;
import isec.loan.entity.User;
import isec.loan.entity.UserInfo;
import isec.loan.service.RiskService;
import isec.loan.service.UserInfoService;
import isec.loan.service.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * Created by p on 2019/07/17.
 */
@RestController
@RequestMapping(value = "risk")
@Validated
public class RiskController {

    private Logger logger = LoggerFactory.getLogger(RiskController.class);

    @Autowired
    RiskService riskService;

    @Autowired
    UserService userService;

    @Autowired
    UserInfoService userInfoService;

    @GetMapping(value = "callBackOfCarrierGatherFinish")
    public void callBackOfCarrierGatherFinish(@RequestParam Map<String, String> map) {
        logger.info("callBackOfCarrierGatherFinish 接受数据=> " + JSONObject.toJSONString(map));
        if (S.isBlank(map.get("mobile"))) {
            logger.error("手机号码为空");
            return;
        }
        List<User> userList = userService.findByWhere("mobile=" + map.get("mobile"));
        if (CollectionUtils.isEmpty(userList)) {
            logger.error("找不到该用户");
            return;
        }

        String userId = userList.get(0).getUserId();
        String mobile = map.get("mobile");
        String taskId = map.get("taskId");
        String result = map.get("result");
        String message = map.get("message");

        Risk risk = new Risk();
        risk.setUserId(userList.get(0).getUserId());
        risk.setMobile(map.get("mobile"));
        risk.setName(map.get("name"));
        risk.setIdcard(map.get("idcard"));
        risk.setApiKey("carrierGatherFinish");
        risk.setResponse(JSONObject.toJSONString(map));
        if (!"true".equals(result)) {
            logger.info("运营商报告采集失败:" + message);
            risk.setStatus("fail");
        } else {
            risk.setStatus("success");
            logger.info("运营商报告采集成功");
        }

        riskService.saveOrUpdateRisk(risk);
        riskService.saveCarrierReport(userId, mobile, taskId);
    }

    @GetMapping(value = "callBackOfCarrierAccreditFinish")
    public void callBackOfCarrierAccreditFinish(@RequestParam Map<String, String> map) {
        logger.info("callBackOfCarrierAccreditFinish 接受数据=> " + JSONObject.toJSONString(map));
        if (S.isBlank(map.get("mobile"))) {
            logger.error("手机号码为空");
            return;
        }
        List<User> userList = userService.findByWhere("mobile=" + map.get("mobile"));
        if (CollectionUtils.isEmpty(userList)) {
            logger.error("找不到该用户");
            return;
        }
        Risk risk = new Risk();
        risk.setUserId(userList.get(0).getUserId());
        risk.setMobile(map.get("mobile"));
        risk.setName(map.get("name"));
        risk.setIdcard(map.get("idcard"));
        risk.setApiKey("carrierAccreditFinish");
        risk.setResponse(JSONObject.toJSONString(map));
        if ("1".equals(map.get("status"))) {
            risk.setStatus("success");
        } else if ("2".equals(map.get("status"))) {
            risk.setStatus("processing");
        } else {
            risk.setStatus("fail");
        }
        riskService.saveOrUpdateRisk(risk);
    }


    @PostMapping(value = "getCarrierAccreditUrl")
    public Map<String, Object> getCarrierAccreditUrl(@In User user, @NotBlank(message = "backUrl不为空") String backUrl) {
        UserInfo userInfo = userInfoService.findById(user.getUserId());
        if (userInfo == null) {
            throw new PromptException("userInfo 不存在");
        }
        String url = riskService.getCarrierAccreditUrl(user.getMobile(), userInfo.getName(), userInfo.getIdcard(), backUrl);
        return MapBox.instance().put("url", url).toMap();
    }


    /**
     * 淘宝授权异步回调：
     * {"result":"true","userId":"bafea7b22e23483a943323df8b732f50",    "taskId":"ee3b868a-b8e9-11e9-b15f-00163e0c310d"}
     *
     * @param map
     */
    @GetMapping(value = "callBackOfTaoBaoGatherFinish")
    public void callBackOfTaoBaoGatherFinish(@RequestParam Map<String, String> map) {
        logger.info("callBackOfTaoBaoGatherFinish 接受数据=> " + JSONObject.toJSONString(map));
        String userId = map.get("userId");
        if (S.isBlank(userId)) {
            logger.error("userId为空");
            return;
        }

        User user = userService.findById(map.get("userId"));
        if (user == null) {
            logger.error("找不到该用户");
            return;
        }

        String taskId = map.get("taskId");
        String result = map.get("result");
        String message = map.get("message");

        Risk risk = new Risk();
        risk.setUserId(userId);
        risk.setApiKey("taoBaoGatherFinish");
        risk.setResponse(JSONObject.toJSONString(map));
        if (!"true".equals(result)) {
            logger.info("淘宝数据采集失败:" + message);
            risk.setStatus("fail");
        } else {
            risk.setStatus("success");
            logger.info("淘宝数据采集成功");

            //更新芝麻分

        }

        riskService.saveOrUpdateRisk(risk);
        riskService.saveTaoBaoAnayReport(userId, taskId);
    }


    /**
     * 获取淘宝授权地址
     *
     * @param user    用户token
     * @param backUrl 授权成功同步跳转地址
     * @return
     */
    @PostMapping(value = "getTaoBaoAccreditUrl")
    public Map<String, Object> getTaoBaoAccreditUrl(@In User user, @NotBlank(message = "backUrl不为空") String backUrl) {
        String url = riskService.getTaoBaoAccreditUrl(user.getUserId(), backUrl);
        return MapBox.instance().put("url", url).toMap();
    }

}
