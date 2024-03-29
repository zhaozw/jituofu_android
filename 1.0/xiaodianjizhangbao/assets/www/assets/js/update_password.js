/**
 * Created with JetBrains PhpStorm.
 * User: praise
 * Date: 9/22/13
 * Time: 9:10 PM
 * To change this template use File | Settings | File Templates.
 */
define(function(require, exports, module) {
    var $ = require('zepto.min.js');
    var IO = require("io.js");
    var Utils = require("utils.js");

    var oldPassword = $('input[name=password]');
    var newPassword = $('input[name=passwordc]');
    var newPasswordCheck = $('input[name=passwordcheck]');

    function validation(){
        var result = true;
        var oldPasswordVal = $.trim(oldPassword.val());
        var newPasswordVal = $.trim(newPassword.val());
        var newPasswordCheckVal = $.trim(newPasswordCheck.val());

        if(!oldPasswordVal){
            Utils.alert('请输入当前登录密码');
            result = false;
        }else if(oldPasswordVal && (oldPasswordVal.length < 6 || oldPasswordVal.length > 50)){
            Utils.alert('当前登录密码不得少于6位,多于50位');
            result = false;
        }else if(!newPasswordVal){
            Utils.alert('请输入新的登录密码');
            result = false;
        }else if(newPasswordVal && (newPasswordVal.length < 6 || newPasswordVal.length > 50)){
            Utils.alert("新的登录密码不得少于6位,多于50位");
            result = false;
        }else if(!newPasswordCheckVal){
            Utils.alert("请再次输入新的登录密码");
            result = false;
        }else if(newPasswordCheckVal && (newPasswordCheckVal !== newPasswordVal)){
            Utils.alert("两次输入的新密码不相等");
            result = false;
        }

        return result;
    }

    function submitSettings(e){
        if(validation()){
            requestSettings();
        }
    }

    function requestSettings(){
        Utils.confirm("确认修改密码？", function (which) {
                var data = "action=update&password="+$.trim(oldPassword.val())+
                    "&passwordc="+$.trim(newPassword.val())+
                    "&passwordcheck="+$.trim(newPasswordCheck.val());
                $('#J-ok').unbind();
                new IO({
                    url: "update_password.php",
                    data: data,
                    on: {
                        start: function (){
                            $('#J-result').html('');
                            Utils.loading.show("正在修改...");
                        },
                        success: function (data){
                            if (data.bizCode === 1) {
                                Utils.loading.warn("修改密码成功，请重新登录");
                                localStorage.setItem("user", JSON.stringify(data.data.user));
                                $('#J-result').html(data.memo);
                                setTimeout(function () {
                                    Utils.loading.hide();
                                    seajs.use("cleaner.js", function (){
                                        seajs.use("utils.js", function (Utils){
                                            location.href = "login.html";
                                        });
                                    })
                                }, 1500);
                            } else {
                                $('#J-result').html(data.memo);
                                Utils.loading.hide();
                            }
                            $('#J-ok').bind('click', function (e){
      e.preventDefault();
      submitSettings();
    });
                        },
                        error: function (){
                            Utils.loading.error("修改密码发生异常，请重试");
                            $('#J-ok').bind('click', function (e){
      e.preventDefault();
      submitSettings();
    });
                            setTimeout(function () {
                                Utils.loading.hide();
                            }, 1500);
                        }
                    }
                }).send();
        });
    }

    $('#J-ok').bind('click', function (e){
      e.preventDefault();
      submitSettings();
    });
});


