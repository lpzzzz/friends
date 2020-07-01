/**
 *
 * @param btn
 * @param entityType
 * @param entityId
 * @param entityUserId
 * @param cheerUserId
 * @param likedId
 */
function like(btn, entityType, entityId, entityUserId, cheerUserId, likedId) {
    $.post(
        CONTEXT_PATH + "/like",
        {
            "entityType": entityType, "entityId": entityId,
            "entityUserId": entityUserId, "cheerUserId": cheerUserId, "likedId": likedId
        },
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) { //加油成功
                window.location.reload();
            } else {
                alert(data.msg);
            }
        }
    );
}

$(function () {
    $("#publishCheerBtn").click(publishCheerBtn);
});

function publishCheerBtn() {
    // 获取 输入的内容
    var sendWord = $("#sendWord").val();
    // 获取选中的用户的id
    var resopnseId = $("#responseId option:selected").val();

    // 发送异步请求
    $.post(
        CONTEXT_PATH + "/userCase/addUserCaseCheer",
        {"sendWord": sendWord, "responseId": resopnseId},
        function (data) {
            data = $.parseJSON(data); // 将JSON字符串转换为 JS 对象

            if (data.code == 0) {
                // 在提示框中显示 提示消息
                $("#hintBody").text(data.msg);
                // 显示提示框
                $("#hintModal").modal("show");
                // 在两秒之后隐藏提示框 如果创建成功，刷新页面
                setTimeout(function () {
                    $("#hintModal").modal("hide");
                }, 3000);
            } else { // 如果失败! 返回错误提示信息
                // 在提示框中显示 提示消息
                $("#hintBody").text(data.msg);
                // 显示提示框
                $("#hintModal").modal("show");
                // 在两秒之后隐藏提示框 如果创建成功，刷新页面
                setTimeout(function () {
                    $("#hintModal").modal("hide");
                }, 3000);
            }
        }
    );
}