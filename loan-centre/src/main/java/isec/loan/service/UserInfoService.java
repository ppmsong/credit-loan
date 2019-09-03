package isec.loan.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import isec.base.util.S;
import isec.loan.core.AbstractService;
import isec.loan.entity.UserInfo;

/**
 * Created by p on 2019/07/17.
 */
@Service
@Transactional
public class UserInfoService extends AbstractService<UserInfo> {

	public void resetUserInfo(String userId) {
		UserInfo userInfo = findById(userId);
		userInfo.setZhimaVerify(0);
		userInfo.setOperatorVerify(0);
		userInfo.setBankVerify(0);
		userInfo.setContactVerify(0);
		userInfo.setUpdateTime(S.getCurrentTimestamp());
		update(userInfo);
	}
}
