package isec.loan.controller;

import com.alibaba.fastjson.JSONObject;
import isec.loan.entity.Settings;
import isec.loan.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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


    /**
     * 生成账单
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


}
