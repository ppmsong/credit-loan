package isec.loan.service;

import isec.loan.core.AbstractService;
import isec.loan.entity.ActionRecord;
import isec.loan.entity.AdminUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by p on 2019/08/08.
 */
@Service
@Transactional
public class AdminUserService extends AbstractService<AdminUser> {

}
