####################
#   General info   #
####################
Component Name : SDMiddleware
Component current version : v0.2.5.2-Beta
Component latest previous version : v0.2.5.1-Beta

####################
#   Depend. info   #
####################
EnvironmentUtil-v0.8-Alpha.jar
AC_CommunicationModule-v0.8-Beta.jar
SDCloudWebserviceInterface-v1.0.1.1-Beta.jar
SDLicensingWebserviceInterface-v0.2-Beta.jar (Can use SDLicensingWebserviceInterface-v0.2.1-Beta.jar including bug fix)
SDDiagBusinessLogic-v0.3.3-Beta.jar
SDDictManagement-v0.2-Beta.jar
SDVCIControlModule-v0.9-Beta.jar
SDVCISecurity-v0.2-Beta.jar
SDLicensingModule-v0.1-Alpha.jar

####################
#   Changelog info #
####################
Bugfixing: #1175, #1179
Improvements:
- Added support of SD Cloud Webservice on HTTP version no user credential webservices, and HTTPS for webservices using credential (SDMiddlewareConfig.xml latest version need to be used)
- Corrected bug in VCI update logic when comparing version number (bug #1179) (SD CLOUD Webserver VCI "version code" should contains "." like "01.01.17"
- Refactored some version checking/processing in the WrapperUtil.java class.
- Added some Unit Test for version comparison modification
- Corrected bug #1175 about wrong return code when timeout happen when checking the licensing version (also handled 404 or other server internal error status)
Comments: 	


