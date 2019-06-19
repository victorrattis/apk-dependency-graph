
const fs = require('fs')
const electron = require('electron')
const path = require('path')

const existFile = (path) => fs.existsSync(path)
const getJarDir = (dir) => path.join(dir, "lib/apk-dependency-graph.jar")

const findApkDependencyGraphPath = (appPath) => {
    if (existFile(getJarDir(appPath + ".unpacked"))) {
        return getJarDir(appPath + ".unpacked");
    } else if (existFile(getJarDir(appPath))) {
        return getJarDir(appPath);
    } else {
        return "";
    }
}

class AppStore {
    constructor() {
        const app = (electron.app || electron.remote.app)
        const appDir = app.getAppPath()
        this.userData = app.getPath('userData');
        this.jarPath = findApkDependencyGraphPath(appDir);

        console.log("jarPath: " + this.jarPath);
        console.log("getAnalyzedJsPath: " + this.getAnalyzedJsPath());
        console.log("getOutputPath: " + this.getOutputPath());
    }

    getOutputPath() {
        return `${this.userData}/output/`
    }
    
    getApkDependencyGraphPath() {
        return this.jarPath
    }

    getAnalyzedJsPath() {
        return `${this.userData}/analyzed.js`
    }

    hasApkDependencyGraphJar() {
        return existFile(this.jarPath)
    }
}

module.exports = AppStore
