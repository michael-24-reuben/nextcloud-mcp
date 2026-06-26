#!/usr/bin/env pwsh
param(
    [string]$EnvFile = (Join-Path $PSScriptRoot '.env'),
    [string]$UploadDir,
    [string]$UserId
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

function ConvertTo-WebDavPath([string]$Path) {
    $segments = $Path.Trim('/').Split('/', [System.StringSplitOptions]::RemoveEmptyEntries) |
        ForEach-Object { [uri]::EscapeDataString($_) }
    return ($segments -join '/')
}

$envMap = Read-DotEnv -Path $EnvFile
$baseUrl = $envMap['NC_MCP_MAIN_BASE_URL']
$password = $envMap['NC_MCP_MAIN_APP_PASSWORD']
$targetDir = if ($UploadDir) { $UploadDir } else { $envMap['NC_MCP_SMOKE_TEST_UPLOAD_DIR'] }
if ([string]::IsNullOrWhiteSpace($targetDir)) { $targetDir = '/CodexScratch' }

foreach ($name in @('NC_MCP_MAIN_BASE_URL', 'NC_MCP_MAIN_APP_PASSWORD')) {
    if ([string]::IsNullOrWhiteSpace($envMap[$name]) -or
        $envMap[$name] -like 'replace-with-*' -or
        $envMap[$name] -like 'https://cloud.example.com*') {
        throw "Missing or placeholder value: $name"
    }
}

$baseUrl = $baseUrl.TrimEnd('/')

$loginCandidates = @()
if ($UserId) {
    $loginCandidates += $UserId
} else {
    $loginCandidates += $envMap['NC_MCP_MAIN_USERNAME']
    $loginCandidates += $envMap['NC_MCP_MAIN_ACCOUNT_ID']
}
$loginCandidates = $loginCandidates | Where-Object { -not [string]::IsNullOrWhiteSpace($_) } | Select-Object -Unique
if (-not $loginCandidates) {
    throw 'NC_MCP_MAIN_USERNAME or NC_MCP_MAIN_ACCOUNT_ID is required.'
}

$resolvedLogin = $null
$resolvedUserId = $null
foreach ($candidate in $loginCandidates) {
    $authBytes = [System.Text.Encoding]::UTF8.GetBytes("${candidate}:${password}")
    $authHeader = 'Basic ' + [Convert]::ToBase64String($authBytes)
    $ocsClient = [System.Net.Http.HttpClient]::new()
    $ocsClient.DefaultRequestHeaders.Authorization = [System.Net.Http.Headers.AuthenticationHeaderValue]::Parse($authHeader)
    $ocsClient.DefaultRequestHeaders.Add('OCS-APIRequest', 'true')
    $ocsClient.DefaultRequestHeaders.Accept.ParseAdd('application/json')
    $ocsClient.Timeout = [TimeSpan]::FromSeconds(15)
    try {
        $response = $ocsClient.GetAsync("$baseUrl/ocs/v1.php/cloud/user").GetAwaiter().GetResult()
        if ([int]$response.StatusCode -ne 200) {
            continue
        }
        $body = $response.Content.ReadAsStringAsync().GetAwaiter().GetResult()
        $json = $body | ConvertFrom-Json
        $resolvedLogin = $candidate
        $resolvedUserId = [string]$json.ocs.data.id
        break
    }
    finally {
        $ocsClient.Dispose()
    }
}

if ([string]::IsNullOrWhiteSpace($resolvedLogin) -or [string]::IsNullOrWhiteSpace($resolvedUserId)) {
    throw 'Could not authenticate with NC_MCP_MAIN_USERNAME or NC_MCP_MAIN_ACCOUNT_ID.'
}

$targetDir = '/' + $targetDir.Trim('/')
$timestamp = Get-Date -Format 'yyyyMMdd-HHmmss'
$fileName = "nextcloud-mcp-smoke-$timestamp.txt"
$remotePath = ($targetDir.TrimEnd('/') + '/' + $fileName)
$content = "nextcloud-mcp live upload smoke test`naccount=$($envMap['NC_MCP_MAIN_ACCOUNT_ID'])`nuser=$resolvedUserId`ntime=$(Get-Date -Format o)`n"
$bytes = [System.Text.Encoding]::UTF8.GetBytes($content)

$authBytes = [System.Text.Encoding]::UTF8.GetBytes("${resolvedLogin}:${password}")
$authHeader = 'Basic ' + [Convert]::ToBase64String($authBytes)
$client = [System.Net.Http.HttpClient]::new()
$client.DefaultRequestHeaders.Authorization = [System.Net.Http.Headers.AuthenticationHeaderValue]::Parse($authHeader)
$client.Timeout = [TimeSpan]::FromSeconds(30)

try {
    # Collection paths should include a trailing slash for MKCOL.
    $dirUri = "$baseUrl/remote.php/dav/files/$([uri]::EscapeDataString($resolvedUserId))/$(ConvertTo-WebDavPath $targetDir)/"
    $mkcol = [System.Net.Http.HttpRequestMessage]::new([System.Net.Http.HttpMethod]::new('MKCOL'), $dirUri)
    $mkcolResponse = $client.Send($mkcol)
    $mkcolStatus = [int]$mkcolResponse.StatusCode
    if ($mkcolStatus -notin 201, 405) {
        $body = $mkcolResponse.Content.ReadAsStringAsync().GetAwaiter().GetResult()
        throw "MKCOL failed: HTTP $mkcolStatus $($mkcolResponse.ReasonPhrase) $body"
    }

    $fileUri = "$baseUrl/remote.php/dav/files/$([uri]::EscapeDataString($resolvedUserId))/$(ConvertTo-WebDavPath $remotePath)"
    $put = [System.Net.Http.HttpRequestMessage]::new([System.Net.Http.HttpMethod]::Put, $fileUri)
    $put.Content = [System.Net.Http.ByteArrayContent]::new($bytes)
    $put.Content.Headers.ContentType = [System.Net.Http.Headers.MediaTypeHeaderValue]::Parse('text/plain; charset=utf-8')
    $put.Headers.Add('X-OC-MTime', [DateTimeOffset]::UtcNow.ToUnixTimeSeconds().ToString())
    $putResponse = $client.Send($put)
    $putStatus = [int]$putResponse.StatusCode
    if ($putStatus -notin 200, 201, 204) {
        $body = $putResponse.Content.ReadAsStringAsync().GetAwaiter().GetResult()
        throw "PUT failed: HTTP $putStatus $($putResponse.ReasonPhrase) $body"
    }

    $propfind = [System.Net.Http.HttpRequestMessage]::new([System.Net.Http.HttpMethod]::new('PROPFIND'), $fileUri)
    $propfind.Headers.Add('Depth', '0')
    $propfind.Content = [System.Net.Http.StringContent]::new(
        '<?xml version="1.0"?><d:propfind xmlns:d="DAV:"><d:prop><d:getcontentlength/><d:getetag/><d:getlastmodified/></d:prop></d:propfind>',
        [System.Text.Encoding]::UTF8,
        'application/xml')
    $propfindResponse = $client.Send($propfind)
    $propfindStatus = [int]$propfindResponse.StatusCode
    if ($propfindStatus -ne 207) {
        $body = $propfindResponse.Content.ReadAsStringAsync().GetAwaiter().GetResult()
        throw "PROPFIND failed: HTTP $propfindStatus $($propfindResponse.ReasonPhrase) $body"
    }

    $getResponse = $client.GetAsync($fileUri).GetAwaiter().GetResult()
    $getStatus = [int]$getResponse.StatusCode
    $downloaded = $getResponse.Content.ReadAsStringAsync().GetAwaiter().GetResult()
    if ($getStatus -ne 200) {
        throw "GET failed: HTTP $getStatus $($getResponse.ReasonPhrase)"
    }
    if ($downloaded -ne $content) {
        throw 'Downloaded content did not match uploaded content.'
    }

    [pscustomobject]@{
        Result = 'UPLOAD_VERIFIED'
        AccountId = $envMap['NC_MCP_MAIN_ACCOUNT_ID']
        Login = $resolvedLogin
        UserId = $resolvedUserId
        RemotePath = $remotePath
        Bytes = $bytes.Length
        MkcolHttpStatus = $mkcolStatus
        PutHttpStatus = $putStatus
        PropfindHttpStatus = $propfindStatus
        GetHttpStatus = $getStatus
    } | ConvertTo-Json -Depth 4
}
finally {
    $client.Dispose()
}
