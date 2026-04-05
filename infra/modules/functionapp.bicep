param location string
param tags object
param functionAppName string
param hostingPlanName string
param storageConnectionString string
param appInsightsInstrumentationKey string

resource hostingPlan 'Microsoft.Web/serverfarms@2022-09-01' = {
  name: hostingPlanName
  location: location
  tags: tags
  sku: {
    name: 'Y1'
    tier: 'Dynamic'
  }
  properties: {
    reserved: true
  }
}

resource functionApp 'Microsoft.Web/sites@2022-09-01' = {
  name: functionAppName
  location: location
  tags: tags
  kind: 'functionapp,linux'
  properties: {
    serverFarmId: hostingPlan.id
    siteConfig: {
      appSettings: [
        {
          name: 'AzureWebJobsStorage'
          value: storageConnectionString
        }
        {
          name: 'FUNCTIONS_EXTENSION_VERSION'
          value: '~4'
        }
        {
          name: 'FUNCTIONS_WORKER_RUNTIME'
          value: 'java'
        }
        {
          name: 'APPINSIGHTS_INSTRUMENTATIONKEY'
          value: appInsightsInstrumentationKey
        }
        {
          name: 'AzureStorageConnectionString'
          value: storageConnectionString
        }
        {
          name: 'TableName'
          value: 'ticketresults'
        }
      ]
      linuxFxVersion: 'JAVA|21'
    }
  }
}
