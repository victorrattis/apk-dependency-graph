
const path = require('path')
const { exec } = require('child_process')

const makeDependencyGraphCommand = 
        (appStore, fileName, filter, isInner) => {
    const extension = path.extname(fileName)
    const xpref = path.basename(fileName, extension)

    const jsonPath = appStore.getAnalyzedJsPath();
    const outPath = `${appStore.getOutputPath()}/${xpref}`
    const libPath = appStore.getApkDependencyGraphPath()

    return `java -jar ${libPath} -i ${outPath} -a ${fileName} -o ${jsonPath} -f ${filter} -d ${isInner}`
}

class ApkDependencyGraphRunner {
    constructor(appStore) {
        this.appStore = appStore
    }

    run(config) {
        return new Promise((resolve, reject) => {
            if (!this.appStore.hasApkDependencyGraphJar()) {
                console.log(this.appStore.getApkDependencyGraphPath());
                reject("Without apk-dependency-graph jar!")
                return
            }

            if (!config) {
                reject("Without info to run!")
            }

            if (config.apkFile === undefined) {
                reject("Without an APK file!")
                return
            }

            const command = makeDependencyGraphCommand(
                this.appStore,
                config.apkFile,
                config.filter != undefined ? config.filter : "",
                config.isInnerEnabled)

            if (command == undefined || command === "") {
                reject("Without command to run!")
                return
            }

            exec(command, (error, stdout, stderr) => {
                if (error) return reject(error)
                if (stderr) return reject(stderr)
                resolve()
            })
        })
    }
}

module.exports = ApkDependencyGraphRunner