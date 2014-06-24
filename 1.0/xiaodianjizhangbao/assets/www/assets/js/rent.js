/**
 * 录入租金模块
 * User: praise
 * Date: 10/27/13
 * Time: 1:47 PM
 * To change this template use File | Settings | File Templates.
 */
define(function (require, exports, module) {
    var $ = require("zepto.min.js");
    var IO = require("io.js");
    var Utils = require("utils.js");

    var ioType = "post";
    var url = 'rent.php';

    function add(callback) {
        Utils.prompt('请输入当日租金', function (rent){
            rent = $.trim(rent);
            if(rent){
                if (rent.indexOf(".") <= 0 && !/^\d+$/.test(rent)) {
                    Utils.alert(
                        "租金输入错误");
                    return;
                } else if (rent.indexOf(".") !== -1 && !/^\d+\.?\d+$/.test(rent)) {
                    Utils.alert(
                        "租金输入错误");
                    return;
                }
                rent = rent * 1;
                new IO({
                    url: url,
                    data: 'action=add&price=' + rent,
                    on: {
                        success: function (data) {
                            callback && callback.call(callback, data);
                        }
                    }
                }).send();
            }
        });
    }

    return {
        getRange: function (start, end, callback) {
            callback || (callback = function () {
            });
            if (start && end) {
                new IO({
                    url: url,
                    data: "action=query&start=" + start + "&end=" + end + "",
                    on: {
                        success: function (data) {
                            callback.call(callback, data);
                        }
                    }
                }).send();
            }
        },
        add: add
    };
});
