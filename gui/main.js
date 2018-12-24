let { app, BrowserWindow, ipcMain } = require('electron');

let mainWindow

const APP_TITLE = "APK Dependency Graph"
const DEV_MODE = false;

function checkEnverinment() {
	console.log("res path: " + process.resourcesPath);
}
checkEnverinment();

function createWindow () {
  const electron = require('electron');
  var screenElectron = electron.screen;
  var mainScreen = screenElectron.getPrimaryDisplay();
  var dimensions = mainScreen.size;

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
  mainWindow.loadFile('home.html')

  // Open the DevTools.
  // mainWindow.webContents.openDevTools()

  // Emitted when the window is closed.
  mainWindow.on('closed', function () {
    // Dereference the window object, usually you would store windows
    // in an array if your app supports multi windows, this is the time
    // when you should delete the corresponding element.
    mainWindow = null
  })
}

// This method will be called when Electron has finished
// initialization and is ready to create browser windows.
// Some APIs can only be used after this event occurs.
app.on('ready', createWindow)

// Quit when all windows are closed.
app.on('window-all-closed', function () {
  // On macOS it is common for applications and their menu bar
  // to stay active until the user quits explicitly with Cmd + Q
  if (process.platform !== 'darwin') {
    app.quit()
  }
})

app.on('activate', function () {
  // On macOS it's common to re-create a window in the app when the
  // dock icon is clicked and there are no other windows open.
  if (mainWindow === null) {
    createWindow()
  }
})

ipcMain.on('open-page-html', (event, file) => {
  // generateApkDependencyGraph();
});

ipcMain.on('load-apk-dependency-graph', (event, config) => {
  console.log(config);
  generateApkDependencyGraph(
    config,
    () => {
      console.log("completed!");
      openPagePreview();
    }, 
    (error) => {
      console.log('exec error: ' + error);
    });
});

// ./run.sh '/home/vhra/Documents/analise-apk/sticky-session.apk' br.org.cesar true

function openPagePreview() {
  const electron = require('electron');
  var screenElectron = electron.screen;
  var mainScreen = screenElectron.getPrimaryDisplay();
  var dimensions = mainScreen.size;

  let page = new BrowserWindow({
    width: dimensions.width, 
    height: dimensions.height
  });
  page.loadFile("index.html");
  page.setMenu(null);

  page.on('closed', function () {
    page = null
  })

  mainWindow.close();
}

var fs = require('fs');

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

function generateApkDependencyGraph(config, onComplete, onError) {
  var exec = require('child_process').exec, child;
  // child = exec('java -jar ~/Applications/example.jar',
  // child = exec("./../run.sh '/home/vhra/Documents/analise-apk/sticky-session.apk' br.org.cesar true",
  let filter = config.filter != undefined ? config.filter : "";

  child = exec(`./../run.sh ${config.apkFile} ${filter} ${config.isInnerEnabled}`,
    function (error, stdout, stderr){
      console.log('stdout: ' + stdout);
      console.log('stderr: ' + stderr);
      if(error !== null){
        onError(error);
        
      } else {
        onComplete();
        // openPagePreview();
      }
  });
}


