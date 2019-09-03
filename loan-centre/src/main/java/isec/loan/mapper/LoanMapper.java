package isec.loan.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import isec.loan.core.Mapper;
import isec.loan.entity.Loan;

public interface LoanMapper extends Mapper<Loan> {
	@Select("select l.user_id userId, l.*  from (select * from loan order by user_id, create_time desc limit 0,9999999999999999999)l  group by user_id  having loan_status=7 and reset_user_verify_time < unix_timestamp()")
	List<Loan> selectCloseOverdueLoan();

}