package isec.loan.controller;

import com.github.pagehelper.PageHelper;
import isec.base.bean.MapBox;
import isec.loan.entity.Product;
import isec.loan.service.MoneyCalculateService;
import isec.loan.service.ProductService;
import isec.loan.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "product")
@Validated
public class ProductController {

    @Autowired
    ProductService productService;

    @Autowired
    MoneyCalculateService moneyCalculateService;

    @Autowired
    UserInfoService UserInfoService;

    /**
     * 产品列表
     *
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping("/queryProductList")
    public Map<String, Object> queryProductList(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        List<Map<String, Object>> data = new ArrayList<>();

        PageHelper.startPage(page, pageSize);
        PageHelper.orderBy("create_time");
        List<Product> productList = productService.findByWhere("is_delete=1");

        for (Product product : productList) {
            Map<String, Object> productData = new HashMap<String, Object>();
            productData.put("name", product.getName());
            productData.put("productId", product.getProductId());
            productData.put("borrowMoney", product.getBorrowMoney() / 100);
            data.add(productData);
        }
        return MapBox.instance().put("productList", data).toMap();
    }


    /**
     * 商品详情
     *
     * @param productId
     * @return
     */
    @RequestMapping("/queryProductDetails")
    public Map<String, Object> queryProductDetails(@NotBlank(message = "productId不能为空") String productId) {
        Map<String, Object> data = new HashMap<String, Object>();
        Product product = productService.findById(productId);

        data.put("borrowMoney", product.getBorrowMoney() / 100);
        data.put("days", product.getDays());
        data.put("riskCost", product.getRiskCost() / 100);
        data.put("realMoney", moneyCalculateService.getRealMoney(product.getBorrowMoney(), product.getRiskCost()));
        data.put("repayMoney",
                moneyCalculateService.getRepayMoney(product.getRete(), product.getBorrowMoney(), product.getDays()));
        data.put("overdueMoney",
                moneyCalculateService.getOverdueMoney(product.getOverdueRate(), product.getBorrowMoney()));

        return data;

    }

}
