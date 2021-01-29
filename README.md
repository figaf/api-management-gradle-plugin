# api-management-gradle-plugin
This plugin provides an integration with SAP API management platform. It can be managed Api Proxy objects and non encrypted key value map objects by this plugin.

## Requirements

Gradle 4.10 or later.

## Getting started

You need to organize modular structure, where each separate API Proxy/Key Value Mapping folder is a Gradle module.
Default project structure:
```
rootProject
├── apiProxy1TechnicalName
│   └── ...
├── apiProxy2TechnicalName
│   └── ...
├── keyValueMap1TechnicalName
│   └── ...
├── keyValueMap2TechnicalName
│   └── ...       
├── ...
├── build.gradle
└── gradle.properties
```
Use `downloadApiManagementObject` task to fetch and automatically unpack bundled API Proxy and Key Value Mapping sources. Just create a high-level folders for needed objects, where name of the folder is a technical name of the object. Then register these folders as a modules in `settings.gradle` (see later) and run `downloadApiManagementObject` task.

build.gradle
```
buildscript {
    repositories {
        mavenLocal()
        jcenter()
        maven { url "https://jitpack.io" }
    }
}

plugins {
    id 'com.figaf.api-management-plugin' version '2.0.RELEASE' apply false
}

configure(subprojects.findAll()) { sub ->

    if (sub.name.startsWith("apiproxy-")) {

        apply plugin: 'com.figaf.api-management-plugin'

        apiManagementPlugin {
            url = apiManagementUrl
            username = apiManagementUsername
            password = apiManagementPassword
            platformType = cloudPlatformType
            sourceFilePath = "$project.projectDir".toString()
            apiManagementObjectType = 'API_PROXY'
            httpClientsFactory = new com.figaf.integration.common.factory.HttpClientsFactory(
                project.hasProperty('connectionSettings.useProxyForConnections') ? project.property('connectionSettings.useProxyForConnections').toBoolean() : false,
                project.hasProperty('connectionSettings.connectionRequestTimeout') ? project.property('connectionSettings.connectionRequestTimeout').toInteger() : 300000,
                project.hasProperty('connectionSettings.connectTimeout') ? project.property('connectionSettings.connectTimeout').toInteger() : 300000,
                project.hasProperty('connectionSettings.socketTimeout') ? project.property('connectionSettings.socketTimeout').toInteger() : 300000
            )
        }
    }

    if (sub.name.startsWith("keyvaluemap-")) {

        apply plugin: 'com.figaf.api-management-plugin'

        apiManagementPlugin {
            url = apiManagementUrl
            username = apiManagementUsername
            password = apiManagementPassword
            platformType = cloudPlatformType
            sourceFilePath = "$project.projectDir".toString()
            apiManagementObjectType = 'KEY_VALUE_MAP'
            httpClientsFactory = new com.figaf.integration.common.factory.HttpClientsFactory(
                project.hasProperty('connectionSettings.useProxyForConnections') ? project.property('connectionSettings.useProxyForConnections').toBoolean() : false,
                project.hasProperty('connectionSettings.connectionRequestTimeout') ? project.property('connectionSettings.connectionRequestTimeout').toInteger() : 300000,
                project.hasProperty('connectionSettings.connectTimeout') ? project.property('connectionSettings.connectTimeout').toInteger() : 300000,
                project.hasProperty('connectionSettings.socketTimeout') ? project.property('connectionSettings.socketTimeout').toInteger() : 300000
            )
        }
    }

}
```

settings.gradle
```
pluginManagement {
    repositories {
        mavenLocal()
        maven { url "https://jitpack.io" }
        gradlePluginPortal()
    }
}

include "apiproxy-apiProxy1TechnicalName"
project (":apiproxy-apiProxy1TechnicalName").projectDir = file("apiProxy1TechnicalName")

include "apiproxy-apiProxy2TechnicalName"
project (":apiproxy-apiProxy2TechnicalName").projectDir = file("apiProxy2TechnicalName")

include "keyvaluemap-keyValueMap1TechnicalName"
project (":keyvaluemap-keyValueMap1TechnicalName").projectDir = file("keyValueMap1TechnicalName")

include "keyvaluemap-keyValueMap2TechnicalName"
project (":keyvaluemap-keyValueMap2TechnicalName").projectDir = file("keyValueMap2TechnicalName")
```

gradle.properties
```
apiManagementUrl=https://<...>.hana.ondemand.com:443
apiManagementUsername=user@company.com
apiManagementPassword=123456
cloudPlatformType=CLOUD_FOUNDRY
```

## Tasks
The plugin has 2 tasks
1. `uploadApiManagementObject` - upload APIProxy/Key Value Map to API Management platform
2. `downloadApiManagementObject` - download APIProxy/Key Value Map from API Management platform to the repository.

## Configuration
The tasks can be configured through an extension `apiManagementPlugin` which accepts several parameters:
* `url`* - basic path to the API Management agent. Example: `https://apiportalxxx.hanatrial.ondemand.com`
* `username`* - API Management username. Example: `S00000000`
* `password`* - API Management password. Example: `123456`
* `platformType`* - Cloud platform type. `NEO` or `CLOUD_FOUNDRY`. Default value: `NEO`.
* `sourceFilePath`* - path to the directory with the APIProxy/Key Value Map. Default value: `$project.projectDir` which means
that root directory of the APIProxy/Key Value Map will be taken. In most cases this parameter shouldn't be overridden but it can be any valid path.
Example: `C:\some\path`
* `apiManagementObjectType`* - Type of artifact for which the tasks will be executed. `API_PROXY` or `KEY_VALUE_MAP`.
* `apiManagementObjectName` - APIProxy/Key Value Map name. By default the name of the folder is used. If your project structure is not standard
you can define this parameter directly. Example: `MyAPIProxy`
* `ignoreFilesList` - list of files (or directories) which shouldn't be added to the archive when the plugin executes `uploadApiProxy` task and shouldn't be modified when the plugin executes `downloadApiProxy` task.
The plugin always adds to this list the following paths: `src/test`, `build.gradle`, `gradle.properties`, `settings.gradle`. Example: `["somefile.txt", "somefolder"]`
* `httpClientsFactory` - configuration for http requests. Its constructor has the following parameters: `useProxyForConnections`, `connectionRequestTimeout`, `connectTimeout`, `socketTimeout`.
If not provided it will use the following default values: `false`, `300000`, `300000`, `300000`.
