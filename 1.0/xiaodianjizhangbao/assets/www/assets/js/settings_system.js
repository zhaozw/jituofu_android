define(function (require, exports, module){
    var $ = require('zepto.min.js');
    var Utils = require("utils.js");

    $('#J-version').html('v'+localStorage.getItem("version"));
    $('#J-productname').html(JSON.parse(localStorage.getItem("getProductInfo"))['productname']);

    $('#J-getVersion').bind("click", function (e){
        e.preventDefault();
        getVersion();
    });

    $('#J-comment').bind("click", function (e){
        e.preventDefault();
        window.plugins.comment.show();
    });

    function getVersion(){
        Utils.loading.show("正在检查新版本");
        seajs.use("getProductInfo.js", function (PI){
            PI.get(function (data){
                if(data.bizCode === 1 && (localStorage.getItem('version') != data.data.android_version)){
                    Utils.loading.hide();
                    Utils.confirm("发现新版本,是否需要更新？", function (which) {
                            Utils.downloadUpdate();
                    }, null, "更新,取消");
                }else if(data.bizCode === 1 && (localStorage.getItem('version') == data.data.android_version)){
                    Utils.loading.hide();
                    Utils.alert(
                        "没有更新的版本");
                }else if(data.bizCode !== 1){
                    Utils.loading.warn(data.memo);
                    setTimeout(function () {
                        Utils.loading.hide();
                    }, 3000);
                }
            }, function (){
                Utils.loading.hide();
                Utils.alert(
                    "查询超时");
            }, function (){
                Utils.loading.hide();
            })
           }
        )
    }
});