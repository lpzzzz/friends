/**
 * 添加好友单击事件
 */
$(function () {
    $("#publishBtn").click(addUserCase);
});

function addUserCase() {
    // 将添加好友框隐藏
    $("#publishModal").modal("hide");
    // 获取 添加信息
    var responseId = $("#responseId").val();
    var content = $("#userCaseContent").val();

    /*发送异步请求 */
    $.post(
        CONTEXT_PATH + "/userCase/addUserCase",
        {"responseId": responseId, "content": content},
        function (data) {
            data = $.parseJSON(data);

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

function accept(userCaseId) {

    $.post(
        CONTEXT_PATH + "/userCase/setUserCase",
        {"userCaseId": userCaseId},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                // 在提示框中显示 提示消息
                $("#acceptHintBody").text(data.msg);
                // 显示提示框
                $("#acceptHintModal").modal("show");
                // 在两秒之后隐藏提示框 如果创建成功，刷新页面
                setTimeout(function () {
                    $("#acceptHintModal").modal("hide");
                }, 2000);
            } else {
                $("#acceptHintBody").text(data.msg);
                // 显示提示框
                $("#acceptHintModal").modal("show");
                // 在两秒之后隐藏提示框 如果创建成功，刷新页面
                setTimeout(function () {
                    $("#acceptHintModal").modal("hide");
                }, 2000);
            }
        }
    );
}

function acceptCheer(requestId, requestContent) {
    $.post(
        CONTEXT_PATH + "/userCase/addUserCaseCheerAccept",
        {"requestId":requestId,"requestContent":requestContent},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                // 在提示框中显示 提示消息
                $("#acceptHintBody").text(data.msg);
                // 显示提示框
                $("#acceptHintModal").modal("show");
                // 在两秒之后隐藏提示框 如果创建成功，刷新页面
                setTimeout(function () {
                    $("#acceptHintModal").modal("hide");
                }, 2000);
            } else {
                $("#acceptHintBody").text(data.msg);
                // 显示提示框
                $("#acceptHintModal").modal("show");
                // 在两秒之后隐藏提示框 如果创建成功，刷新页面
                setTimeout(function () {
                    $("#acceptHintModal").modal("hide");
                }, 2000);
            }
        }
    );
}