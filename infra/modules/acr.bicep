param location string
param tags object
param acrName string

resource acr 'Microsoft.ContainerRegistry/registries@2023-01-01-preview' = {
  name: substring(acrName, 0, min(length(acrName), 50))
  location: location
  tags: tags
  sku: {
    name: 'Basic'
  }
  properties: {
    adminUserEnabled: true
  }
}

output acrName string = acr.name
output loginServer string = acr.properties.loginServer
