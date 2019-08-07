package isec.loan.mapper;

import isec.loan.core.Mapper;
import isec.loan.entity.Risk;
import org.apache.ibatis.annotations.Param;


/**
 * @author Administrator
 */
public interface RiskMapper extends Mapper<Risk> {

    int updateRiskLoan(@Param("userId") String userId, @Param("loanId") String loanId);
}