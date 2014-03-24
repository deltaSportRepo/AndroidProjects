$SourceServerName = "10.0.0.2"
$SourceContentPath = "\\bin\DSMobileSupport2.2.apk"
$DestinationContentPath = "\\10.0.0.2\Inetpub\MobileAppSupport\Download\Android2.2\"
$cred = Get-Credential # dev.milos sifrazaserver131089

Invoke-Command -ComputerName $SourceServerName -ScriptBlock {param ($SourcePath,$InstallPath, $cred) 
            Copy-Item -Path $SourcePath\* -Destination $InstallPath -Recurse -Credential $cred
        } -ArgumentList $SourceContentPath, $DestinationContentPath, $cred