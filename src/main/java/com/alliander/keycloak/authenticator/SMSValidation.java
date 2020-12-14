package com.alliander.keycloak.authenticator;

import org.keycloak.Config;
import org.keycloak.authentication.FormAction;
import org.keycloak.authentication.FormActionFactory;
import org.keycloak.authentication.FormContext;
import org.keycloak.authentication.ValidationContext;
import org.keycloak.events.Details;
import org.keycloak.events.Errors;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.*;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.provider.ProviderConfigProperty;

import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SMSValidation implements FormAction, FormActionFactory {
    public static final String PROVIDER_ID = "registration-phone-number-validation";

    @Override
    public String getHelpText() {
        return "Validates phone number attributes and stores them in user data.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return null;
    }

    @Override
    public void validate(ValidationContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        List<FormMessage> errors = new ArrayList<>();

        context.getEvent().detail(Details.REGISTER_METHOD, "form");
        String eventError = Errors.INVALID_REGISTRATION;

        String phoneNumber = formData.getFirst("user.attributes.phoneNumber");
        boolean phoneValid = true;
        if (phoneNumber.isBlank()) {
            errors.add(new FormMessage("phoneNumber", "Die Telefonnummer darf nicht leer sein"));
            phoneValid = false;
        } else if (!isPhoneValid(phoneNumber)) {
            errors.add(new FormMessage("phoneNumber", "Die Telefonnummer entspricht nicht den Anforderungen"));
            phoneValid = false;
        }

        if (errors.size() > 0) {
            context.error(eventError);
            context.validationError(formData, errors);
            return;

        } else {
            context.success();
        }
    }

    public boolean isPhoneValid(String phoneNumber) {
        Pattern pattern = Pattern.compile("^(((0041|\\+41)\\s{0,1}[1-9]{2}|0[1-9]{2})\\s{0,1}\\d{3}\\s{0,1}\\d{2}\\s{0,1}\\d{2})|((?!0041|\\+41)(00[1-9]{1,3}|\\+[1-9]{1,3})\\s{0,1}[\\d\\s]{5,15})$");
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    @Override
    public void success(FormContext context) {
         UserModel user = context.getUser();
         MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
         user.setSingleAttribute("phoneNumber", formData.getFirst("user.attributes.phoneNumber"));

    }

    @Override
    public void buildPage(FormContext context, LoginFormsProvider form) {
        // complete
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {

    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }


    @Override
    public void close() {

    }

    @Override
    public String getDisplayType() {
        return "PhoneNumber Validation";
    }

    @Override
    public String getReferenceCategory() {
        return null;
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    private static AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
        AuthenticationExecutionModel.Requirement.REQUIRED,
        AuthenticationExecutionModel.Requirement.DISABLED
    };

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public FormAction create(KeycloakSession session) {
        return this;
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
