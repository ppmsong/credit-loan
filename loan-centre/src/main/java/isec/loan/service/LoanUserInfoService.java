package isec.loan.service;

import isec.loan.core.AbstractService;
import isec.loan.entity.LoanUserInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class LoanUserInfoService extends AbstractService<LoanUserInfo> {

}
