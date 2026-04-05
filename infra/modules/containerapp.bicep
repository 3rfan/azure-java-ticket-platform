param location string
param tags object
param environmentName string
param containerAppName string
param logAnalyticsWorkspaceId string
param acrName string
param storageConnectionString string

resource acr 'Microsoft.ContainerRegistry/registries@2023-01-01-preview' existing = {
  name: acrName
}

resource containerAppEnv 'Microsoft.App/managedEnvironments@2023-05-01' = {
  name: environmentName
  location: location
  tags: tags
  properties: {
    appLogsConfiguration: {
      destination: 'log-analytics'
      logAnalyticsConfiguration: {
        customerId: reference(logAnalyticsWorkspaceId, '2022-10-01').customerId
        sharedKey: listKeys(logAnalyticsWorkspaceId, '2022-10-01').primarySharedKey
      }
    }
  }
}

resource containerApp 'Microsoft.App/containerApps@2023-05-01' = {
  name: containerAppName
  location: location
  tags: tags
  properties: {
    managedEnvironmentId: containerAppEnv.id
    configuration: {
      ingress: {
        external: true
        targetPort: 8080
      }
      secrets: [
        {
          name: 'registry-password'
          value: acr.listCredentials().passwords[0].value
        }
        {
          name: 'storage-connection-string'
          value: storageConnectionString
        }
      ]
      registries: [
        {
          server: acr.properties.loginServer
          username: acr.listCredentials().username
          passwordSecretRef: 'registry-password'
        }
      ]
    }
    template: {
      containers: [
        {
          name: 'api'
          image: '${acr.properties.loginServer}/ticketapi:latest'
          env: [
            {
              name: 'PORT'
              value: '8080'
            }
            {
              name: 'AZURE_STORAGE_CONNECTION_STRING'
              secretRef: 'storage-connection-string'
            }
          ]
          resources: {
            cpu: json('0.5')
            memory: '1.0Gi'
          }
        }
      ]
      scale: {
        minReplicas: 0
        maxReplicas: 2
      }
    }
  }
}

output containerAppUrl string = containerApp.properties.configuration.ingress.fqdn
