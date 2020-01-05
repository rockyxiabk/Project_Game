###关于gradlew命令

**配置**

 1).Gradle配置

- 基本使用
    - 右侧gradle面板，选择对应的tasks，自定义task在other目录下按照ASCII码排序
    - gradlew clean：清除build目录
    - run app ->edit configurations->gradle add (可以定制添加不同module的task任务)
- 添加系统变量
    - [GRADLE_HOME](变量值为你的gradle插件所在的目录)
    - [PATH](%GRADLE_HOME%\bin)
    - [参考博客地址](https://blog.csdn.net/qq402164452/article/details/70207308)
- task配置
    - [自动编译打包](gradlew.bat assembleRelease(自动打包项目中所有module和每个module的渠道包))
    - [文件复制](gradlew cF或者gradlew copyFile)
    - [文件删除](gradlew deleteResFile或者gradlew dRF)
    - [多任务执行](gradlew dRF cF，表示先执行删除操作，在执行复制操作)
    - [task相关](https://blog.csdn.net/a_ycmbc/article/details/53997067?locationNum=7&fps=1)
 - [gradle构建](https://blog.csdn.net/s402178946/article/details/54140200)

 ###关于打包相关

 **打包流程说明**

   1).配置 项目下的build.gradle 文件下ext->androidAssetsFile->copyToPath（要复制到的文件绝对路径）和assetsFilePath（资源文件绝对路径）
   2).点击右侧Gradle按钮，选择Tasks->other->copyFile(执行复制操作)或者deleteResFile（执行删除操作）
   3).对于如果想精简gameName文件夹，可以删除文件夹下的其他未使用的文件
   4).复制完操作后，选择要运行的module为app_main主项目分支
   5).修改要打包的游戏对应的view_渠道sdk_游戏名，dependencies->**选择
   6).然后修改productFlavors->product_all-applicationId
   7).修改完成后重新编译项目，然后打包build->Generator Signed Bundle/APK
                                        ->选择APK（类型）
                                        ->点击next
                                        ->配置签名文件
                                        ->点击next
                                        ->配置包的输出路径和选择要打包的类型（可多选）
                                        ->finish(开始打包)