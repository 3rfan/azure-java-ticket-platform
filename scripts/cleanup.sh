#!/bin/bash

# Define variables
RESOURCE_GROUP="rg-nebius-java-demo-dev"

echo "Checking if resource group $RESOURCE_GROUP exists..."
EXISTS=$(az group exists --name $RESOURCE_GROUP)

if [ "$EXISTS" == "true" ]; then
    echo "Resource group found. Beginning deletion..."
    # Perform deletion asynchronously to avoid hanging
    az group delete --name $RESOURCE_GROUP --yes --no-wait
    echo "Deletion request submitted. It may take a few minutes to complete in the background."
else
    echo "Resource group $RESOURCE_GROUP does not exist. Nothing to clean up."
fi
