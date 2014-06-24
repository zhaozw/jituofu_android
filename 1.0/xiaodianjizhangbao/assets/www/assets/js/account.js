/**
 * Created with JetBrains PhpStorm.
 * User: praise
 * Date: 9/22/13
 * Time: 9:10 PM
 * To change this template use File | Settings | File Templates.
 */
define(function (require, exports, module) {
    var $ = require('zepto.min.js');
    var IO = require("io.js");
    var Utils = require("utils.js");

    function getStoreSettings(){
        new IO({
            url: "store_settings.php",
            data: "action=query",
            timeoutcallback: function (){
                Utils.loading.hide();
                Utils.confirm("获取小店信息超时", function (which) {
                    getStoreSettings();
                }, null, "重试,取消");
            },
            on: {
                start: function (){
                    Utils.loading.show("获取小店信息...");
                },
                success: function (data){
                    Utils.loading.hide();
                    if(data.bizCode !== 1){
                        return Utils.confirm(data.memo, function (which) {
                            if (which === 1) {
                                getStoreSettings();
                            }
                        }, "我的小店", "重试,取消");
                    }

                    if(data.data){renderMyStore(JSON.parse(data.data));}else{
                        var data = {tip_rent: "off"};
                        renderMyStore(data);
                    }
                }
            }
        }).send();
    }

    function renderMyStore(data){
        if(tip_rent = data.tip_rent){
            var html = '';
            switch(tip_rent){
                case"on":
                    html = '<div class="switcher" data-status="on" data-role="tip_rent">'+
                        '<div class="on">'+
                        '<span class="trigger"></span>'+
                        '开启'+
                        '</div>'+
                        '</div>';

                    break;
                case "off":
                    html = '<div class="switcher" data-status="off" data-role="tip_rent">'+
                        '<div class="off">'+
                        '<span class="trigger"></span>'+
                        '关闭'+
                        '</div>'+
                        '</div>';
                    break;
            }
            $('#J-tipRentTrigger').append(html);
        }
        bindUItoSwitchers();
    }

    function bindUItoSwitchers(){
        $('#J-switchers .switcher').unbind().bind('click', function (){
            var role = $(this).attr("data-role");
            var status = $(this).attr("data-status");
            var data = {
                role: role,
                status : status === "on" ? "off" : "on"
            };
            changeSwitcherIO(data, $(this));
        });
    }

    var changeSwitcherIOObj = null;
    function changeSwitcherIO(extraData, switcher){
        if(changeSwitcherIOObj && changeSwitcherIOObj.ajaxObj){
            changeSwitcherIOObj.ajaxObj.abort();
        }
        changeSwitcherIOObj = new IO({
            url: "store_settings.php",
            data: "action=update&role="+extraData.role+"&status="+extraData.status,
            timeoutcallback: function (){
                Utils.loading.hide();
                Utils.confirm("处理超时", function (which) {
                    changeSwitcherIO(extraData);
                }, null, "重试,取消");
            },
            on: {
                start: function (){
                    Utils.loading.show(extraData.status === "on" ? "开启中..." : "关闭中...");
                },
                success: function (data){
                    Utils.loading.hide();
                    if(data.bizCode !== 1){
                        return Utils.confirm(data.memo, function (which) {
                            changeSwitcherIO(extraData);
                        }, null, "重试,取消");
                    }

                    var html = '';
                    switch(data.data.status){
                        case"on":
                            html = '<div class="switcher" data-status="on" data-role="'+extraData.role+'">'+
                                '<div class="on">'+
                                '<span class="trigger"></span>'+
                                '开启'+
                                '</div>'+
                                '</div>';

                            break;
                        case "off":
                            html = '<div class="switcher" data-status="off" data-role="'+extraData.role+'">'+
                                '<div class="off">'+
                                '<span class="trigger"></span>'+
                                '关闭'+
                                '</div>'+
                                '</div>';
                            break;
                    }
                    switcher.parent().append(html);
                    switcher.remove();
                    bindUItoSwitchers();
                }
            }
        });
        changeSwitcherIOObj.send();
    }

    getStoreSettings();

    $('#J-storeName').html(user.displayname || user.username);
    $('.username').html(user.username);
    $('.email').html(user.email || "<a href='settings.html' class='J-hideTabBar'>完善我的资料</a>");

    $('#J-logout').bind("click", function () {
        Utils.confirm('确认退出?', function (which) {
            seajs.use("cleaner.js", function () {
                seajs.use("utils.js", function (Utils) {
                    Utils.tip("您已安全退出", 1000);
                    setTimeout(function () {
                        location.href = "login.html";
                    }, 1500);
                });
            })
        }, null, "退出,取消");
    });
});


