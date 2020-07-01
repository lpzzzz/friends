/*实现上传时显示图片开始*/
var CONTEXT_PATH = "/friends"; // 定义全局变量

// 打开file表单，选择图片
function chooseImageFile(inputFileId) {
    $("#" + inputFileId).click();
}

var fileReader = new FileReader();
regexImageFile = /^(?:image\/bmp|image\/cis\-cod|image\/gif|image\/ief|image\/jpeg|image\/jpeg|image\/jpeg|image\/pipeg|image\/png|image\/svg\+xml|image\/tiff|image\/x\-cmu\-raster|image\/x\-cmx|image\/x\-icon|image\/x\-portable\-anymap|image\/x\-portable\-bitmap|image\/x\-portable\-graymap|image\/x\-portable\-pixmap|image\/x\-rgb|image\/x\-xbitmap|image\/x\-xpixmap|image\/x\-xwindowdump)$/i;

function showImgToView(inputFileId) {
    // 选择图片文件
    var imgFile = $("#" + inputFileId).get(0).files[0];
    // 判断上传文件是否为图片格式
    if (!regexImageFile.test(imgFile.type)) {
        alert("请选择有效的图片!");
        return;
    } else {
        // 将文件读取到DataUrl
        fileReader.readAsDataURL(imgFile);
        //readAsDataURL()方法是FileReader对象里面的方法，其作用为可以获取API异步读取的文件数据，
        // 将图片另存为数据Url,还可以实现图片上传预览的效果，让用户确认是否就是上传这张图片，提升用户体验。
    }
}

// 读取文件
fileReader.onload = function (ev) {
    // 将该URL绑定到img标签的Src属性上，就可以实现图片上传预览效果
    $("#IsImgEmployeePicture").attr("src", ev.target.result);
    // alert(ev.target.result);
};

/*实现上传时显示图片结束*/

/*创建活动弹出框JS*/
$(function () {
    $("#publishBtn").click(publish);
});

function publish() {
    // 将发布框关闭
    $("#publishModal").modal("hide");
    // 获取标题和描述
    var title = $("#recipient-name").val(); // 获取标题框中的值
    var content = $("#message-text").val(); // 获取内容框中的值
    var endDate = $("#endDate").val(); // 获取活动的结束时间

    $.post(
        CONTEXT_PATH + "/activity/add",
        {"endDate": endDate, "title": title, "content": content},
        function (data) {
            data = $.parseJSON(data); // 将JSON字符串转换为 JS 对象
            // 在提示框中显示 提示消息
            $("#hintBody").text(data.msg);
            // 显示提示框
            $("#hintModal").modal("show");
            // 在两秒之后隐藏提示框 如果创建成功，刷新页面
            setTimeout(function () {
                $("#hintModal").modal("hide");
                if (data.code == 0) {
                    window.location.reload();
                }
            }, 2000);
        }
    );
}