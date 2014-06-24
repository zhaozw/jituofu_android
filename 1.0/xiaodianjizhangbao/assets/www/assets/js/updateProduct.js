/**
 * Created with Praise Song(http://labs.cross.hk).
 * User: Praise
 * Date: 13-9-7
 * Time: 上午11:34
 */
//take a photo
define(function (require, exports, module){
    var $ = require('zepto.min.js');
    var type = require('types.js');
    var Utils = require("utils.js");
    var IO = require("io.js");

    var queryString = Utils.queryString2Obj();

    if(queryString && queryString.pageNum){
        var pageNum = queryString.pageNum;
        var id = queryString.id;
        var typeID = queryString.type;

        $('.back').attr("href", $('.back').attr("href")+"?pageNum="+pageNum+"&id="+id+"&type="+typeID);
    }

    function queryProductById(){
        new IO({
            url: "products.php",
            data: "id="+id+"&action=queryById",
            timeoutcallback: function (){
                Utils.loading.hide();
                Utils.confirm(
                    "查询商品超时！",  // message
                    function (which) {
                        queryProductById();
                    },         // callback
                    function (){
                        location.href = $('.back').attr("href");
                    },            // title
                    '重试,返回'                  // buttonName
                );
            },
            on: {
                start: function (){
                    Utils.loading.show("获取商品信息...");
                },
                success: function (data){
                    Utils.loading.hide();
                    if(data.bizCode !== 1){
                        Utils.confirm(
                            data.memo,  // message
                            function (which) {
                                queryProductById();
                            },         // callback
                            function (){
                                location.href = $('.back').attr("href");
                            },            // title
                            '重试,返回'                  // buttonName
                        );
                    }else if(data.data && data.data.product && data.data.product.p_id){
                        $('#name').val(data.data.product.p_name);
                        $('#price').val(data.data.product.p_price);
                        $('.kc').html(data.data.product.p_count);
                    }
                },
                error: function (data){
                    Utils.loading.hide();
                    Utils.confirm(
                        msg,  // message
                        function (which) {
                            queryProductById();
                        },         // callback
                        function (){
                            location.href = $('.back').attr("href");
                        },            // title
                        '重试,返回'                  // buttonName
                    );
                }
            }
        }).send();
    }

    function queryTypes(){
        type.query(function (data){
            if(data.bizCode === 1 && data.data && data.data.types.length >= 1){
                var options = '<option value="">商品分类</option>';
                $.each(data.data.types, function (i, type){
                    var selected = '';
                    if(typeID && (type.id === typeID)){
                        selected = "selected";
                    }
                    options += '<option value="'+type.id+'" '+selected+'>'+type.name+'</option>';
                });
                $('#J-typesSelector').html('<select>'+options+'</select>');
            }
        });
    } 
    function update(){ 
        var pNameNode = $('#name');
        var pPriceNode = $('#price');
        var pCount = $('#count');

        pNameNode.val(pNameNode.val().replace(/\s/g, ''));

        if(!$.trim(pNameNode.val())){
            return Utils.alert("商品名称不能为空！");
        }else if(!$.trim(pPriceNode.val())){
            return Utils.alert("商品价格不能为空！");
        }else if(!$('#J-typesSelector select').val()){
            return Utils.alert("请选择商品分类！");
        }

        function updateIO(){
            $('#J-editProduct-btn').unbind().bind('click', function (e){
                return false;
            }).html("修改中...");
            new IO({
                url: "products.php",
                data: "id="+id+"&action=update&name="+$.trim(pNameNode.val())+"&from=&man=&type="+$('#J-typesSelector select').val()+
                    "&count="+$.trim(pCount.val())+"&price="+$.trim(pPriceNode.val()),
                timeoutcallback: function (){
                    Utils.confirm(
                        "更新商品超时",  // message
                        function (which) {
                            updateIO();
                        },         // callback
                        function (){
                            $('#J-editProduct-btn').unbind().click(update).html("确定");
                        },            // title
                        '重试,取消'                  // buttonName
                    );
                },
                on: {
                    success: function (data){
                        if(data.bizCode !== 1){
                            Utils.confirm(
                                data.memo,  // message
                                function (which) {
                                    updateIO()
                                },         // callback
                                null,            // title
                                '重试,取消'                  // buttonName
                            );
                        }else if(data.data && data.data.product && data.data.product.p_id){
                            Utils.confirm(
                                $.trim(pNameNode.val()) + " 更新成功",  // message
                                function (which) {
                                        $('#name').val(data.data.product.p_name);
                                        $('#price').val(data.data.product.p_price);
                                        $('#count').val("");
                                        $('.kc').html(data.data.product.p_count);
                                        var options = $('#J-typesSelector option');
                                        $.each(options, function (i, option){
                                            if($(option).val() === data.data.product.p_type){
                                                $(option).attr("selected", "selected");
                                                return false;
                                            }
                                        });
                                });
                            $('#J-editProduct-btn').unbind().click(update).html("确定");
                        }
                    },
                    error: function (data){
                        Utils.confirm(
                            msg,  // message
                            function (which) {
                                updateIO()
                            },         // callback
                            null,            // title
                            '重试,取消'                  // buttonName
                        );
                    }
                }
            }).send();
        }
        Utils.confirm(
            "您确认修改 "+pNameNode.val(),  // message
            function (which) {
                updateIO();
            },         // callback
            null,            // title
            '改,取消'                  // buttonName
        );
    }

    queryTypes();
    $('#J-editProduct-btn').click(function (e){
      e.preventDefault();
      update();
    });
    queryProductById();
});



