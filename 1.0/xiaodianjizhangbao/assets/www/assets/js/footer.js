/**
 * Created with JetBrains PhpStorm.
 * User: praise
 * Date: 10/19/13
 * Time: 11:41 PM
 * To change this template use File | Settings | File Templates.
 */
define(function (require, exports, module){
    var $ = require('zepto.min.js');
    var Utils = require("utils.js");

    var backs = $('.header .back');
    if(backs.length >= 1){
        $.each(backs, function (i, back){
            if(!$(back).attr("data-norouting")){
                if(history.length < 2){
                    $(back).attr("href", "cashier.html");
                }else{
                    $(back).attr("href", "javascript:void(0)");
                    $(back).unbind().bind("click", function (){
                        history.back();
                    });
                }
            }
        });
    }

    document.addEventListener("backbutton", onBackKeyDown, false);

    function onBackKeyDown(){
        if(history.length <= 2 || !localStorage.getItem('user')){
            Utils.confirm("确认退出？", function (){
                navigator.app.exitApp();
            })  ;
        }else{
            history.back();
        }
    }
});