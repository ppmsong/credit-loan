package isec.loan.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import isec.loan.core.Mapper;
import isec.loan.entity.PayFlow;

public interface PayFlowMapper extends Mapper<PayFlow> {
	@Select(" select COALESCE(SUM(total_amount),0) from pay_flow where status=2 and trade_type in (2,3) and trade_no=#{billNo}")
	int selectRePayMoney(@Param("billNo")String billNo);
}