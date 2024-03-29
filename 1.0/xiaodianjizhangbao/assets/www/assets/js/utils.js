/**
 * Created with JetBrains PhpStorm.
 * User: praise
 * Date: 10/19/13
 * Time: 11:41 PM
 * To change this template use File | Settings | File Templates.
 */
define(function (require, exports, module) {
    var $ = require('zepto.min.js');

    function _alert(msg, callback){
        var productInfo = JSON.parse(localStorage.getItem("getProductInfo"));
        navigator.notification.alert(
            msg,  // message
            function () {
                callback && callback();
            },         // callback
            productInfo.productname,            // title
            '知道了'                  // buttonName
        );
    }

    return {
        props2Array: function (props) {
            props = props.split("|");
            var result = {};
            $.each(props, function (i, p) {
                if (p) {
                    var prop = p.split(":");
                    var name = prop[0];
                    var val = prop[1]['split'](',');
                    result[name] = val;
                }
            });
            return result;
        },
        loading: {
            show: function (text) {
                window.plugins.waitingDialog.hide();
                window.plugins.waitingDialog.show(text);
//                text = text || "loading";
//                var docH = $(document).height();
//                var winH = $(window).height();
//
//                if ($('body').find(".mask").get(0)) {
//                    $('body').find(".mask").remove();
//                }
//                $('body').append("<div class=\"mask\"></div>");
//                $('.mask').css({
//                    height: Math.max(docH, winH)
//                });
//                if ($('body').find(".loadingBox").get(0)) {
//                    $('body').find(".loadingBox").remove();
//                }
//                $('body').append("<div class=\"loadingBox warn\">" + text + "</div>");
//                $('.loadingBox').css({
//                    left: ($(window).width() - parseInt($('.loadingBox').css("width"), 10)) / 2,
//                    top: (winH - $('.loadingBox').offset().height)/2+window.scrollY
//                });
            },
            updateText: function (text) {
                window.plugins.waitingDialog.show(text);
//                $('.loadingBox').html(text);
            },
            error: function (text) {
//                $('.loadingBox').removeClass("warn").addClass("error").html(text);
                _alert(text);
            },
            warn: function (text) {
//                $('.loadingBox').removeClass("error").addClass("warn").html(text);
                _alert(text);
            },
            hide: function () {
//                $('.mask').hide();
//                $('.loadingBox').hide();
                window.plugins.waitingDialog.hide();
            }
        },
        //强制将小于10的数字转换为2位数字，如01-09
        to2Num: function (num) {
            if (num < 10) {
                num = '0' + num;
            }
            return num;
        },
        queryString2Obj: function () {
            var queryString = location.href;
            if (queryString.indexOf("?") < 0) {
                return false;
            }

            queryString = queryString.split("?");

            if (!queryString[1]) {
                return false;
            }

            queryString = queryString[1]['split']("&");
            var result = {};
            if (queryString.length < 2) {
                var data = queryString[0]['split']("=");
                if (!data[1]) {
                    data[1] = false;
                }
                result[data[0]] = data[1];
            } else {
                $.each(queryString, function (i, str) {
                    var data = str.split("=");
                    if (!data[1]) {
                        data[1] = false;
                    }
                    result[data[0]] = data[1];
                });
            }

            return result;
        },
        network: function () {
            var networkState = navigator.network.connection.type;

            var states = {};
            states[Connection.UNKNOWN] = false;
            states[Connection.ETHERNET] = 'Ethernet';
            states[Connection.WIFI] = 'wifi';
            states[Connection.CELL_2G] = '2G';
            states[Connection.CELL_3G] = '3G';
            states[Connection.CELL_4G] = '4G';
            states[Connection.NONE] = false;

            return states[networkState];
        },
        tip: function (tip, time){
            var time = time || 5000;
            if($.tipTimer){
                clearTimeout($.tipTimer);
            }
            var html = "<div class='globalTip' id='J-globalTip'>"+tip+"</div>";
            var mask = '<div class="globalTipMask"></div>';
            if($('#J-globalTip').get(0)){
                $('#J-globalTip').remove();
                $('.globalTipMask').remove();
            }
            $('body').append(mask);
            $('body').append(html);
            var winH = $(window).height();
            var winW = $(window).width();
            var containerW = parseInt($('#J-globalTip').offset().width, 10);
            var containerH = parseInt($('#J-globalTip').css("height"), 10);

            $('#J-globalTip').css({
                left: (winW - containerW) / 2,
                top: (winH - containerH) / 2 + window.scrollY,
                opacity: 1
            });
            $('.globalTipMask').css({
                opacity: 1,
                height: Math.max($(document).height(), winH)
            });
            $.tipTimer = setTimeout(function (){
                $('#J-globalTip').remove();
                $('.globalTipMask').remove();
            }, time);
        },
        alert: function (msg, callback){
            _alert(msg, callback);
        },
        confirm: function (msg, firstCallback, secondCallback, btns){
            var productInfo = JSON.parse(localStorage.getItem("getProductInfo"));
            var btns = btns || "确认,取消";
            var msg = msg || "";
            var firstCallback = firstCallback || function (){};
            var secondCallback = secondCallback || function (){};

            navigator.notification.confirm(msg, function (which) {
                if(which === 1){
                    firstCallback();
                }else if(which === 2){
                    secondCallback();
                }
            }, productInfo.productname, btns);
        },
        downloadUpdate: function (){
            window.open('http://115.29.39.106/download/xiaodianjizhangbao.apk', '_system', 'location=yes');
            navigator.app.exitApp();
        },
        prompt: function (msg, firstCallback, secondCallback, btns, defaultTxt){
            var productInfo = JSON.parse(localStorage.getItem("getProductInfo"));
            var btns = btns || ['确认','取消'];
            var defaultTxt = defaultTxt || ' ';
            var firstCallback = firstCallback || function (){};
            var secondCallback = secondCallback || function (){};
            navigator.notification.prompt(
                msg,  // message
                function (results){
                    if(results.buttonIndex === 1){
                        firstCallback && firstCallback.call(firstCallback, results.input1);
                    }else if(results.buttonIndex === 2){
                        secondCallback && secondCallback.call(secondCallback);
                    }
                },                  // callback to invoke
                productInfo.productname,            // title
                btns,             // buttonLabels
                defaultTxt                // defaultText
            );
        }
    };
});