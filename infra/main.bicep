targetScope = 'resourceGroup'

param location string = resourceGroup().location
param environmentName string = 'dev'
param appName string = 'nebius-java-demo'

var tags = {
  env: environmentName
  project: appName
}

module monitoring 'modules/monitoring.bicep' = {
  name: 'monitoringDeployment'
  params: {
    location: location
    tags: tags
    workspaceName: 'log-${appName}-${environmentName}'
    appInsightsName: 'appi-${appName}-${environmentName}'
  }
}

module storage 'modules/storage.bicep' = {
  name: 'storageDeployment'
  params: {
    location: location
    tags: tags
    storageAccountName: 'st${replace(appName, '-', '')}${environmentName}'
  }
}

module acr 'modules/acr.bicep' = {
  name: 'acrDeployment'
  params: {
    location: location
    tags: tags
    acrName: 'cr${replace(appName, '-', '')}${environmentName}'
  }
}

module containerapp 'modules/containerapp.bicep' = {
  name: 'containerAppDeployment'
  params: {
    location: location
    tags: tags
    environmentName: 'cae-${appName}-${environmentName}'
    containerAppName: 'ca-${appName}-api-${environmentName}'
    logAnalyticsWorkspaceId: monitoring.outputs.logAnalyticsWorkspaceId
    acrName: acr.outputs.acrName
    storageConnectionString: storage.outputs.connectionString
  }
}

module functionapp 'modules/functionapp.bicep' = {
  name: 'functionAppDeployment'
  params: {
    location: location
    tags: tags
    functionAppName: 'func-${appName}-worker-${environmentName}'
    hostingPlanName: 'asp-${appName}-worker-${environmentName}'
    storageConnectionString: storage.outputs.connectionString
    appInsightsInstrumentationKey: monitoring.outputs.appInsightsInstrumentationKey
  }
}
