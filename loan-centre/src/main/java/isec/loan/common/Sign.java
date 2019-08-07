package isec.loan.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedMap;

public class Sign {

	private static Logger logger = LoggerFactory.getLogger(Sign.class);
	
	public static String sort(SortedMap<String, Object> param) {
		if(param.isEmpty()) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		// 所有参与传参的参数按照accsii排序（升序）
		Iterator<Entry<String, Object>> it = param.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Object> entry = (Entry<String, Object>) it.next();
			String k = entry.getKey();
			Object v = entry.getValue();
			if (null != v && !"".equals(v)) {
				sb.append(k + "=" + v + "&");
			}
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public static String sha1(String str) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1"); // 如果是SHA加密只需要将"SHA-1"改成"SHA"即可
			digest.update(str.getBytes());
			byte messageDigest[] = digest.digest();
			// Create Hex String
			StringBuffer hexStr = new StringBuffer();
			// 字节数组转换为 十六进制 数
			for (int i = 0; i < messageDigest.length; i++) {
				String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexStr.append(0);
				}
				hexStr.append(shaHex);
			}
			return hexStr.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getSign(SortedMap<String, Object> param, String key) {
		String s = sort(param) + "&key=" + key;
		logger.debug("签名串=>"+s);
		return sha1(s);
	}

}
