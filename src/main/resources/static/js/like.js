/**
 * 点赞的方法
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
            if (data.code == 0) {
                // $(btn).children("b").text(data.likeStatus==1?'已赞':'点赞'); //
                window.location.reload();
            } else {
                alert(data.msg);
            }
        }
    );
}