package com.ibm.commerce.payment.samples.tokenization.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import com.ibm.commerce.payment.samples.tokenization.PaymentTokenSampleResource;

public class TokenManagementHelper {

	 private static final String CLASS_NAME = PaymentTokenSampleResource.class.getName();
		private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

		private static final ConcurrentMap<String, Map<String, String>> TOKEN_TO_PI_MAP = new ConcurrentHashMap<>();
	 
		public TokenManagementHelper() throws Exception {
		}
		
		public void saveToken(String token, Map<String, String> pi) {
			LOGGER.info("saveToken(" + token + ", " + pi + ")");
			LOGGER.info("TOKEN_TO_PI_MAP (before):" + TOKEN_TO_PI_MAP);
			TOKEN_TO_PI_MAP.put(token, new HashMap<>(pi));
			LOGGER.info("TOKEN_TO_PI_MAP (after):" + TOKEN_TO_PI_MAP);
		}

		public Map<String, String> fetchToken(String token) {
			LOGGER.info("fetchToken(" + token + ")");
			LOGGER.info("TOKEN_TO_PI_MAP:" + TOKEN_TO_PI_MAP);
			Map<String, String> pi = TOKEN_TO_PI_MAP.get(token);
			LOGGER.info("pi: " + pi);
			return pi == null ? null : new HashMap<>(pi);
		}

		public void removeToken(String token) throws Exception {
			LOGGER.info("removeToken(" + token + ")");
			LOGGER.info("TOKEN_TO_PI_MAP (before):" + TOKEN_TO_PI_MAP);
			TOKEN_TO_PI_MAP.remove(token);
			LOGGER.info("TOKEN_TO_PI_MAP (after):" + TOKEN_TO_PI_MAP);
		}

		public void saveTransToken(String token, String transactionToken, short status) {
			String transTokenName = PaymentTokenSampleResource.TRANSCTIONID + "_" + transactionToken;
			
			LOGGER.info("saveTransToken(" + token + ", " + transactionToken + ", " + status + ")");
			LOGGER.info("TOKEN_TO_PI_MAP (before):" + TOKEN_TO_PI_MAP);
			Map<String, String> pi = TOKEN_TO_PI_MAP.get(token);
			if (pi == null) {
				pi = new HashMap<String, String>();
				TOKEN_TO_PI_MAP.put(token, pi);
			}
			pi.put(transTokenName, String.valueOf(status));
			LOGGER.info("TOKEN_TO_PI_MAP (after):" + TOKEN_TO_PI_MAP);
		}

		public void updateToken(String token, Map<String, String> newPI)  {
			LOGGER.info("updateToken(" + token + ", " + newPI + ")");
			LOGGER.info("TOKEN_TO_PI_MAP (before):" + TOKEN_TO_PI_MAP);
			Map<String, String> pi = TOKEN_TO_PI_MAP.get(token);
			if (pi != null) {
				pi.putAll(newPI);
			}
			LOGGER.info("TOKEN_TO_PI_MAP (after):" + TOKEN_TO_PI_MAP);
		}

		public String getTransTokenStatus(String token, String transactionToken) {
			String transTokenName = PaymentTokenSampleResource.TRANSCTIONID + "_" + transactionToken;
			
			LOGGER.info("getTransTokenStatus(" + token + ", " + transactionToken + ")");
			LOGGER.info("TOKEN_TO_PI_MAP:" + TOKEN_TO_PI_MAP);
			Map<String, String> pi = TOKEN_TO_PI_MAP.get(token);
			String status = pi == null ? null : pi.get(transTokenName);
			LOGGER.info("status: " + status);
			return status;
		}
}
