
const { ipcRenderer } = require('electron');

let selectedFile;

const findById = (id) => document.getElementById(id);

const DRAG_FILE_ID = 'drag-file';
const DRAG_TITLE_ID = 'drag-title';
const BTN_PREVIEW_ID = 'btn-preview';
const INPUT_FILTER_ID = 'input-filter';
const CHECK_INNER_CLASS_ID = 'checkbox-inner-class';

(function () {
    var holder = findById(DRAG_FILE_ID);
    var buttonPreview = findById(BTN_PREVIEW_ID);

    buttonPreview.onclick = clickPreview;

    holder.ondragover = () => {
        return false;
    };

    holder.ondragleave = () => {
        return false;
    };

    holder.ondragend = () => {
        return false;
    };

    holder.ondrop = (e) => {
        e.preventDefault();

        let file = e.dataTransfer.files[0];
        if (file != undefined) {
            selectedFile = file.path;
            findById(DRAG_TITLE_ID).textContent = file.path;
            console.log('File(s) you dragged here: ', file.path)
        }
        
        return false;
    };
})();

const isInnerClass = () => {
    let checkbox = findById(CHECK_INNER_CLASS_ID);
	return checkbox.checked ? true : false;
}

function clickPreview() {
    console.log("click preview");

    if (selectedFile == undefined) {
        alert("Without a selected apk file!");
        return;
    }

    let inputFilter = findById(INPUT_FILTER_ID);
	let isInner = isInnerClass();    

    showPreviewLoading();

	console.log("filter: " + inputFilter.value);
	console.log("isInner: " + isInner);

    ipcRenderer.send(
        'load-apk-dependency-graph', 
        {
            apkFile: selectedFile,
            filter: inputFilter.value,
            isInnerEnabled: isInner
        });
};

function showPreviewLoading() {
	// TODO: Implement this funcionality.
}
