param location string
param tags object
param storageAccountName string

resource storageAccount 'Microsoft.Storage/storageAccounts@2022-09-01' = {
  name: substring(storageAccountName, 0, min(length(storageAccountName), 24))
  location: location
  tags: tags
  sku: {
    name: 'Standard_LRS'
  }
  kind: 'StorageV2'
  properties: {
    accessTier: 'Hot'
  }
}

var connString = 'DefaultEndpointsProtocol=https;AccountName=${storageAccount.name};EndpointSuffix=${environment().suffixes.storage};AccountKey=${storageAccount.listKeys().keys[0].value}'

resource queueService 'Microsoft.Storage/storageAccounts/queueServices@2022-09-01' = {
  parent: storageAccount
  name: 'default'
}

resource queue 'Microsoft.Storage/storageAccounts/queueServices/queues@2022-09-01' = {
  parent: queueService
  name: 'tickets'
}

resource tableService 'Microsoft.Storage/storageAccounts/tableServices@2022-09-01' = {
  parent: storageAccount
  name: 'default'
}

resource table 'Microsoft.Storage/storageAccounts/tableServices/tables@2022-09-01' = {
  parent: tableService
  name: 'ticketresults'
}

output storageAccountName string = storageAccount.name
output connectionString string = connString
