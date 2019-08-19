package isec.loan.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;

import isec.base.bean.MapBox;
import isec.base.util.S;
import isec.loan.entity.Advert;
import isec.loan.entity.Product;
import isec.loan.entity.Settings;
import isec.loan.service.AdvertService;
import isec.loan.service.SettingsService;

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


}
