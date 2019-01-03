
let { app, BrowserWindow, ipcMain } = require('electron');
let fs = require('fs');
const path = require('path');

let mainWindow;

const APP_TITLE = "APK Dependency Graph"
const DEV_MODE = false;

function createWindow () {
  	// Create the browser window.
  	mainWindow = new BrowserWindow({
    	width: 360, 
    	height: 380, 
    	title: APP_TITLE,
    	resizable: DEV_MODE,
    	useContentSize: true
  	});

  	if (!DEV_MODE) {
  		// Disable menu bar.
  		mainWindow.setMenu(null);
  	}

  	// Load the index.html of the app.
  	mainWindow.loadFile('home.html');

  	// Emitted when the window is closed.
  	mainWindow.on('closed', function () {
    	mainWindow = null;
  	});
}

app.on('ready', createWindow);

app.on('window-all-closed', function () {
  	// On macOS it is common for applications and their menu bar
  	// to stay active until the user quits explicitly with Cmd + Q
  	if (process.platform !== 'darwin') {
    	app.quit();
  	}
});

app.on('activate', function () {
  	// On macOS it's common to re-create a window in the app when the
  	// dock icon is clicked and there are no other windows open.
  	if (mainWindow === null) {
    	createWindow();
  	}
});

ipcMain.on('load-apk-dependency-graph', (event, config) => {
	console.log(config);
  	runDependencyGraph(
  		config,
  		() => {
      		console.log("completed!");
      		openDependecyGraphPreview();
    	}, 
    	(error) => {
      		console.log('exec error: ' + error);
    	});
});

function openDependecyGraphPreview() {
	const { screen } = require('electron');
	let mainScreen = screen.getPrimaryDisplay();
	let dimensions = mainScreen.size;

	let page = new BrowserWindow({
    	width: dimensions.width, 
    	height: dimensions.height
  	});
  	page.loadFile("index.html");
  	page.setMenu(null);

  	page.on('closed', function () {
  		console.log("close page preview!");
    	page = null
  	});

  	mainWindow.close();
}

function getApkDenpendencyLibPath() {
    let currentDir = process.resourcesPath;
    if (fs.existsSync(`./run.sh`)) {
        return "./run.sh";
    } if (fs.existsSync(`${currentDir}/app/run.sh`)) {
        console.log("run.sh exists in the app resources.");
        return `${currentDir}/app/run.sh`;
    } else {
        console.log("File not found!");
        return ""; 
    }   
}

const hasApkDependencyGraphLib = () => {
	return fs.existsSync(getApkDependencyAnalizerPath());
}

const getApkDependencyAnalizerPath = () => {
    return path.join(__dirname, "lib/apk-dependency-graph.jar");
}

const runDependencyGraph = (config, onComplete, onError) => {
    if (!hasApkDependencyGraphLib()) {
        onError("Without apk-dependency-graph jar!");
        return;
    }

	let command = makeDependencyGraphCommand(
			config.apkFile,
			config.filter != undefined ? config.filter : "",
			config.isInnerEnabled);

	console.log(command);
	runCommand(command, onComplete, onError);
}

const makeDependencyGraphCommand = (fileName, filter, isInner) => {
	let extension = path.extname(fileName);
	let xpref = path.basename(fileName, extension);

	let dir = __dirname;
	let jsonPath = `${dir}/analyzed.js`;
	let outPath = `${dir}/output/${xpref}`;
	let libPath = getApkDependencyAnalizerPath();

	return `java -jar ${libPath} -i ${outPath} -a ${fileName} -o ${jsonPath} -f ${filter} -d ${isInner}`;
}

const runCommand = (command, onComplete, onError) => {
	if (command == undefined || command === "") {
		onError("Without command to run!");
		return;
	}

	let { exec } = require('child_process');
	exec(command, (error, stdout, stderr) => {
			console.log('stdout: ' + stdout);
      		console.log('stderr: ' + stderr);
      		if(error !== null){
        		onError(error);
      		} else {
        		onComplete();
      		}
		});
}

