package isec.loan.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import isec.base.bean.MapBox;
import isec.loan.core.PromptException;
import isec.loan.core.StatusCodeEnum;
import isec.loan.entity.Advert;
import isec.loan.entity.AppVersion;
import isec.loan.entity.Settings;
import isec.loan.service.AdvertService;
import isec.loan.service.AppVersionService;
import isec.loan.service.SettingsService;
import isec.loan.service.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author p
 * @date 2019/07/23
 */
@RestController
@RequestMapping(value = "common")
@Validated
public class CommonController {

    @Autowired
    SettingsService settingsService;
    @Autowired
    AdvertService advertService;
    @Autowired
    AppVersionService appVersionService;
    @Autowired
    TelegramService telegramService;


    /**
     * 获取全局配置
     *
     * @return
     */
    @PostMapping(value = "gloabSetting")
    public JSONObject globleSetting() {
        List<Settings> settingList = settingsService.findAll();
        JSONObject settingData = new JSONObject();
        for (Settings settings : settingList) {
            settingData.put(settings.getSetKey(), settings.getSetVal());
        }

        return settingData;

    }


    /**
     * 查询广告接口
     */
    @PostMapping(value = "queryAdverts")
    public Map<String, Object> queryAdverts(@Valid @NotBlank(message = "广告位不能为空") String advPosition,
                                            @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        List<Map<String, Object>> data = new ArrayList<>();

        PageHelper.startPage(page, pageSize);
        PageHelper.orderBy("sort asc");
        List<Advert> advertList = advertService.findByWhere("status=1 and adv_position='" + advPosition + "'");

        for (Advert advert : advertList) {
            Map<String, Object> advertData = new HashMap<String, Object>();
            advertData.put("name", advert.getName());
            advertData.put("type", advert.getType());
            advertData.put("clientType", advert.getClientType());
            advertData.put("imageUrl", advert.getImgUrl());
            advertData.put("info", advert.getInfo());
            advertData.put("remark", advert.getRemark());
            data.add(advertData);
        }
        return MapBox.instance().put("advertList", data).toMap();
    }


    /**
     * 获取最新版本号
     *
     * @param type
     * @return
     */
    @PostMapping(value = "getLastVersion")
    public Map<String, Object> getLastVersion(@NotBlank(message = "请传入客户端类型") @Digits(message = "类型必须为1或者2", integer = 1, fraction = 0) String type) {

        // 获取最新版本
        PageHelper.startPage(1, 1);
        PageHelper.orderBy("sort desc");
        AppVersion lastedAppVersion = appVersionService.findBy("type", type);

        if (null == lastedAppVersion) {
            throw new PromptException(StatusCodeEnum.APP_VERSION_NONE);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("type", lastedAppVersion.getType());
        data.put("imposed", lastedAppVersion.getImposed());
        data.put("showbox", lastedAppVersion.getShowbox());
        data.put("version", lastedAppVersion.getVersion());
        data.put("description", lastedAppVersion.getDescription());
        data.put("addTime", lastedAppVersion.getAddTime());

        return data;

    }


    /**
     * 发送tg验证码
     *
     * @param mark   标识，如还款时传账单号
     * @param tgType tg类型，参见枚举TgType
     * @return
     */
    @PostMapping(value = "sendTg")
    public void sendTg(String mark, int tgType) {

        telegramService.sendTgMsg2(mark, tgType);

    }

}
