package com.alliander.keycloak.authenticator;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.common.util.Time;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.util.List;

/**
 * Created by joris on 18/11/2016.
 */
public class SMSAuthenticatorUtil {

    private static Logger logger = Logger.getLogger(SMSAuthenticatorUtil.class);

    public static String getAttributeValue(UserModel user, String attributeName) {
        String result = null;
        List<String> values = user.getAttribute(attributeName);
        if(values != null && values.size() > 0) {
            result = values.get(0);
        }

        return result;
    }


    public static String getCredentialValue(AuthenticationFlowContext context, String type) {
        CredentialModel result = null;

        RealmModel realm = context.getRealm();
        UserModel user = context.getUser();
        KeycloakSession session = context.getSession();

        List<CredentialModel> creds = session.userCredentialManager().getStoredCredentialsByType(realm, user, type);
        if (!creds.isEmpty()) result = creds.get(0);

        return result.getValue();
    }

    public static void setCredentialValue(AuthenticationFlowContext context, String type, String value) {
        RealmModel realm = context.getRealm();
        UserModel user = context.getUser();
        KeycloakSession session = context.getSession();

        List<CredentialModel> creds = session.userCredentialManager().getStoredCredentialsByType(realm, user, type);
        if (creds.isEmpty()) {
            CredentialModel secret = new CredentialModel();
            secret.setType(type);
            secret.setValue(value);
            secret.setCreatedDate(Time.currentTimeMillis());
            session.userCredentialManager().createCredential(realm ,user, secret);
        } else {
            creds.get(0).setValue(value);
            session.userCredentialManager().updateCredential(realm, user, creds.get(0));
        }

    }

    public static String getConfigString(AuthenticatorConfigModel config, String configName) {
        return getConfigString(config, configName, null);
    }

    public static String getConfigString(AuthenticatorConfigModel config, String configName, String defaultValue) {

        String value = defaultValue;

        if (config.getConfig() != null) {
            // Get value
            value = config.getConfig().get(configName);
        }

        return value;
    }

    public static Long getConfigLong(AuthenticatorConfigModel config, String configName) {
        return getConfigLong(config, configName, null);
    }

    public static Long getConfigLong(AuthenticatorConfigModel config, String configName, Long defaultValue) {

        Long value = defaultValue;

        if (config.getConfig() != null) {
            // Get value
            Object obj = config.getConfig().get(configName);
            try {
                value = Long.valueOf((String) obj); // s --> ms
            } catch (NumberFormatException nfe) {
                logger.error("Can not convert " + obj + " to a number.");
            }
        }

        return value;
    }
}
