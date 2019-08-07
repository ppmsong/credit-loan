package isec.loan.service;

import isec.loan.core.AbstractService;
import isec.loan.entity.UserInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by p on 2019/07/17.
 */
@Service
@Transactional
public class UserInfoService extends AbstractService<UserInfo> {

}
