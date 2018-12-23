package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.BuildStep
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.dotnetBuild
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.dotnetRestore
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.v2018_2.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the buildType with id = 'Build'
accordingly, and delete the patch script.
*/
changeBuildType(RelativeId("Build")) {
    check(buildNumberPattern == "%build.counter%") {
        "Unexpected option value: buildNumberPattern = $buildNumberPattern"
    }
    buildNumberPattern = "1.0.%build.counter%-%teamcity.build.branch%"

    expectSteps {
        dotnetRestore {
            name = "nuget restore"
            projects = "ElectronNetAngular.sln"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
        step {
            name = "npm ci"
            type = "jonnyzzz.npm"
            param("npm_commands", "ci")
            param("teamcity.build.workingDir", "ElectronNetAngular")
        }
        dotnetBuild {
            name = "dotnet build"
            projects = "ElectronNetAngular.sln"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
    }
    steps {
        update<BuildStep>(1) {
            param("teamcity.build.workingDir", "DevActivator")
        }
        insert(3) {
            powerShell {
                name = "electronize"
                workingDir = "DevActivator"
                scriptMode = script {
                    content = "electronize build /target win"
                }
            }
        }
        insert(4) {
            powerShell {
                name = "electron-packager"
                workingDir = "DevActivator/obj/desktop/win"
                scriptMode = script {
                    content = """
                        npm install
                        electron-packager . --platform=win32 --arch=x64  --out="%system.teamcity.build.workingDir%\DevActivator\bin\desktop" --overwrite --electron-version=4.0.0
                    """.trimIndent()
                }
            }
        }
        insert(5) {
            powerShell {
                scriptMode = script {
                    content = """Compress-Archive -Path %system.teamcity.build.workingDir%\DevActivator\obj\desktop\win\DevActivator\bin\desktop\* -CompressionLevel Fastest -DestinationPath %system.teamcity.build.workingDir%\DevActivator\obj\desktop\win\DevActivator\bin\desktop\app.%build.number%.zip"""
                }
            }
        }
    }
}