# keycloak-sms-authenticator
Forked from https://github.com/Alliander/keycloak-sms-authenticator because it's not anymore maintaint
and not compatible with Keycloak 10 and above.


To install the SMS Authenticator one has to:

* Add the jar to the Keycloak server:
  * `$ cp build/libs/keycloak-sms-authenticator.jar _KEYCLOAK_HOME_/providers/`

* Add two templates to the Keycloak server:
  * `$ cp templates/sms-validation.ftl _KEYCLOAK_HOME_/themes/base/login/`
  * `$ cp templates/sms-validation-error.ftl _KEYCLOAK_HOME_/themes/base/login/`

* Add messages to the Keycloak server
  * Merge or add the message*.properties files from _templates/messages/*_ with the server files _KEYCLOAK_HOME\_/themes/base/login/_
    


Configure your REALM to use the SMS Authentication.
First create a new REALM (or select a previously created REALM).

Under Authentication > Flows:
* Copy 'Browse' flow to 'Browser with SMS' flow
* Click on 'Actions > Add execution on the 'Browser with SMS Forms' line and add the 'SMS Authentication'
* Set 'SMS Authentication' to 'REQUIRED' or 'ALTERNATIVE'
* To configure the SMS Authernticator, click on Actions  Config and fill in the attributes.


Under Authentication > Bindings:
* Select 'Browser with SMS' as the 'Browser Flow' for the REALM.
