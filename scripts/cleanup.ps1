$ResourceGroupName = "rg-nebius-java-demo-dev"

Write-Host "Checking if resource group $ResourceGroupName exists..."
$Exists = az group exists --name $ResourceGroupName

if ($Exists -eq "true") {
    Write-Host "Resource group found. Beginning deletion..."
    az group delete --name $ResourceGroupName --yes --no-wait
    Write-Host "Deletion request submitted. It may take a few minutes to complete in the background."
} else {
    Write-Host "Resource group $ResourceGroupName does not exist. Nothing to clean up."
}
