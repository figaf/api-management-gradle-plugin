# api-management-gradle-plugin
This plugin provides an integration with SAP API management platform. It can be managed Api Proxy objects and non encrypted key value map objects by this plugin.

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
