/**
 * Created by apple on 1/13/14.
 */
define(function (require, exports, module) {
    var $ = require('zepto.min.js');
    var IO = require("io.js");
    var Utils = require("utils.js");

    return {
        get: function (callback, timeoutcallback, error){
            new IO({
                data: "action=query&username=username",
                url: "getProductInfo.php",
                timeoutcallback: function (){
                    timeoutcallback && timeoutcallback();
                },
                on: {
                    success: function (data){
                        if(data.bizCode === 1){
                            localStorage.setItem("getProductInfo", JSON.stringify(data.data));
                        }
                        callback && callback.call(this, data);
                    },
                    error: function (){
                        error && error();
                    }
                }
            }).send();
        }
    };
});