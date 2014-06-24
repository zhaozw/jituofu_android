/**
 * Created with JetBrains PhpStorm.
 * User: praise
 * Date: 11/2/13
 * Time: 11:16 AM
 * To change this template use File | Settings | File Templates.
 */
define(function (require, exports, module){
    var $ = require('zepto.min.js');
    var IO = require('io.js');
    var Utils = require("utils.js");

    var api = 'types.php';

    function addType(name, callback, timeoutcallback){
        var self = this;
        var io = new IO({
            url: api,
            data: "action=add&name="+encodeURI(name),
            timeoutcallback: function (){
                Utils.alert(
                    "处理超时，请重试",  // message
                    function () {
                        timeoutcallback && timeoutcallback.call(this);
                    });
            },
            on: {
                success: function (data){
			       if(data.bizCode === 1 && data.data && data.data.id){
			         localStorage.removeItem("types");
                   }else{
                       if(data.bizCode !== 2){
                           Utils.alert(
                               data.memo);
                       }
                   }
				   callback && callback.call(callback, data);
                },
                error: function (data){
                    Utils.alert(
                        data.memo);
                }
            }
        });
        io.send();
        return io;
    }

    function queryTypes(callback, timeoutcallback){
        if(types = localStorage.getItem("types")){
            types = JSON.parse(types);
            callback && callback.call(callback, types);
            return false;
        }
        new IO({
            url: api,
            data: "action=query",
            timeoutcallback: function (){
                timeoutcallback && timeoutcallback.call(this);
            },
            on: {
                success: function (data){
                    if(data.bizCode === 1 && data.data && data.data.types.length >= 1){
                        sava2local(data);
                    }
                    callback && callback.call(callback, data);
                    return false;
                }
            }
        }).send();
    }

    function sava2local(data){
        localStorage.setItem("types", JSON.stringify(data));
    }

    return {
        add: addType,
        query: queryTypes,
        clear: function(){
            localStorage.removeItem("types");
        }
    };
});