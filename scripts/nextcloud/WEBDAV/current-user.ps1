#!/usr/bin/env pwsh
param(
    [string]$EnvFile = (Join-Path $PSScriptRoot '.env'),
    [string]$Username
)

$ErrorActionPreference = 'Stop'

function Read-DotEnv([string]$Path) {
    if (-not (Test-Path -LiteralPath $Path)) {
        throw "Env file not found: $Path"
    }

    $values = @{}
    Get-Content -LiteralPath $Path | ForEach-Object {
        $line = $_.Trim()
        if ($line -and -not $line.StartsWith('#') -and $line.Contains('=')) {
            $idx = $line.IndexOf('=')
            $key = $line.Substring(0, $idx).Trim()
            $value = $line.Substring($idx + 1).Trim().Trim('"').Trim("'")
            $values[$key] = $value
        }
    }
    return $values
}

$envMap = Read-DotEnv -Path $EnvFile
$baseUrl = $envMap['NC_MCP_MAIN_BASE_URL'].TrimEnd('/')
$password = $envMap['NC_MCP_MAIN_APP_PASSWORD']

if ([string]::IsNullOrWhiteSpace($baseUrl) -or [string]::IsNullOrWhiteSpace($password)) {
    throw 'NC_MCP_MAIN_BASE_URL and NC_MCP_MAIN_APP_PASSWORD are required.'
}

$candidates = @()
if ($Username) {
    $candidates += $Username
} else {
    $candidates += $envMap['NC_MCP_MAIN_USERNAME']
    $candidates += $envMap['NC_MCP_MAIN_ACCOUNT_ID']
}
$candidates = $candidates | Where-Object { -not [string]::IsNullOrWhiteSpace($_) } | Select-Object -Unique
if (-not $candidates) {
    throw 'NC_MCP_MAIN_USERNAME or NC_MCP_MAIN_ACCOUNT_ID is required.'
}

foreach ($login in $candidates) {
    $authBytes = [System.Text.Encoding]::UTF8.GetBytes("${login}:${password}")
    $authHeader = 'Basic ' + [Convert]::ToBase64String($authBytes)
    $client = [System.Net.Http.HttpClient]::new()
    $client.DefaultRequestHeaders.Authorization = [System.Net.Http.Headers.AuthenticationHeaderValue]::Parse($authHeader)
    $client.DefaultRequestHeaders.Add('OCS-APIRequest', 'true')
    $client.DefaultRequestHeaders.Accept.ParseAdd('application/json')
    $client.Timeout = [TimeSpan]::FromSeconds(15)

    try {
        $response = $client.GetAsync("$baseUrl/ocs/v1.php/cloud/user").GetAwaiter().GetResult()
        $body = $response.Content.ReadAsStringAsync().GetAwaiter().GetResult()
        if ([int]$response.StatusCode -ne 200) {
            continue
        }

        $json = $body | ConvertFrom-Json
        [pscustomobject]@{
            HttpStatus = [int]$response.StatusCode
            Login = $login
            OcsStatus = $json.ocs.meta.status
            OcsStatusCode = $json.ocs.meta.statuscode
            Id = $json.ocs.data.id
            DisplayName = $json.ocs.data.displayname
            EmailPresent = -not [string]::IsNullOrWhiteSpace($json.ocs.data.email)
        } | ConvertTo-Json -Depth 4
        exit 0
    }
    finally {
        $client.Dispose()
    }
}

throw "OCS current-user failed for configured login candidates."
