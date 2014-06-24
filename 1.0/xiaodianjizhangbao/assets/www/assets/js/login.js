/**
 * Created by apple on 11/24/13.
 */
define(function (require, exports, module) {
    var $ = require('zepto.min.js');
    var AjaxForm = require('ajaxForm.js');
    var Utils = require('utils.js');

    var loginForm = new AjaxForm({
        node: $('.loginBox .form'),
        items: {
            username: {
                min: 2,
                max: 20,
                pattern: "^\.{2,20}$",
                errorMsg: {
                    empty: "请输入用户名",
                    min: "用户名不得少于2个中英文或数字",
                    max: "用户名不得多于20个中英文或数字",
                    pattern: "用户名必须是2到20位中英文或数字"
                }
            },
            password: {
                min: 6,
                max: 50,
                errorMsg: {
                    empty: "请输入登录密码",
                    min: "登录密码不得少于6位",
                    max: "登录密码不得多于50位"
                }
            }
        },
        showErrorMsg: function (msg) {
            Utils.alert(
                msg[0]);
        }
    });
    var callbacks = {
        success: function (data) {
            if (data && data.bizCode === 1 && data.data && data.data.user && data.data.user.user_id) {
                localStorage.setItem("user", JSON.stringify(data.data.user));
                location.href = data.data.redirect;
                $('#J-loginBtn').bind('click', requestLogin).html('登录');
            } else if (data && data.bizCode === 0) {
                var msg = data.data.msg[0] || data.memo;
                Utils.alert(
                    msg);
                $('#J-loginBtn').bind('click', requestLogin).html('登录');
            } else {
                var msg = data.memo;
                Utils.alert(
                    msg);
                $('#J-loginBtn').bind('click', requestLogin).html('登录');
            }
        },
        error: function (data) {
            $('#J-loginBtn').bind('click', requestLogin).html('登录');
        },
        requestLogin: requestLogin
    };

    function requestLogin(e) {
        e.preventDefault();
        loginForm.submit(function () {
            $('#J-loginBtn').unbind().html("正在登录...");
        }, callbacks.success, callbacks.error);
    }

    $('#J-loginBtn').bind('click', requestLogin);

    return callbacks;
});
