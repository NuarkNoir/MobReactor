cd C:\Users\minec\Documents\GitHub\TrashApps\
$BuildTime = (Get-Date).ToString('dd.MM.yy-a\t-HH.mm')
$projectname = ($MyInvocation.MyCommand.Definition | split-path -parent).Split("\")
$projectname = $projectname[$projectname.Count-1]
$filename = $projectname + "-DEBUG-" + $BuildTime
write-host ***********************************************
write-host Project builder wrapper for gradlew v. 1.4 beta
write-host Made by Nuark aka Nuark.Noir
write-host *********************************************** 
write-host Bulding project: $projectname
write-host Date: (Get-Date).ToString('dd.MM.yy HH:mm')
write-host *********************************************** 
[string]$service = $(read-host "Install generated apk on device[y/n]?")
cls
write-host Starting gradle wrapper...
if ($service.ToLower().Equals("y")) {
.\gradlew assembleDebug installDebug | Out-Host
} 
else {
.\gradlew assembleDebug | Out-Host
}
pause
cls
Copy-Item -Path ".\app\build\outputs\apk\debug\app-debug.apk" -Destination (".\" + $filename + ".apk")
write-host Output can be found at .\$filename
pause